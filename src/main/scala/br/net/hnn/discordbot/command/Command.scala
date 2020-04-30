package br.net.hnn.discordbot.command

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

trait Command {
  def name: String

  def apply(event: GuildMessageReceivedEvent, args: Array[String]): Unit
}
