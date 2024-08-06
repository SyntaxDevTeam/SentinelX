package pl.syntaxerr

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SentinelXCommand(private val plugin: SentinelX) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val pdf = plugin.description
        val miniMessage = MiniMessage.miniMessage()
        val legacySerializer = LegacyComponentSerializer.legacySection()
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (sender.hasPermission("SentinelX.help")) {
                        val message = miniMessage.deserialize("<gray>#######################################\n#\n#  <gold>Dostępne komendy dla My-Sentinel:\n<gray>#\n#  <gold>/slx help <gray>- <white>Wyświetla ten monit.\n<gray>#  <gold>/slx version <gray>- <white>Pokazuje info pluginu. \n<gray>#  <gold>/slx reload <gray>- <white>Przeładowuje plik konfiguracyjny\n<gray>#\n#######################################")
                        sender.sendMessage(legacySerializer.serialize(message))
                    } else {
                        val message = miniMessage.deserialize("<red>Nie masz uprawnień do tej komendy.</red>")
                        sender.sendMessage(legacySerializer.serialize(message))
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (sender.hasPermission("SentinelX.version")) {
                        val message = miniMessage.deserialize(
                            """
                            <gray>#######################################
                            #
                            #   <gold>→ <bold>${pdf.name}</bold> ←
                            <gray>#   <white>Autor: <bold><gold>${pdf.authors}</gold></bold>
                            <gray>#   <white>WWW: <bold><gold>${pdf.website}</gold></bold>
                            <gray>#   <white>Wersja: <bold><gold>${pdf.version}</gold></bold><gray>
                            #
                            #######################################
                            """.trimIndent()
                        )
                        sender.sendMessage(legacySerializer.serialize(message))
                    } else {
                        val message = miniMessage.deserialize("<red>Nie masz uprawnień do tej komendy.</red>")
                        sender.sendMessage(legacySerializer.serialize(message))
                    }
                }
                args[0].equals("reload", ignoreCase = true) -> {
                    if (sender.hasPermission("SentinelX.reload")) {
                        plugin.restartMySentinelTask()
                        val message = miniMessage.deserialize("<green>Plik konfiguracyjny został przeładowany.</green>")
                        sender.sendMessage(legacySerializer.serialize(message))
                    } else {
                        val message = miniMessage.deserialize("<red>Nie masz uprawnień do tej komendy.</red>")
                        sender.sendMessage(legacySerializer.serialize(message))
                    }
                }
                args[0].equals("addword", ignoreCase = true) -> {
                    if (sender.hasPermission("SentinelX.addword")) {
                        if (args.size > 1) {
                            val newWord = args[1]
                            plugin.addBannedWord(newWord)
                            val message = miniMessage.deserialize("<green>Dodano nowe zakazane słowo: $newWord</green>")
                            sender.sendMessage(legacySerializer.serialize(message))
                        } else {
                            val message = miniMessage.deserialize("<red>Proszę podać słowo do dodania.</red>")
                            sender.sendMessage(legacySerializer.serialize(message))
                        }
                    } else {
                        val message = miniMessage.deserialize("<red>Nie masz uprawnień do tej komendy.</red>")
                        sender.sendMessage(legacySerializer.serialize(message))
                    }
                }
                else -> {
                    val message = miniMessage.deserialize("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
                    sender.sendMessage(legacySerializer.serialize(message))
                }
            }
        } else {
            val message = miniMessage.deserialize("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
            sender.sendMessage(legacySerializer.serialize(message))
        }
        return true
    }
}