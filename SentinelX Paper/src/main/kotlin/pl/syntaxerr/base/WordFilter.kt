package pl.syntaxerr.base

import java.util.*

class WordFilter(var bannedWords: MutableList<String>) {

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

    fun addBannedWord(word: String) {
        if (!bannedWords.contains(word)) {
            bannedWords.add(word)
        }
    }
}
