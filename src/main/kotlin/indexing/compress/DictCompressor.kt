package indexing.compress

import utilities.findLs
import java.io.File

class DictCompressor {

    private val dictFileName = "../InfoRetrievalData/temp/__Compressor/dict.txt"

    val lcsSign = '*'
    val hasLcsSign = '$'

    var termsArr = ArrayList<Byte>()

    fun compress(term: String) {
        addTerm(term.length)
        addTerm(term)
    }

    fun frontCodeLastBlock(firstTermIndex: Int, blockSize: Int) {
        val allTerms = Array(blockSize) { "" }
        var termIndex = firstTermIndex

        // get all terms in block
        for (i in 0 until blockSize) {
            var term = ""

            val size = getTermByte(termIndex++)

            for (j in termIndex until termIndex + size)
                term += getTermChar(j)

            allTerms[i] = term
            termIndex += size
        }

        val lcs = findLs(allTerms)

        // Rewrite the byte array with new compressed values
        if (lcs.length > 2) {
            for (i in termsArr.size - 1 downTo firstTermIndex)
                termsArr.removeAt(i)

            addTerm(lcs.length + 1)
            addTerm(hasLcsSign)
            addTerm(lcs)

            allTerms.forEach { term ->
                val newTerm = term.replace(lcs, lcsSign.toString())

                addTerm(newTerm.length)
                addTerm(newTerm)
            }

        }
    }

    fun serialize() {
        val osDict = File(dictFileName).outputStream()
        osDict.write(termsArr.toByteArray())
        osDict.close()
    }

    private fun addTerm(term: String) {
        term.forEach { addTerm(it) }
    }

    private fun getTermByte(index: Int): Byte {
        return termsArr[index]
    }

    private fun getTermChar(index: Int): Char {
        return termsArr[index].toChar()
    }

    private fun addTerm(char: Char) {
        termsArr.add(char.toByte())
    }

    private fun addTerm(int: Int) {
        termsArr.add(int.toByte())
    }
}