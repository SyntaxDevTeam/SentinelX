package pl.syntaxerr.sentinelex
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@Suppress("UnstableApiUsage")
class SentinelX : JavaPlugin(), Listener {

    private lateinit var wordFilter: WordFilter
    private var fullCensorship: Boolean = false

    override fun onEnable() {
        saveDefaultConfig()
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register("sentinelx", "Komenda pluginu SentinelX. Wpisz /slx help aby sprawdzic dostępne komendy", SentinelXCommand(this))
            commands.register("slx", "Komenda pluginu SentinelX. Wpisz /sentinelx help aby sprawdzic dostępne komendy", SentinelXCommand(this))
        }
        val bannedWords: List<String> = config.getStringList("bannedWords")
        this.wordFilter = WordFilter(bannedWords)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Listener)
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
    }

    fun restartMySentinelTask() {
        try {
            AsyncChatEvent.getHandlerList().unregister(this as Plugin)
            super.reloadConfig()
            updateSentinel()
        } catch (e: Exception) {
            logger.severe("Wystąpił błąd podczas przełądowania konfiguracji: " + e.message)
            e.printStackTrace()
        }
    }

    private fun updateSentinel() {
        val bannedWords: List<String> = config.getStringList("bannedWords")
        this.wordFilter = WordFilter(bannedWords)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message: String = (event.originalMessage() as TextComponent).content()
        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
        }
    }
}

class WordFilter(private val bannedWords: List<String>) {

    fun containsBannedWord(message: String): Boolean {
        return bannedWords.any { message.contains(it) }
    }

    fun censorMessage(message: String, fullCensorship: Boolean): String {
        val words = message.split("\\s+".toRegex()).toMutableList()
        for (i in words.indices) {
            for (bannedWord in bannedWords) {
                if (words[i].lowercase(Locale.getDefault()).contains(bannedWord.lowercase(Locale.getDefault()))) {
                    val replacement = if (fullCensorship) "*".repeat(words[i].length) else words[i].substring(0, 2) + "*".repeat(words[i].length - 2)
                    words[i] = replacement
                }
            }
        }
        return words.joinToString(" ")
    }
}