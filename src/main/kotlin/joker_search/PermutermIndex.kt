package joker_search

import java.lang.IllegalArgumentException
import java.util.*

class PermutermIndex : JokerSearcher {
    private class TrieNode {
        val children: MutableMap<Char, TrieNode> = HashMap()
        var word = ""

        fun isWord() : Boolean { return word.isNotEmpty() }
    }

    private val root = TrieNode()
    var count = 0
        private set
    val isEmpty: Boolean get() = count == 0

    override fun insert(word: String) {
        var permuterm = "$word$"
        for (i in permuterm.indices) {
            insertPermuterm(permuterm, word)
            permuterm = permuterm.substring(1) + permuterm[0]
        }
    }

    override fun query(query: String): Iterable<String> {
        var jokersCount = 0
        for (i in query.indices) {
            if (query[i] == '*') jokersCount++
        }

        if (jokersCount < 1 || jokersCount > 2)
            throw IllegalArgumentException("Word doesn't contain joker (*) or contains more than 2 of them")

        var permuterm = "$query$"
        while (permuterm[0] != '*') {
            permuterm = permuterm.substring(1) + permuterm[0]
        }
        permuterm = permuterm.substring(1)

        // if 2 jokers
        var mustContain = ""
        if (jokersCount == 2) {
//            while (permuterm[0] != '*') {
//                mustContain += permuterm[0]
//                permuterm = permuterm.substring(1)
//            }
//            permuterm = permuterm.substring(1)

            var i = 0
            while (permuterm[i++] != '*');
            mustContain += permuterm.substring(0, i - 1)
            permuterm = permuterm.substring(i)
        }

        val node = getNode(permuterm)
        val list: MutableList<String> = LinkedList()
        if (node == null) return list
        getAllWords(node, permuterm, list)

        // if 2 jokers
        if (jokersCount == 2) {
            return list.filter {
                it.contains(mustContain)
            }
        }

        return list
    }

    fun hasWord(word: String): Boolean {
        val node = getNode(word)
        return node != null && node.word.isNotEmpty()
    }

    fun delete(word: String): Boolean {
        return delete(root, word, 0)
    }

    private fun insertPermuterm(permuterm: String, word: String) {
        var current = root
        for (ch in permuterm) {
            val map: MutableMap<Char, TrieNode> = current.children
            val node = map[ch]
            if (node == null) map[ch] = TrieNode().also { current = it } else current = node
        }

        if (current.word.isEmpty()) {
            current.word = word
            count++
        }
    }

    private fun getAllWords(
        node: TrieNode,
        word: String,
        list: MutableList<String>
    ) {
        if (node.isWord()) list.add(node.word)
        for (ch in node.children.keys) {
            getAllWords(node.children[ch]!!, word + ch, list)
        }
    }

    private fun getNode(word: String): TrieNode? {
        var current = root
        for (ch in word) {
            val node: TrieNode = current.children[ch] ?: return null
            current = node
        }
        return current
    }

    private fun delete(node: TrieNode, word: String, index: Int): Boolean {
        if (index == word.length) {
            if (!node.isWord()) return false
            node.word = ""
            count--
            return node.children.isEmpty()
        }

        val ch = word[index]
        val next: TrieNode = node.children[ch] ?: return false

        val deleteNode = delete(next, word, index + 1) && !next.isWord()

        if (deleteNode) {
            node.children.remove(ch)
            return node.children.isEmpty()
        }

        return false
    }
}