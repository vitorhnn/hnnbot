package br.net.hnn.discordbot.lavabridge

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}


class ResultHandler extends AudioLoadResultHandler {
  override def trackLoaded(track: AudioTrack): Unit = ()

  override def playlistLoaded(playlist: AudioPlaylist): Unit = ()

  override def noMatches(): Unit = ()

  override def loadFailed(exception: FriendlyException): Unit = ()
}
