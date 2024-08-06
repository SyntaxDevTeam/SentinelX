package pl.syntaxerr.helpers

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EventListener : Listener {
    private val events = mutableListOf<SyntaxDevTeamEvent>()

    @EventHandler
    fun onSyntaxDevTeamEvent(event: SyntaxDevTeamEvent) {
        // Dodaj event do listy
        events.add(event)
    }

    fun getHighestPriorityEvent(): SyntaxDevTeamEvent? {
        // Znajdź event z najwyższym priorytetem
        return events.maxByOrNull { it.prior }
    }
    fun getPluginsByPriorityDescending(): List<String> {
        // Zwróć listę nazw pluginów w odwróconej kolejności ich priorytetów
        return events.sortedByDescending { it.prior }.map { it.pluginName }
    }
}

