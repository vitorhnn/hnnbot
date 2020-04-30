package br.net.hnn.discordbot.spotify

import java.time.Instant

import br.net.hnn.discordbot.spotify.SpotifyAPIWrapper.SpotifyToken
import org.json4s.FieldSerializer._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import sttp.client._
import sttp.client.httpclient.{HttpClientFutureBackend, WebSocketHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Wrapper around the Spotify API (https://developer.spotify.com/documentation/web-api/). Extremely hacky and ad-hoc.
 */
object SpotifyAPIWrapper {
  private val SPOTIFY_AUTH_URL = "https://accounts.spotify.com"
  private val SPOTIFY_API_URL = "https://api.spotify.com"

  private class SpotifyToken(var bearerToken: String, val expiresIn: Int) {
    val expiresAt: Instant = Instant.now.plusSeconds(expiresIn)
  }

}


class SpotifyAPIWrapper(val spotifyId: String, val spotifySecret: String) {
  private var spotifyToken: Option[SpotifyToken] = Option.empty

  implicit private val backend: SttpBackend[Future, Nothing, WebSocketHandler] = HttpClientFutureBackend()

  implicit val formats: Formats = DefaultFormats + FieldSerializer[ClientCredentials](
    Map(),
    renameFrom("access_token", "accessToken")
      orElse renameFrom("token_type", "tokenType")
      orElse renameFrom("expires_in", "expiresIn")
  )

  def getAlbum(albumId: String): Future[Either[String, Album]] = {
    // This is terrible and it seems I should use Cats?
    this.maybeRefreshToken.flatMap {
      case Left(lv) => Future.successful(Left(lv))
      case Right(token) =>
        basicRequest
          .auth.bearer(token.bearerToken)
          .get(uri"${SpotifyAPIWrapper.SPOTIFY_API_URL}/v1/albums/$albumId")
          .send()
          .map(_.body)
          .flatMap {
            case Left(lv) => Future.successful(Left(lv))
            case Right(res) => Future.successful(Right(parse(res).extract[Album]))
          }
    }
  }

  /**
   * Refreshes the Spotify token if it is empty or if it has expired
   */
  private def maybeRefreshToken: Future[Either[String, SpotifyToken]] = { // TODO: probably rewrite in a more functional fashion
    this.spotifyToken match {
      case Some(token) =>
        if (token.expiresAt.isBefore(Instant.now)) {
          this.refreshAccessToken
        }

        Future.successful(Right(token))
      case None => this.refreshAccessToken
    }
  }

  private def refreshAccessToken: Future[Either[String, SpotifyToken]] = {
    val req = basicRequest
      .auth.basic(this.spotifyId, this.spotifySecret)
      .body(("grant_type", "client_credentials"))
      .post(uri"${SpotifyAPIWrapper.SPOTIFY_AUTH_URL}/api/token")

    req.send()
      .map(_.body)
      .map(_.map(parse(_).extract[ClientCredentials]))
      .map(_.map(credentialsResponse => {
        val token = new SpotifyAPIWrapper.SpotifyToken(credentialsResponse.accessToken, credentialsResponse.expiresIn)
        this.spotifyToken = Some(token)
        token
      }))
  }
}
