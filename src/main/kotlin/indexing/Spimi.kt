package indexing

import dicts.DocUnit
import indexing.compress.Dict
import indexing.compress.DictCompressor
import utilities.Memory
import utilities.deleteAllFiles
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.system.measureTimeMillis

class Spimi(directoryPath: String, splitRegex: Regex, dropRate: Float = 0.2f,
            depth: Int = Int.MAX_VALUE) {

    private val spimiDir = "../InfoRetrievalData/temp/__Spimi/"

    init {
        deleteAllFiles(spimiDir)

        val dict: HashMap<String, MutableSet<Int>> = HashMap()
        val parser = Parser(splitRegex)
        var blockCount = 0

        File(directoryPath)
            .walk()
            .maxDepth(depth)
            .filter { file -> file.isFile }
            .forEach { file ->
                val doc = DocUnit(file.path)

                file.forEachLine { line ->
                    parser.parse(line)
                        .forEach { str ->
                            if (Memory.freeMemoryPresumable < Memory.maxMemory * dropRate) {
                                writeBlock(dict, blockCount++)
                            }

                            dict.getOrPut(str, { TreeSet() }) += doc.id
                        }
                }
            }.run {
                writeBlock(dict, blockCount++)
            }

        mergeBlocks(blockCount)
    }

    private fun mergeBlocks(fileNum: Int) {
        // @param words - string lines of type term:[5, 6, 100, ...]
        // Returns smallest term or null if all lines are nulls
        fun nextTerm(words: Array<String?>): String? {
            // find first non null line
            var i = 0
            while (words[i] == null) {
                if (++i == words.size) return null
            }

            // Compare first non null value to others non nulls to find the smallest term
            var term = words[i]!!.substring(0, words[i]!!.indexOf(':'))
            for (j in i + 1 until words.size) {
                if (words[j] != null) {
                    val nextTerm = words[j]!!.substring(0, words[j]!!.indexOf(':'))
                    if (nextTerm < term) term = nextTerm
                }
            }

            return term
        }

        fun nextLine(br: BufferedReader): String? {
            var line = br.readLine()
            if (line.isNullOrEmpty()) return null
            while (line[line.length - 1] != ']') line += br.readLine()
            return line
        }

        val brs: Array<BufferedReader> = Array(fileNum) { BufferedReader(FileReader("$spimiDir$it.txt")) }
        val termsDocIds: Array<String?> = Array(fileNum) { nextLine(brs[it]) }

        val dict = Dict()
        dict.start()

        var currentTerm = nextTerm(termsDocIds)
        var currentIndex = ""

        while (currentTerm != null) {
            // merge all indexes of this term
            for (i in termsDocIds.indices) {
                if (termsDocIds[i] == null) continue

                val dots = termsDocIds[i]!!.indexOf(":")
                val term = termsDocIds[i]!!.substring(0, dots)

                if (term != currentTerm) continue

                // format 1 2 3 10 100 ...
                var index = termsDocIds[i]!!
                    .substring(dots + 2, termsDocIds[i]!!.length - 1)
                    .replace(",", "")

                if (currentIndex.isNotEmpty()) {
                    // compare last currentIndex element and
                    // first index element as they may be equal
                    val curIndexLastSpace = currentIndex.indexOfLast { c -> c == ' ' }
                    val curIndexLastEl = currentIndex.substring(curIndexLastSpace + 1)

                    val indexFirstSpace = index.indexOf(' ')
                    val indexFirstEl = if (indexFirstSpace == -1) index else index.substring(0, indexFirstSpace)

                    if (curIndexLastEl == indexFirstEl)
                        index = index.substring(indexFirstSpace + 1)

                    // Add space
                    currentIndex += " "
                }
                currentIndex += index

                termsDocIds[i] = nextLine(brs[i])
            }

            // write term and index to disk
            dict.compress(currentTerm, currentIndex)

            // set new values and return if all terms are nulls
            currentTerm = nextTerm(termsDocIds)
            currentIndex = ""
        }

        // closes fileOutputStream in postings compressor
        dict.end()

        // test retrieval
//        println(dict.find("romeo").contentToString())

        // serialize to disk
        dict.serialize()
    }

    private fun writeBlock(dict: MutableMap<String, MutableSet<Int>>, blockCount: Int) {
        println("$spimiDir$blockCount.txt ${dict.keys.count()}")

        writeToFile(dict, "$spimiDir$blockCount.txt")
        dict.clear()
        Memory.gcBite()
    }

    private fun writeToFile(dict: Map<String, MutableSet<Int>>, filePath: String) {
        File(filePath).bufferedWriter().use { out ->
            var first = true
            dict.keys.sorted().forEach { key ->
                if (!first) out.write("\n")
                first = false
                out.write("${key}:${dict[key]}")
            }
        }
    }
}