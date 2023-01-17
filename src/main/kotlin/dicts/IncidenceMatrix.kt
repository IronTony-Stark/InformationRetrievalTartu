package dicts

import java.util.*

class IncidenceMatrix(private val inputDict: Dict) {

    private var dict: TreeMap<String, String> = TreeMap()
    private var documentIdsZero: Int = 0
    private var documentIdsTotal: Int = 0

    init {
        inputDict.terms.forEach {term ->
            var ids = ""

            var current = 0
            inputDict.get(term).keys.forEach { doc ->
                while (current++ < doc.id) ids += 0
                ids += 1
            }

            for (i in current until DocUnit.idGlobal) {
                ids += 0
            }

            ids.forEach { if (it == '0') documentIdsZero++ }
            documentIdsTotal += ids.length

            dict[term] = ids
        }
    }

    fun get(word: String): String {
        return dict[word] ?: ""
    }

    fun getZeroRatio(): Double {
        return documentIdsZero.toDouble() / documentIdsTotal
    }
}