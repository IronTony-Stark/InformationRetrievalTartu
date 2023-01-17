package indexing

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dictBiwordSavePath
import dictSavePath
import dicts.Dict
import dicts.DictBiword
import dicts.DocUnit
import docUnitsSavePath
import java.io.File
import java.lang.reflect.Type
import java.util.*

fun invertedIndex(directoryPath: String, regex: Regex, depth: Int = Int.MAX_VALUE) {
    val dict = Dict()
    val dictBiword = DictBiword()

//    val dict: HashMap<String, MutableSet<Int>> = HashMap()
//    val type: Type = object : TypeToken<HashMap<String, MutableSet<Int>>>() {}.type

    File(directoryPath)
        .walk()
        .maxDepth(depth)
        .filter { file -> file.isFile }
        .forEach { file ->

            var index = 0
            val docUnit = DocUnit(file.path)

            // Biword indexes
            var previousWord = ""

            file.forEachLine { line ->
                line
                    .split(regex)
                    .filter { str -> str.isNotBlank() }
                    .map { str -> str.toLowerCase() }
                    .forEach { str ->
                        dict.put(str, docUnit, index++)

                        // Biword indexes
                        if (index != 0)
                            dictBiword.put("$previousWord $str", docUnit)
                        previousWord = str

//                        dict.getOrPut(str, { TreeSet() }) += docUnit.id
                    }
            }

        }

//    dict.saveToJson(dictSavePath)
//    dictBiword.saveToJson(dictBiwordSavePath)
    DocUnit.saveToJson(docUnitsSavePath)

//    val gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
//    val json = gson.toJson(dict, type)
//    File(dictSavePath).writeText(json)
}