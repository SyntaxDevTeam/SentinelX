package pl.syntaxerr

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SentinelXCommand(private val plugin: SentinelX) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val pdf = plugin.description
        if (args.isNotEmpty()) {
            if (args[0].equals("help", ignoreCase = true)) {
                if (sender.hasPermission("SentinelX.help")) {
                    sender.sendMessage("<gray>#######################################\n#\n#  <gold>Dostępne komendy dla My-Sentinel:\n<gray>#\n#  <gold>/slx help <gray>- <white>Wyświetla ten monit.\n<gray>#  <gold>/slx version <gray>- <white>Pokazuje info pluginu. \n<gray>#  <gold>/slx reload <gray>- <white>Przeładowuje plik konfiguracyjny\n<gray>#\n#######################################")
                } else {
                    sender.sendMessage("Nie masz uprawnień do tej komendy.")
                }
            } else if (args[0].equals("version", ignoreCase = true)) {
                if (sender.hasPermission("SentinelX.version")) {
                    sender.sendMessage(
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
                } else {
                    sender.sendMessage("Nie masz uprawnień do tej komendy.")
                }
            } else if (args[0].equals("reload", ignoreCase = true)) {
                if (sender.hasPermission("SentinelX.reload")) {
                    plugin.restartMySentinelTask()
                    sender.sendMessage("<green>Plik konfiguracyjny został przeładowany.</green>")
                } else {
                    sender.sendMessage("Nie masz uprawnień do tej komendy.")
                }
            }
        } else {
            sender.sendMessage("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
        }
        return true
    }
}