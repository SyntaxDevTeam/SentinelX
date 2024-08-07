package pl.syntaxerr

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bstats.bukkit.Metrics
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxerr.base.WordFilter
import pl.syntaxerr.commans.SentinelXCommand
import pl.syntaxerr.eventhandler.*
import pl.syntaxerr.helpers.*

@Suppress("UnstableApiUsage")
class SentinelX : JavaPlugin(), Listener {

    private lateinit var logger: Logger
    private val pluginMetas = this.pluginMeta
    private var config = getConfig()
    private var debugMode = config.getBoolean("debug")
    private lateinit var wordFilter: WordFilter
    private var fullCensorship: Boolean = false
    private lateinit var pluginManager: PluginManager


    override fun onLoad() {
        logger = Logger(pluginMetas, debugMode)
    }

    override fun onEnable() {
        saveDefaultConfig()

        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register("sentinelx", "Komenda pluginu SentinelX. Wpisz /slx help aby sprawdzic dostępne komendy", SentinelXCommand(this))
            commands.register("slx", "Komenda pluginu SentinelX. Wpisz /sentinelx help aby sprawdzic dostępne komendy", SentinelXCommand(this))
        }
        this.wordFilter = WordFilter(this)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(SentinelXChat(wordFilter, fullCensorship), this)
        val pluginId = 22781
        Metrics(this, pluginId)
        // Inicjalizacja PluginManager z przekazaniem obiektu JavaPlugin
        pluginManager = PluginManager(this)

        // Pobranie listy pluginów z zewnętrznego źródła
        val externalPlugins = pluginManager.fetchPluginsFromExternalSource("https://raw.githubusercontent.com/SyntaxDevTeam/plugins-list/main/plugins.json")

        // Pobranie listy załadowanych pluginów
        val loadedPlugins = pluginManager.fetchLoadedPlugins()

        // Pobranie nazwy pluginu z najwyższym priorytetem
        val highestPriorityPlugin = pluginManager.getHighestPriorityPlugin(externalPlugins, loadedPlugins)

        // Sprawdzenie, czy nazwa pluginu z najwyższym priorytetem to ta sama co aktualnie uruchamiany plugin
        if (highestPriorityPlugin == pluginMetas.name) {
            val syntaxDevTeamPlugins = loadedPlugins.filter { it != pluginMetas.name }
            logger.pluginStart(syntaxDevTeamPlugins)
        }
    }

    fun restartMySentinelTask() {
        try {
            AsyncChatEvent.getHandlerList().unregister(this as Plugin)
            super.reloadConfig()
            updateSentinel()
        } catch (e: Exception) {
            logger.err("Wystąpił błąd podczas przełądowania konfiguracji: " + e.message)
        }
    }

    private fun updateSentinel() {
        this.wordFilter = WordFilter(this)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(SentinelXChat(wordFilter, fullCensorship), this)
    }

    fun addBannedWord(word: String) {
        wordFilter.addBannedWord(word)
        restartMySentinelTask()
    }

    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Listener)
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
    }
}
