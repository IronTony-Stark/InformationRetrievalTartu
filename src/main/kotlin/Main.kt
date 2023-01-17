import boolean_search.BooleanSearch
import dicts.Dict
import dicts.DictBiword
import dicts.DocUnit
import joker_search.JokerSearcher
import joker_search.KGramIndex
import joker_search.PermutermIndex
import utilities.Memory
import kotlin.system.measureTimeMillis

val regex = "[^-а-яА-Яa-zA-Z0-9]+".toRegex()
val regexWithLoss = "[^-а-яА-Яa-zA-Z]+".toRegex()

const val dictSavePath = "dict.json"
const val dictBiwordSavePath = "dictBiword.json"
const val docUnitsSavePath = "docUnits.json"

// directed me that
fun booleanSearchAndIncidenceMatrix(dict: Dict, dictBiword: DictBiword) {
    assert(DocUnit.docUnitsParsed.isNotEmpty())

    println("Boolean Search: ")
    while (true) {
        val query = readLine()
        if (query.isNullOrEmpty())
            break
        try {
            println(BooleanSearch(query, dict, dictBiword).getResult())
        } catch (e: Exception) {
            println(e.message)
        }
    }

    val matrix = dicts.IncidenceMatrix(dict)
    println("Zero Ratio: ${matrix.getZeroRatio()}%")

    println("Incidence Matrix")
    println("Input a word")
    while (true) {
        val query = readLine()
        if (query.isNullOrEmpty())
            break
        println(matrix.get(query))
    }
}

fun jokerSearch(dict: Dict) {
    assert(DocUnit.docUnitsParsed.isNotEmpty())

    val permuterm: JokerSearcher = PermutermIndex()
    val kgram: JokerSearcher = KGramIndex(3)

    dict.terms.forEach {
        permuterm.insert(it)
        kgram.insert(it)
    }

    println("Joker :) Search: ")
    while (true) {
        val query = readLine()
        if (query.isNullOrEmpty())
            break
        try {
            val resPermuterm = permuterm.query(query) as List
            println("Permuterm: $resPermuterm")
            println(resPermuterm.size)
            println()

            val resKGram = kgram.query(query) as List
            println("KGram: $resKGram")
            println(resKGram.size)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

fun main() {
    // -Xms128m -Xmx256m
    println("Presumable free memory: ${Memory.freeMemoryPresumable / 10e8} GB")

    val timeDict: Long = measureTimeMillis {
//        invertedIndex("data/", regex)
    }
    println("Time: $timeDict")

//    val dict = Dict(Dict.loadFromJson(dictSavePath))
//    val dictBiword = DictBiword(DictBiword.loadFromJson(dictBiwordSavePath))
//    DocUnit.loadFromJson(docUnitsSavePath)

//    booleanSearchAndIncidenceMatrix(dict, dictBiword)
//    jokerSearch(dict)
}
