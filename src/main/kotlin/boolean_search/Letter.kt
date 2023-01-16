package boolean_search

class Letter(
    private var input: String
) {
    private var p = 0
    private var c = ' '

    fun nextChar(): Char {
        c = if (p < input.length) input[p++] else '$'
        return c
    }

}