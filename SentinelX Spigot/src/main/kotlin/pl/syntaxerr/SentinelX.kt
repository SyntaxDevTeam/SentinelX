package pl.syntaxerr

import org.bstats.bukkit.Metrics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@Suppress("UNUSED_VARIABLE")
class SentinelX : JavaPlugin(), Listener {

    private lateinit var wordFilter: WordFilter
    private var fullCensorship: Boolean = false

    override fun onEnable() {
        saveDefaultConfig()
        val bannedWords: List<String> = config.getStringList("bannedWords")
        this.wordFilter = WordFilter(bannedWords)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(this, this)
        getCommand("sentinelx")?.setExecutor(SentinelXCommand(this))
        getCommand("slx")?.setExecutor(SentinelXCommand(this))
        val pluginId = 22781
        val metrics = Metrics(this, pluginId)
    }

    override fun onDisable() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this as Plugin)
    }

    fun restartMySentinelTask() {
        try {
            AsyncPlayerChatEvent.getHandlerList().unregister(this as Plugin)
            reloadConfig()
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
    fun onChat(event: AsyncPlayerChatEvent) {
        val message: String = event.message
        if (wordFilter.containsBannedWord(message)) {
            event.message = wordFilter.censorMessage(message, fullCensorship)
        }
    }
    fun addBannedWord(word: String) {
        val bannedWords = config.getStringList("bannedWords")
        if (!bannedWords.contains(word)) {
            bannedWords.add(word)
            config.set("bannedWords", bannedWords)
            saveConfig()
            restartMySentinelTask()
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
                if (words[i].toLowerCase(Locale.getDefault()).contains(bannedWord.toLowerCase(Locale.getDefault()))) {
                    val replacement = if (fullCensorship) "*".repeat(words[i].length) else words[i].substring(0, 2) + "*".repeat(words[i].length - 2)
                    words[i] = replacement
                }
            }
        }
        return words.joinToString(" ")
    }

}
