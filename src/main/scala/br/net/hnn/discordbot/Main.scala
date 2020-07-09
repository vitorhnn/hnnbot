package br.net.hnn.discordbot

import br.net.hnn.discordbot.command.{PlayCommand, SpotifyAlbumCommand, StopCommand}
import br.net.hnn.discordbot.spotify.SpotifyAPIWrapper
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.{AccountType, JDABuilder}

object Main {
  def main(args: Array[String]): Unit = {
    val dotenv = Dotenv.configure().ignoreIfMissing().load()

    val bot = new CommandDispatcher(new SpotifyAPIWrapper(dotenv.get("SPOTIFY_ID"), dotenv.get("SPOTIFY_SECRET")))

    bot.registerCommand(new PlayCommand(bot))
    bot.registerCommand(new SpotifyAlbumCommand(bot))
    bot.registerCommand(new StopCommand(bot))
    bot.registerCommand(new BoaNoiteCommand(bot))

    new JDABuilder(AccountType.BOT)
      .setToken(dotenv.get("BOT_TOKEN"))
      .addEventListeners(bot)
      .setActivity(Activity.listening(dotenv.get("ACTIVITY")))
      .build()
  }
}
