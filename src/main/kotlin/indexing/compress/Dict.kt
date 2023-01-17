package indexing.compress

import utilities.deleteAllFiles

class Dict(private val blockSize: Int = 4) {

    private val compressorDir = "../InfoRetrievalData/temp/__Compressor/"

    private val dictCompressor = DictCompressor()
    private val postingCompressor = PostingCompressor(blockSize)

    init {
        deleteAllFiles(compressorDir)
    }

    fun start() = postingCompressor.openFile()
    fun end() = postingCompressor.closeFile()

    fun compress(term: String, posting: String) {
        if (term.length > 126) {
//            println(term.length)
            return
        }

        val termIndexCopy = dictCompressor.termsArr.size

        dictCompressor.compress(term)
        val useFrontCode = postingCompressor.compress(termIndexCopy, posting)

        if (useFrontCode)
            dictCompressor.frontCodeLastBlock(
                postingCompressor.pointersArr.last().termIndex, blockSize
            )
    }

    fun find(term: String): Array<Int> {
        // -1 -> term is smaller
        // -2 -> term is bigger
        // -3 -> absent
        fun linearSearch(inIndex: Int): Int {
            var index = inIndex
            var lcs = ""
            var foundBigger = false
            var foundSmaller = false

            val lcsSize = getTermByte(index++)
            if (getTermChar(index) == dictCompressor.hasLcsSign) {
                index++
                for (j in 0 until lcsSize - 1)
                    lcs += getTermChar(index++)
            } else index--

            for (i in 0 until blockSize) {
                var currentTerm = ""
                val size = getTermByte(index++)

                for (j in 0 until size)
                    currentTerm += getTermChar(index++)
                currentTerm = currentTerm.replace(dictCompressor.lcsSign.toString(), lcs)

                when {
                    currentTerm == term -> return i
                    currentTerm > term -> foundBigger = true
                    currentTerm < term -> foundSmaller = true
                }
            }

            return if (foundBigger && foundSmaller) -3
            else if (foundSmaller) -2
            else -1
        }

        var l = 0
        var r: Int = postingCompressor.pointersArr.size - 1
        while (l <= r) {
            val m = l + (r - l) / 2

            // Performs linear search in the middle block.
            // Returns position index in block if found
            val result = linearSearch(postingCompressor.pointersArr[m].termIndex)

            when {
                result > -1 -> return getPosting(m, result)
                result == -2 -> l = m + 1
                result == -1 -> r = m - 1
                else -> return emptyArray()
            }
        }

        return emptyArray()
    }

    private fun getPosting(pointerArrIndex: Int, blockIndex: Int): Array<Int> {
        val pointers = postingCompressor.pointersArr
        return if (blockIndex + 1 == blockSize) // if it's the last element of the pointers array
            PostingCompressor.getPosting(
                pointers[pointerArrIndex].postingIndex[blockIndex],
                pointers[pointerArrIndex + 1].postingIndex[0]
            )
        else
            PostingCompressor.getPosting(
                pointers[pointerArrIndex].postingIndex[blockIndex],
                pointers[pointerArrIndex].postingIndex[blockIndex + 1]
            )
    }

    fun serialize() {
        dictCompressor.serialize()
        postingCompressor.serialize()
    }

    private fun getTermByte(index: Int): Byte {
        return dictCompressor.termsArr[index]
    }

    private fun getTermChar(index: Int): Char {
        return dictCompressor.termsArr[index].toChar()
    }
}