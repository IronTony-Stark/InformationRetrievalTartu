package utilities

import java.io.File

/*m and n must be sorted*/
public fun <T : Comparable<T>> merge(m: Iterable<T>, n: Iterable<T>, isUnion: Boolean): List<T> {
    val result = mutableListOf<T>()

    val mIter = m.iterator()
    val nIter = n.iterator()
    if (!mIter.hasNext()) {
        return if (isUnion) {
            n.toList()
        } else emptyList()
    }
    if (!nIter.hasNext()) {
        return if (isUnion) {
            m.toList()
        } else emptyList()
    }

    var mVar = mIter.next()
    var nVar = nIter.next()

    fun checkIterator(iter1: Iterator<T>, iter2: Iterator<T>): MutableList<T>? {
        if (iter1.hasNext()) {
            if (iter1 == nIter) nVar = iter1.next()
            else mVar = iter1.next()
        } else {
            if (isUnion) {
                while (iter2.hasNext()) {
                    result += iter2.next()
                }
            }
            return result
        }
        return null
    }

    while (true) {
        val cmp = mVar.compareTo(nVar)

        when {
            cmp > 0 -> {
                if (isUnion) result += nVar
                val res = checkIterator(nIter, mIter)
                if (res != null) return res + mVar
            }
            cmp < 0 -> {
                if (isUnion) result += mVar
                val res = checkIterator(mIter, nIter)
                if (res != null) return res + nVar
            }
            else -> {
                result += mVar

                val res = checkIterator(nIter, mIter)
                if (res != null) return res

                val res1 = checkIterator(mIter, nIter)
                if (res1 != null) return res1
            }
        }
    }
}

/*m and all must be sorted*/
public fun revert(m: List<Int>, all: List<Int>): List<Int> {
    if (m.isEmpty()) return all
    if (all.isEmpty()) return emptyList()

    val result = mutableListOf<Int>()

    val mIter = m.iterator()
    val allIter = all.iterator()

    var mVar = mIter.next()

    do {
        val allVar = allIter.next()

        if (allVar == mVar) {
            if (mIter.hasNext()) {
                mVar = mIter.next()
            } else {
                while (allIter.hasNext())
                    result.add(allIter.next())
                return result
            }
        } else {
            result.add(allVar)
        }

    } while (allIter.hasNext())

    return result
}

fun findLs(arr: Array<String>): String {
    var lcs = "" // longest common substring
    var coef = 0.0

    for (i in 3 until arr.size) {
        val newLcsFront = findLcs(arr.copyOfRange(0, i + 1))
        val newCoefFront = newLcsFront.length * i.toDouble()

        if (newCoefFront > coef) {
            coef = newCoefFront
            lcs = newLcsFront
        }

        val newLcsBack = findLcs(arr.copyOfRange(arr.size - i - 1, arr.size))
        val newCoefBack = newLcsBack.length * i.toDouble()

        if (newCoefBack > coef) {
            coef = newCoefBack
            lcs = newLcsBack
        }
    }

    return lcs
}

// longest common substring
fun findLcs(arr: Array<String>): String {
    val n = arr.size
    val s = arr[0]
    val len = s.length
    var res = ""

    for (i in 0 until len) {
        for (j in i + 1..len) {

            // generating all possible substrings
            // of our reference string arr[0] i.e s
            val largestSubstring = s.substring(i, j)
            var k = 1
            while (k < n) {
                // Check if the generated substring is
                // common to all words
                if (!arr[k].contains(largestSubstring)) break
                k++
            }

            // If current substring is present in
            // all strings and its length is greater
            // than current result
            if (k == n && res.length < largestSubstring.length) res = largestSubstring
        }
    }

    return res
}

fun deleteAllFiles(path: String, depth: Int = Int.MAX_VALUE) {
    File(path)
        .walk()
        .maxDepth(depth)
        .filter { file -> file.isFile }
        .forEach { file -> file.delete() }
}