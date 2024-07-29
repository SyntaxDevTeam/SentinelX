package pl.syntaxerr
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bstats.bukkit.Metrics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * Sentinel x
 *
 * @constructor Create empty Sentinel x
 */

@Suppress("UnstableApiUsage")
class SentinelX : JavaPlugin(), Listener {

    // Word filtering in chat messages
    // Filtrowanie słów w wiadomościach czatu
    private lateinit var wordFilter: WordFilter

    // Whether to apply full censorship on chat messages
    // Czy zastosować pełną cenzurę na wiadomościach czatu
    private var fullCensorship: Boolean = false

    /**
     * EN:
     * Method called during plugin enable.
     * Initializes configuration, registers commands and events,
     * and also initializes the word filter and censorship settings.
     *
     * PL:
     * Metoda wywoływana podczas włączania pluginu.
     * Inicjalizuje konfigurację, rejestruje komendy i zdarzenia,
     * a także inicjalizuje filtr słów i ustawienia cenzury.
     */
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
        val pluginId = 22781
        val metrics = Metrics(this, pluginId)
    }

    /**
     * EN:
     * Method called during plugin disable.
     * Unregisters events associated with this plugin.
     *
     * PL:
     * Metoda wywoływana podczas wyłączania pluginu.
     * Odrejestrowuje zdarzenia związane z tym pluginem.
     */
    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Listener)
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
    }

    /**
     * Restart my sentinel task
     *
     */
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

    /**
     * EN:
     * Updates the Sentinel settings.
     * Reloads the list of banned words and censorship settings,
     * then registers events associated with this plugin.
     *
     * PL:
     * Aktualizuje ustawienia Sentinel.
     * Przeładowuje listę zabronionych słów i ustawienia cenzury,
     * a następnie rejestruje zdarzenia związane z tym pluginem.
     */
    private fun updateSentinel() {
        val bannedWords: List<String> = config.getStringList("bannedWords")
        this.wordFilter = WordFilter(bannedWords)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(this, this)
    }

    /**
     * EN:
     * Handles a chat event.
     * If the message contains a banned word, it censors the message.
     *
     * PL:
     * Obsługuje zdarzenie czatu.
     * Jeśli wiadomość zawiera zabronione słowo, cenzuruje wiadomość.
     *
     * @param event The chat event to handle. / Zdarzenie czatu do obsłużenia.
     */
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message: String = (event.originalMessage() as TextComponent).content()
        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
        }
    }
    /**
     * EN:
     * Add banned word
     *
     * PL: Dodawanie nowego zakazanego słowa
     * @param word Banned word / Dodane słowo
     */
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

/**
 * EN:
 * Class for word filtering.
 * This class contains methods for checking if a message contains a banned word,
 * and for censoring a message.
 *
 * PL:
 * Klasa do filtrowania słów.
 * Ta klasa zawiera metody do sprawdzania, czy wiadomość zawiera zabronione słowo,
 * a także do cenzurowania wiadomości.
 *
 * @property bannedWords List of banned words. / Lista zabronionych słów.
 */
class WordFilter(private val bannedWords: List<String>) {

    /**
     * EN:
     * Checks if a message contains a banned word.
     *
     * PL:
     * Sprawdza, czy wiadomość zawiera zabronione słowo.
     *
     * @param message The message to check. / Wiadomość do sprawdzenia.
     * @return True if the message contains a banned word, otherwise false. / Prawda, jeśli wiadomość zawiera zabronione słowo, w przeciwnym razie fałsz.
     */
    fun containsBannedWord(message: String): Boolean {
        return bannedWords.any { message.contains(it) }
    }

    /**
     * EN:
     * Censors a message.
     * Replaces banned words with asterisks. If full censorship is enabled,
     * replaces the entire word with asterisks. Otherwise, replaces everything but
     * the first two letters with asterisks.
     *
     * PL:
     * Cenzuruje wiadomość.
     * Zamienia zabronione słowa na gwiazdki. Jeśli pełna cenzura jest włączona,
     * zamienia całe słowo na gwiazdki. W przeciwnym razie zamienia wszystko oprócz
     * pierwszych dwóch liter na gwiazdki.
     *
     * @param message The message to censor. / Wiadomość do ocenzurowania.
     * @param fullCensorship Whether to apply full censorship. / Czy zastosować pełną cenzurę.
     * @return The censored message. / Ocenzurowana wiadomość.
     */
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