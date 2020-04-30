package br.net.hnn.discordbot.command

import br.net.hnn.discordbot.CommandDispatcher
import br.net.hnn.discordbot.lavabridge.ResultHandler
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class SpotifyAlbumCommand(val bot: CommandDispatcher) extends Command {
  override def name: String = "spotifyAlbum"

  override def apply(event: GuildMessageReceivedEvent, args: Array[String]): Unit = {
    val albumId = args(1)

    val guild = event.getGuild

    val maybeVoiceChannel = Option(event.getMember)
      .flatMap(member => Option(member.getVoiceState))
      .flatMap(voiceState => Option(voiceState.getChannel))

    val voiceChannel = maybeVoiceChannel match {
      case Some(voiceChannel) => voiceChannel
      case None => return
    }

    this.bot.spotifyApi.getAlbum(albumId)
      .flatMap {
        case Left(lv) => Future.successful(Left(lv))
        case Right(album) =>
          event.getChannel.sendMessage(album.tracks.items.foldRight("")((track, acc) => s"${track.name} $acc\n")).queue()

          Future.sequence(album.tracks.items.map(track => {
            var topYtTrack: Option[AudioTrack] = None
            val future = this.bot.globalMusicManager.playerManager.loadItem(s"ytsearch:${album.artists.head.name} - ${track.name}", new ResultHandler {
              override def playlistLoaded(playlist: AudioPlaylist): Unit = {
                topYtTrack = Some(playlist.getTracks.get(0))
              }
            })

            // I'm fairly certain this blocks, but LavaPlayer gives us an old Java Future[T] here
            Future {
              future.get()
              topYtTrack.get
            }
          })).map(Right(_))
      }
      .andThen {
        case Success(tracks) =>
          for (track <- tracks.getOrElse(Seq.empty)) {
            this.bot.globalMusicManager.loadTrack(guild, voiceChannel, track)
          }
      }
  }
}
