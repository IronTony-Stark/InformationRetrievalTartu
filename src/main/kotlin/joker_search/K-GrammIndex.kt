package joker_search

import com.google.gson.internal.LinkedTreeMap
import utilities.merge
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException

class KGramIndex(private val k: Int) : JokerSearcher {

    private var dict = LinkedTreeMap<String, ArrayList<String>>()
    val count get() = dict.keys.count()

    override fun insert(word: String) {
        val term = "$$word$"

        if (term.length < k) return

        for (i in k..term.length) {
            val kGram = term.substring(i - k, i)
            val terms = dict.getOrPut(kGram, { ArrayList() })

            val index = binarySearch(terms.toTypedArray(), word, 0, terms.size - 1)
            if (index < 0) terms.add(-index - 1, word)
        }
    }

    override fun query(query: String): Iterable<String> {
        val check = query.split('*')

        if (check[0].length < k - 1 && check[1].length < k - 1)
            throw UnsupportedOperationException("Query length is two short for $k-gram index")

        var result: ArrayList<String>

        // TODO improve
        var current = "$${check[0]}"
        if (current.length != 1) {
            result = dict[current.substring(0, k)] ?: return emptyList()
            for (i in 1..current.length - k) {
                val temp = dict[current.substring(i, i + k)] ?: return emptyList()
                result = merge(result, temp, false) as ArrayList<String>
            }
        } else {
            current = "${check[1]}$"
            result = dict[current.substring(0, k)] ?: return emptyList()
        }

        for (i in 1..current.length - k) {
            val temp = dict[current.substring(i, i + k)] ?: return emptyList()
            result = merge(result, temp, false) as ArrayList<String>
        }

        return result.filter { it.startsWith(check[0]) && it.endsWith(check[1]) &&
                it.length >= check[0].length + check[1].length }
    }

    private fun binarySearch(arr: Array<String>, word: String, start: Int, end: Int): Int {
        var l = start
        var r: Int = end

        while (l <= r) {
            val m = l + (r - l) / 2

            if (arr[m] == word) return m

            if (arr[m] < word) l = m + 1 else r = m - 1
        }

        return -l - 1
    }

    init {
        if (k < 2) throw IllegalArgumentException("K must be greater than 1")
    }
}