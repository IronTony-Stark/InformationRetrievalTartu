package dicts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type

data class DocUnit(val filePath: String) : Comparable<DocUnit> {

    companion object {
        var idGlobal = 0
        var docUnitsParsed = emptyArray<DocUnit>()

        val type: Type = object : TypeToken<Array<DocUnit>>() {}.type

        fun saveToJson(filePath: String) {
            val gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
            val json = gson.toJson(
                docUnitsParsed,
                type
            )
            File(filePath).writeText(json)
        }

        fun loadFromJson(filePath: String) {
            val gson = Gson()
            docUnitsParsed = gson.fromJson(FileReader(filePath),
                type
            )
            idGlobal = docUnitsParsed.size
        }
    }

    val id: Int

    init {
        id = idGlobal++
        docUnitsParsed += this
    }

    override fun compareTo(other: DocUnit): Int {
        return id.compareTo(other.id)
    }
}