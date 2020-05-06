package br.net.hnn.discordbot.music

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.{AudioTrack, AudioTrackEndReason}

class TrackScheduler(var player: AudioPlayer) extends AudioEventAdapter {
  var queue: BlockingQueue[AudioTrack] = new LinkedBlockingQueue[AudioTrack]

  def enqueue(track: AudioTrack): Unit = {
    if (!player.startTrack(track, true)) {
      queue.offer(track)
    }
  }

  def clearQueue(): Unit = {
    queue.clear()
  }

  override def onTrackEnd(player: AudioPlayer,
                          track: AudioTrack,
                          endReason: AudioTrackEndReason): Unit = {
    if (endReason.mayStartNext) {
      this.nextTrack()
    }
  }

  def nextTrack(): Unit = player.startTrack(queue.poll(), false)

  def stop(): Unit = player.stopTrack()
}
