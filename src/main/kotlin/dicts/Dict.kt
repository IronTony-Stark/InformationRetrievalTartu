package dicts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.util.*

class Dict {

    companion object {
        var type: Type = object : TypeToken<TreeMap<String, TreeMap<DocUnit, MutableList<Int>>>>() {}.type

        fun loadFromJson(filePath: String): TreeMap<String, TreeMap<DocUnit, MutableList<Int>>> {
            val gson = Gson()
            return gson.fromJson(FileReader(filePath), type)
        }
    }

    private var dict: TreeMap<String, TreeMap<DocUnit, MutableList<Int>>>

    val terms: Iterable<String>
        get() {
            return dict.keys
        }

    constructor() {
        dict = TreeMap()
    }

    constructor(map: TreeMap<String, TreeMap<DocUnit, MutableList<Int>>>) {
        dict = map
    }

    fun put(string: String, docUnit: DocUnit, index: Int) {
        val documents = dict.getOrPut(string, { TreeMap() })
        val indexes = documents.getOrPut(docUnit, { mutableListOf() })
        indexes.add(index)
    }

    fun get(string: String): Map<DocUnit, List<Int>> {
        return dict[string] ?: mapOf()
    }

    fun saveToJson(filePath: String) {
        val gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        val json = gson.toJson(dict, type)
        File(filePath).writeText(json)
    }
}
