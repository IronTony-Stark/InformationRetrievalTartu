package indexing.compress

import java.io.File
import java.io.FileOutputStream


class PostingCompressor(private val blockSize: Int) {

    companion object {
        private const val postingsFileName = "../InfoRetrievalData/temp/__Compressor/postings.txt"

        fun getPosting(byteIndexStart: Int, byteIndexEnd: Int) : Array<Int> {
            val ist = File(postingsFileName).inputStream()
            val bytes = ByteArray(byteIndexEnd - byteIndexStart)
            ist.skip(byteIndexStart.toLong())
            ist.read(bytes)
            ist.close()

            val gaps = getPostingGaps(bytes)

            val posting = IntArray(gaps.size)
            var prev = 0
            for (i in gaps.size - 1 downTo 0) {
                posting[gaps.size - i - 1] = gaps[i] + prev
                prev += gaps[i]
            }

            return posting.toTypedArray()
        }

        private fun getPostingGaps(bytes: ByteArray) : Array<Int> {
            val posting = ArrayList<Int>()
            var unit = 0

            for (i in bytes.size - 1 downTo 0) {
                if (bytes[i] < 0) {
                    unit -= bytes[i]
                    unit = unit shl 7
                } else {
                    unit += bytes[i]

                    posting.add(unit)
                    unit = 0
                }
            }

            return posting.toTypedArray()
        }
    }

    data class PointersEntry(val termIndex: Int, val postingIndex: Array<Int>)

    val pointersArr = ArrayList<PointersEntry>()
    private lateinit var outputStream: FileOutputStream
    private var nextInvertedArrPosition = 0
    private var byteIndex = 0

    fun compress(termIndex: Int, posting: String): Boolean {
        if (!::outputStream.isInitialized)
            throw UninitializedPropertyAccessException("Call start method to open output stream. " +
                    "After all data was read, written and compressed, call finish to close the stream")

        var useFrontCode = false

        // if it's a new block, create it
        if (nextInvertedArrPosition == 0)
            pointersArr.add(PointersEntry(termIndex, Array(blockSize) { 0 }))

        // add inverted index to block
        pointersArr.last().postingIndex[nextInvertedArrPosition++] = byteIndex
        val bytes = compress(posting)
        outputStream.write(bytes)
        byteIndex += bytes.size

        // if it's an end of the block, use front code
        if (nextInvertedArrPosition == blockSize) {
            if (blockSize > 2) useFrontCode = true

            nextInvertedArrPosition = 0
        }

        return useFrontCode
    }

    private fun compress(posting: String): ByteArray {
        val bytes = ArrayList<Byte>()
        var previousUnit = 0

        posting
            .split(" ")
            .map { Integer.parseInt(it) }
            .forEach {
                var gap = it - previousUnit
                previousUnit = it

                var i = 0
                while (true) {
                    i++
                    bytes.add((-(gap % 128)).toByte())

                    if (gap < 128)
                        break

                    gap /= 128
                }
                bytes[bytes.size - i] = (-bytes[bytes.size - i]).toByte()
            }

        return bytes.toByteArray()
    }

    fun serialize() {

    }

    fun openFile() {
        outputStream = File(postingsFileName).outputStream()
    }

    fun closeFile() = outputStream.close()
}