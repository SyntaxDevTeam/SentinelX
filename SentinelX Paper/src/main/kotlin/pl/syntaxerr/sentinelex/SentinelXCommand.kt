package pl.syntaxerr.sentinelex

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.plugin.PluginDescriptionFile
import org.jetbrains.annotations.NotNull

@Suppress("UnstableApiUsage")
class SentinelXCommand(private val plugin: SentinelX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        val pdf: PluginDescriptionFile = plugin.description
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.help")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#  <gold>Dostępne komendy dla My-Sentinel:\n<gray>#\n#  <gold>/slx help <gray>- <white>Wyświetla ten monit.\n<gray>#  <gold>/slx version <gray>- <white>Pokazuje info pluginu. \n<gray>#  <gold>/slx reload <gray>- <white>Przeładowuje plik konfiguracyjny\n<gray>#\n#######################################")
                    } else {
                        stack.sender.sendMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.version")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#   <gold>→ <bold>" + pdf.name + "</bold> ←\n<gray>#   <white>Autor: <bold><gold>" + pdf.authors + "</gold></bold>\n<gray>#   <white>WWW: <bold><gold><click:open_url:'" + pdf.website + "'>"  + pdf.website + "</click></gold></bold>\n<gray>#   <white>Wersja: <bold><gold>" + pdf.version + "</gold></bold><gray>\n#\n#######################################")
                    } else {
                        stack.sender.sendRichMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("reload", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.reload")) {
                        plugin.restartMySentinelTask()
                        stack.sender.sendRichMessage("<green>Plik konfiguracyjny został przeładowany.</green>")
                    } else {
                        stack.sender.sendRichMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                else -> {
                    stack.sender.sendRichMessage("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
                }
            }
        } else {
            stack.sender.sendRichMessage("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
        }
    }
}