package indexing

import java.util.stream.Stream

class Parser(private val splitRegex: Regex) {

    fun parse(line: String): Stream<String> {
        return line
            .split(splitRegex)
            .filter { str -> str.isNotEmpty() }
            .map { str -> str.toLowerCase() }
            .stream()
    }
}

