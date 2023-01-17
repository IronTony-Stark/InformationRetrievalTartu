package dicts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.util.*

class DictBiword {

    companion object {
        var type: Type = object : TypeToken<TreeMap<String, MutableSet<DocUnit>>>() {}.type

        fun loadFromJson(filePath: String) : TreeMap<String, MutableSet<DocUnit>> {
            val gson = Gson()
            return gson.fromJson(FileReader(filePath), type)
        }
    }

    private var dict: TreeMap<String, MutableSet<DocUnit>>

    constructor() { dict = TreeMap() }

    constructor(map: TreeMap<String, MutableSet<DocUnit>>) { dict = map }

    fun put(string: String, docUnit: DocUnit) {
        val documents = dict.getOrPut(string, { mutableSetOf() })
        documents += docUnit
    }

    fun get(string: String): Set<DocUnit> {
        return dict[string] ?: emptySet()
    }

    fun saveToJson(filePath: String) {
        val gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        val json = gson.toJson(dict, type)
        File(filePath).writeText(json)
    }
}
