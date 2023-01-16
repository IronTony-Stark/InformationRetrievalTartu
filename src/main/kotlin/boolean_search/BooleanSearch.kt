package boolean_search

import dicts.Dict
import dicts.DictBiword
import dicts.DocUnit
import utilities.merge
import utilities.revert
import java.lang.NumberFormatException
import kotlin.math.abs


class BooleanSearch(inputQuery: String, private val dict: Dict, private val dictBiword: DictBiword) {
    private var input = Letter(inputQuery)
    private var next = 0.toChar()
    private var result: List<Int>

    init {
        next = input.nextChar()
        result = start()
        match('$')
    }

    private fun start(): List<Int> {
        val word = word()
        return operator(word)
    }

    private fun word(): List<Int> {
        dropSpaces()

        when (next) {
            '(' -> {
                next = input.nextChar()
                val list = start()
                match(')')
                return list
            }
            '\"' -> {
                next = input.nextChar()
                val list = phraseQuery()
                match('\"')
                return list
            }
            else -> {
                val word = readSymbols()
                return if (word == "NOT") {
                    val allIds = mutableListOf<Int>()
                    for (i in 0 until DocUnit.idGlobal)
                        allIds.add(i)

                    revert(word(), allIds)
                } else if (word != "AND" && word != "OR" && word != "") {
                    dict.get(word).keys.map { it.id }
                } else throw Exception("Expecting NOT or 'word', found $word")
            }
        }
    }

    private fun operator(word1: List<Int>): List<Int> {
        dropSpaces()

        val operator = readSymbols()
        return if (operator == "AND" || operator == "OR") {
            val word2 = word()

            val result = if (operator == "AND")
                merge(word1, word2, isUnion = false)
            else merge(word1, word2, isUnion = true)

            operator(result)
        } else if (operator != "") throw Exception("Expecting AND OR, found $operator")
        else word1
    }

    private fun dropSpaces() {
        while (next == ' ') next = input.nextChar()
    }

    private fun readSymbols(): String {
        var word = ""
        while (next != ' ' && next != '$' && next != ')') {
            word += next
            next = input.nextChar()
        }
        return word
    }

    private fun phraseQuery(): List<Int> {
        var phrase = ""
        while (next != '\"') {
            if (next == '$')
                throw Exception("\" is missing")

            phrase += next
            next = input.nextChar()
        }

        val words = phrase
            .split(" ")
            .filter { str -> str.isNotEmpty() }
            .map { str -> str.toLowerCase() }

        return if (words.isEmpty()) {
            emptyList()
        } else if (words.size == 1) {
            dict.get(words[0]).keys.map { it.id }
        } else if (words.size == 3 && words[1].startsWith("/")) {
            try {
                val distance = Integer.parseInt(words[1].substring(1))
                positionalSearch(words[0], words[2], distance)
            } catch (e: NumberFormatException) {
                throw NumberFormatException("Expecting /k - k must be a number")
            }
        } else {
            biwordsSearch(words)
        }
    }

    private fun biwordsSearch(words: List<String>): List<Int> {
        var currentIds = emptyList<Int>()

        for (i in 1 until words.size) {
            if (words[i - 1].startsWith("/") || words[i].startsWith("/"))
                throw UnsupportedOperationException("/k is only supported for two words. Word1 /k word2")

            val docIds = dictBiword.get("${words[i - 1]} ${words[i]}").map { it.id }

            currentIds = if (currentIds.isEmpty())
                docIds else merge(currentIds, docIds, false)
        }

        return currentIds
    }

    private fun positionalSearch(w1: String, w2: String, distance: Int): List<Int> {
        val w1DocCoor = dict.get(w1)
        val w2DocCoor = dict.get(w2)

        val res = mutableListOf<Int>()

        w1DocCoor.entries.forEach forEach@ {
            val w2Coords = w2DocCoor[it.key] ?: return@forEach

            val w1CoorIter = it.value.iterator()
            val w2CoorIter = w2Coords.iterator()

            var w1Current = w1CoorIter.next()
            var w2Current = w2CoorIter.next()

            while (true) {
                if (abs(w1Current - w2Current) <= distance) {
                    res += it.key.id
                    break
                }

                if (w1Current < w2Current) {
                    if (w1CoorIter.hasNext())
                        w1Current = w1CoorIter.next()
                    else break
                } else {
                    if (w2CoorIter.hasNext())
                        w2Current = w2CoorIter.next()
                    else break
                }
            }
        }

        return res
    }

    private fun match(c: Char) {
        next = if (next == c) input.nextChar() else throw Exception("Expecting $c, found $next")
    }

    fun getResult(): List<Int> {
        return result
    }
}
