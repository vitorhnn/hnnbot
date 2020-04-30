package br.net.hnn.discordbot.music

import java.nio.ByteBuffer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler

class AudioPlayerSendHandler(var audioPlayer: AudioPlayer)
  extends AudioSendHandler {

  private val buffer = ByteBuffer.allocate(1024)
  private val frame = new MutableAudioFrame

  this.frame.setBuffer(this.buffer)

  override def canProvide: Boolean = this.audioPlayer.provide(frame)

  override def provide20MsAudio: ByteBuffer = {
    this.buffer.flip()

    this.buffer
  }

  override def isOpus: Boolean = true
}
