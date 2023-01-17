package indexing

import dictBiwordSavePath
import dictSavePath
import dicts.Dict
import dicts.DictBiword
import dicts.DocUnit
import docUnitsSavePath
import java.io.File

fun invertedIndex(directoryPath: String, regex: Regex, depth: Int = Int.MAX_VALUE) {
    val dict = Dict()
    val dictBiword = DictBiword()

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
                    }
            }

        }

    dict.saveToJson(dictSavePath)
    dictBiword.saveToJson(dictBiwordSavePath)
    DocUnit.saveToJson(docUnitsSavePath)
}