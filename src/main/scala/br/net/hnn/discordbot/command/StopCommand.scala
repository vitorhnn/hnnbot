package br.net.hnn.discordbot.command

import br.net.hnn.discordbot.CommandDispatcher
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class StopCommand(val bot: CommandDispatcher) extends Command {
  override def name: String = "stop"

  override def apply(event: GuildMessageReceivedEvent, args: Array[String]): Unit = {
    val guild = event.getGuild

    bot.globalMusicManager.disconnect(guild)

    event.getChannel.sendMessage("Leaving channel, goodbye!").queue()
  }
}
