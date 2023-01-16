package joker_search

interface JokerSearcher {
    fun insert(word: String)
    fun query(query: String) : Iterable<String>
}