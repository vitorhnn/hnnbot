package br.net.hnn.discordbot.music

import com.sedmelluq.discord.lavaplayer.player.{AudioPlayer, AudioPlayerManager}

class GuildMusicManager(manager: AudioPlayerManager) {
  val player: AudioPlayer = manager.createPlayer()

  val scheduler = new TrackScheduler(this.player)

  player.addListener(this.scheduler)

  def getSendHandler: AudioPlayerSendHandler =
    new AudioPlayerSendHandler(this.player)
}
