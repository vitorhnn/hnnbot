package br.net.hnn.discordbot.music

import br.net.hnn.discordbot.lavabridge.ResultHandler
import com.sedmelluq.discord.lavaplayer.player.{AudioLoadResultHandler, DefaultAudioPlayerManager}
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}
import net.dv8tion.jda.api.entities.{Guild, VoiceChannel}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Handles all GuildMusicManagers and responds to messages
 */
class GlobalMusicManager() {
  val musicManagers = new mutable.HashMap[Long, GuildMusicManager]
  val playerManager = new DefaultAudioPlayerManager

  AudioSourceManagers.registerLocalSource(playerManager)
  AudioSourceManagers.registerRemoteSources(playerManager)

  def loadFromUrl(guild: Guild, voiceChannel: VoiceChannel, trackUrl: String, audioLoadResultHandler: AudioLoadResultHandler): Unit = {
    val musicManager = getGuildAudioPlayer(guild)
    playerManager.loadItemOrdered(musicManager, trackUrl, new ResultHandler {
      override def trackLoaded(track: AudioTrack): Unit = {
        audioLoadResultHandler.trackLoaded(track)
        connectToVoiceChannel(guild, voiceChannel)
        enqueueTrack(musicManager, track)
      }

      override def playlistLoaded(playlist: AudioPlaylist): Unit = {
        audioLoadResultHandler.playlistLoaded(playlist)
        connectToVoiceChannel(guild, voiceChannel)
        if (playlist.isSearchResult) {
          // we're just enqueing the first search result, which may
          // not be a great idea
          enqueueTrack(musicManager, playlist.getTracks.get(0))
        } else {
          for (track <- playlist.getTracks.asScala) {
            enqueueTrack(musicManager, track)
          }
        }
      }
    })
  }

  def loadTrack(guild: Guild, voiceChannel: VoiceChannel, track: AudioTrack): Unit = {
    val musicManager = getGuildAudioPlayer(guild)
    connectToVoiceChannel(guild, voiceChannel)
    enqueueTrack(musicManager, track)
  }

  def disconnect(guild: Guild): Unit = {
    val musicManager = getGuildAudioPlayer(guild)
    musicManager.scheduler.stop()

    guild.getAudioManager.closeAudioConnection()
  }

  def leaveIfEmpty(channel: VoiceChannel): Unit = {
    val guild = channel.getGuild

    if (guild.getAudioManager.getConnectedChannel == channel) {
      if (channel.getMembers.size() == 1) {
        disconnect(guild)
      }
    }
  }

  private def getGuildAudioPlayer(guild: Guild): GuildMusicManager = {
    val guildId = guild.getIdLong

    val musicManager = musicManagers.getOrElseUpdate(guildId, new GuildMusicManager(this.playerManager))
    guild.getAudioManager.setSendingHandler(musicManager.getSendHandler)
    musicManager
  }

  private def connectToVoiceChannel(guild: Guild, channel: VoiceChannel): Unit = guild.getAudioManager.openAudioConnection(channel)

  private def enqueueTrack(musicManager: GuildMusicManager, track: AudioTrack): Unit = musicManager.scheduler.queue(track)

  def skipTrack(guild: Guild): Unit = {
    val musicManager = getGuildAudioPlayer(guild)
    musicManager.scheduler.nextTrack()
  }
}