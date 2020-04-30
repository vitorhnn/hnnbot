package br.net.hnn.discordbot

import br.net.hnn.discordbot.command.Command
import br.net.hnn.discordbot.music.GlobalMusicManager
import br.net.hnn.discordbot.spotify.SpotifyAPIWrapper
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

import scala.collection.mutable

class CommandDispatcher(val spotifyApi: SpotifyAPIWrapper) extends ListenerAdapter {
  val commandMap = new mutable.HashMap[String, Command]
  val globalMusicManager = new GlobalMusicManager

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    val split = event.getMessage.getContentRaw.split(" ")

    if (split(0).startsWith("!")) {
      val commandName = split(0).substring(1)
      val maybeCommand = this.commandMap.get(commandName)

      maybeCommand match {
        case Some(command) => command(event, split)
        case None => ()
      }
    }
    super.onGuildMessageReceived(event)
  }

  override def onGuildVoiceLeave(event: GuildVoiceLeaveEvent): Unit = {
    globalMusicManager.leaveIfEmpty(event.getChannelLeft)
  }

  def registerCommand(command: Command): Unit = {
    if (commandMap.contains(command.name)) {
      throw new IllegalArgumentException(s"There already exists a command with name ${command.name}")
    }

    commandMap.put(command.name, command)
  }
}