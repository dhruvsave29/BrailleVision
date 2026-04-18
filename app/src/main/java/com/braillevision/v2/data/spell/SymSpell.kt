package com.braillevision.v2.data.spell

import android.content.Context
import com.braillevision.v2.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymSpell @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dictionary = mutableMapOf<String, Int>()
    private val deleteCache = mutableMapOf<String, MutableList<String>>()
    private var isLoaded = false
    
    private val maxEditDistance = 2
    
    suspend fun loadDictionary(): Boolean {
        if (isLoaded) return true
        
        return withContext(Dispatchers.IO) {
            try {
                val words = listOf(
                    "the" to 100000, "be" to 50000, "to" to 50000, "of" to 50000, "and" to 50000,
                    "a" to 50000, "in" to 40000, "that" to 40000, "have" to 30000, "i" to 30000,
                    "it" to 30000, "for" to 25000, "not" to 25000, "on" to 25000, "with" to 25000,
                    "he" to 25000, "as" to 25000, "you" to 25000, "do" to 20000, "at" to 20000,
                    "this" to 20000, "but" to 20000, "his" to 20000, "by" to 20000, "from" to 20000,
                    "they" to 15000, "we" to 15000, "say" to 15000, "her" to 15000, "she" to 15000,
                    "or" to 15000, "an" to 15000, "will" to 15000, "my" to 15000, "one" to 15000,
                    "all" to 15000, "would" to 12000, "there" to 12000, "their" to 12000, "what" to 12000,
                    "so" to 12000, "up" to 12000, "out" to 12000, "if" to 12000, "about" to 12000,
                    "who" to 10000, "get" to 10000, "which" to 10000, "go" to 10000, "me" to 10000,
                    "hello" to 8000, "world" to 8000, "braille" to 5000, "vision" to 5000,
                    "accessibility" to 3000, "matters" to 3000, "reading" to 4000, "power" to 4000,
                    "technology" to 3000, "all" to 5000, "help" to 5000, "please" to 4000,
                    "thank" to 4000, "you" to 10000, "yes" to 5000, "no" to 5000, "can" to 5000,
                    "see" to 5000, "read" to 5000, "write" to 4000, "learn" to 4000, "teach" to 3000,
                    "book" to 4000, "page" to 3000, "text" to 4000, "word" to 4000, "letter" to 3000,
                    "name" to 5000, "good" to 5000, "bad" to 3000, "right" to 4000, "left" to 4000,
                    "start" to 3000, "stop" to 3000, "begin" to 2000, "end" to 3000,
                    "love" to 5000, "life" to 4000, "work" to 5000, "home" to 5000, "school" to 4000,
                    "friend" to 4000, "family" to 4000, "mother" to 3000, "father" to 3000, "child" to 3000,
                    "man" to 4000, "woman" to 4000, "boy" to 3000, "girl" to 3000, "person" to 3000,
                    "day" to 5000, "night" to 3000, "morning" to 3000, "evening" to 2000, "time" to 5000,
                    "year" to 4000, "month" to 3000, "week" to 3000, "today" to 4000, "tomorrow" to 3000,
                    "water" to 4000, "food" to 4000, "drink" to 3000, "eat" to 4000, "sleep" to 3000,
                    "walk" to 3000, "run" to 3000, "talk" to 4000, "speak" to 3000, "listen" to 3000,
                    "hear" to 3000, "look" to 4000, "watch" to 3000, "feel" to 3000, "think" to 4000,
                    "know" to 5000, "understand" to 3000, "believe" to 2000, "hope" to 3000, "want" to 5000,
                    "need" to 5000, "try" to 4000, "use" to 4000, "make" to 5000, "take" to 5000,
                    "give" to 4000, "find" to 4000, "tell" to 4000, "ask" to 4000, "come" to 5000,
                    "great" to 4000, "new" to 4000, "first" to 4000, "last" to 4000, "long" to 4000,
                    "little" to 4000, "own" to 3000, "other" to 4000, "old" to 4000, "young" to 3000,
                    "more" to 5000, "some" to 4000, "any" to 4000, "such" to 3000, "only" to 4000,
                    "same" to 3000, "very" to 4000, "just" to 5000, "also" to 4000, "now" to 5000,
                    "here" to 4000, "there" to 4000, "where" to 4000, "when" to 4000, "why" to 3000,
                    "how" to 4000, "then" to 4000, "than" to 4000, "like" to 5000, "back" to 4000
                )
                
                words.forEach { (word, frequency) ->
                    dictionary[word.lowercase(Locale.US)] = frequency
                    generateDeletes(word.lowercase(Locale.US)).forEach { delete ->
                        deleteCache.getOrPut(delete) { mutableListOf() }.add(word.lowercase(Locale.US))
                    }
                }
                
                isLoaded = true
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    fun correct(word: String): String {
        if (!isLoaded) return word
        
        val lowerWord = word.lowercase(Locale.US)
        
        if (dictionary.containsKey(lowerWord)) {
            return word
        }
        
        val candidates = findCandidates(lowerWord)
        
        if (candidates.isEmpty()) {
            return word
        }
        
        val bestMatch = candidates.maxByOrNull { dictionary[it] ?: 0 }
        return bestMatch?.let { 
            if (word[0].isUpperCase()) it.replaceFirstChar { c -> c.titlecase(Locale.US) } else it 
        } ?: word
    }
    
    fun correctSentence(text: String): String {
        if (!isLoaded) return text
        
        return text.split(" ").joinToString(" ") { word ->
            correct(word)
        }
    }
    
    private fun findCandidates(word: String): List<String> {
        val candidates = mutableListOf<String>()
        
        val deletes = generateDeletes(word)
        deletes.forEach { delete ->
            deleteCache[delete]?.let { candidates.addAll(it) }
        }
        
        val transposes = generateTransposes(word)
        transposes.forEach { transpose ->
            if (dictionary.containsKey(transpose)) {
                candidates.add(transpose)
            }
        }
        
        val edits = generateEdits(word)
        edits.forEach { edit ->
            if (dictionary.containsKey(edit)) {
                candidates.add(edit)
            }
        }
        
        return candidates.distinct()
    }
    
    private fun generateDeletes(word: String): List<String> {
        val deletes = mutableListOf<String>()
        val chars = word.toCharArray()
        
        for (i in chars.indices) {
            val delete = word.substring(0, i) + word.substring(i + 1)
            deletes.add(delete)
        }
        
        return deletes
    }
    
    private fun generateTransposes(word: String): List<String> {
        val transposes = mutableListOf<String>()
        val chars = word.toCharArray()
        
        for (i in 0 until chars.size - 1) {
            val swapped = chars.copyOf()
            val temp = swapped[i]
            swapped[i] = swapped[i + 1]
            swapped[i + 1] = temp
            transposes.add(String(swapped))
        }
        
        return transposes
    }
    
    private fun generateEdits(word: String): List<String> {
        val edits = mutableListOf<String>()
        val alphabet = "abcdefghijklmnopqrstuvwxyz"
        val chars = word.toCharArray()
        
        for (i in 0..chars.size) {
            for (c in alphabet) {
                edits.add(word.substring(0, i) + c + word.substring(i))
            }
        }
        
        for (i in chars.indices) {
            for (c in alphabet) {
                edits.add(word.substring(0, i) + c + word.substring(i + 1))
            }
        }
        
        return edits
    }
    
    fun isReady(): Boolean = isLoaded
}
