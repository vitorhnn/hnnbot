package br.net.hnn.discordbot.command

import br.net.hnn.discordbot.CommandDispatcher
import br.net.hnn.discordbot.lavabridge.ResultHandler
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class PlayCommand(val bot: CommandDispatcher) extends Command {
  override def name: String = "play"

  override def apply(event: GuildMessageReceivedEvent, args: Array[String]): Unit = {
    val guild = event.getGuild

    val maybeVoiceChannel = Option(event.getMember)
      .flatMap(member => Option(member.getVoiceState))
      .flatMap(voiceState => Option(voiceState.getChannel))

    val voiceChannel = maybeVoiceChannel match {
      case Some(voiceChannel) => voiceChannel
      case None => return
    }

    bot.globalMusicManager.loadFromUrl(guild, voiceChannel, args(1), new ResultHandler {
      override def trackLoaded(track: AudioTrack): Unit = {
        event.getChannel.sendMessage(s"Loaded track ${track.getInfo.title}").queue()
      }
    })
  }
}

