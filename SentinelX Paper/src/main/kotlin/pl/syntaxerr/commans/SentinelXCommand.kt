package pl.syntaxerr.commans

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import org.jetbrains.annotations.NotNull
import pl.syntaxerr.SentinelX

/**
 * SentinelX command
 *
 * @property plugin
 * @constructor Create empty SentinelXCommand
 */
@Suppress("UnstableApiUsage")
class SentinelXCommand(private val plugin: SentinelX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
        val pdf = plugin.description
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.help")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#  <gold>Dostępne komendy dla " + pluginMeta.name + ":\n<gray>#\n#  <gold>/slx help <gray>- <white>Wyświetla ten monit.\n<gray>#  <gold>/slx version <gray>- <white>Pokazuje info pluginu. \n<gray>#  <gold>/slx reload <gray>- <white>Przeładowuje plik konfiguracyjny\n<gray>#\n#######################################")
                    } else {
                        stack.sender.sendMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.version")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#   <gold>→ <bold>" + pluginMeta.name + "</bold> ←\n<gray>#   <white>Autor: <bold><gold>" + pdf.authors + "</gold></bold>\n<gray>#   <white>WWW: <bold><gold><click:open_url:'" + pdf.website + "'>"  + pdf.website + "</click></gold></bold>\n<gray>#   <white>Wersja: <bold><gold>" + pluginMeta.version + "</gold></bold><gray>\n#\n#######################################")
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
                args[0].equals("addword", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("SentinelX.addword")) {
                        if (args.size > 1) {
                            val newWord = args[1]
                            plugin.addBannedWord(newWord)
                            stack.sender.sendMessage("<green>Dodano nowe zakazane słowo: $newWord</green>")
                        } else {
                            stack.sender.sendMessage("<red>Proszę podać słowo do dodania.</red>")
                        }
                    } else {
                        stack.sender.sendMessage("Nie masz uprawnień do tej komendy.")
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
