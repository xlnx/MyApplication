package com.example.myapplication

import androidx.sqlite.db.SimpleSQLiteQuery

class VocabSelector {

    private val _buckets = ArrayList<String>()
    private var _shuffle = true
    private var _limit: Int? = null
    private var _starred: Boolean = false

    fun addBucket(vararg other: String): VocabSelector {
        for (bucket in other) {
            _buckets.add(bucket)
        }
        return this
    }
    fun setBuckets(buckets: Collection<String>): VocabSelector {
        _buckets.clear()
        _buckets.addAll(buckets)
        return this
    }
    fun setShuffle(shuffle: Boolean): VocabSelector {
        _shuffle = shuffle
        return this
    }
    fun setLimit(num: Int): VocabSelector {
        _limit = num
        return this
    }
    fun setStarred(): VocabSelector {
        _starred = true
        return this
    }

    fun select(): ArrayList<Vocab> {
        var queryStr = "SELECT * ${buildQuery()}"
        println(queryStr)
        return GlobalResource.db.vocabDao()
            .select(SimpleSQLiteQuery(queryStr))
            .toCollection(ArrayList())
    }

    fun count(): Int {
        var queryStr = "SELECT COUNT(*) ${buildQuery()}"
        println(queryStr)
        return GlobalResource.db.vocabDao()
            .count(SimpleSQLiteQuery(queryStr))
    }

    private fun buildQuery(): String {
        var queryStr = "FROM vocab "
        if (_starred || !_buckets.isEmpty()) {
            queryStr += "WHERE "
            if (_starred) {
                queryStr += "star "
            }
            if (_starred && !_buckets.isEmpty()) {
                queryStr += "AND "
            }
            if (!_buckets.isEmpty()) {
                queryStr += "bucket in ("
                var first = true
                for (bucket in _buckets) {
                    if (!first) {
                        queryStr += ", "
                    }
                    queryStr += "\"$bucket\""
                    first = false
                }
                queryStr += ") "
            }
        }
        if (_shuffle) {
            queryStr += "ORDER BY RANDOM() "
        }
        if (_limit != null) {
            queryStr += "LIMIT $_limit"
        }
        return queryStr
    }

    companion object {
        fun selectByCurrentProfile(): ArrayList<Vocab> {
            return VocabSelector()
                .addBucket("N5")
                .setLimit(10)
//                .setShuffle()
                .select()
        }
    }
}
