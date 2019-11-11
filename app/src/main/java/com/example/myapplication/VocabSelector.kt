package com.example.myapplication

class VocabSelector {

    private val _buckets = ArrayList<String>()
    private var _shuffle = true
    private var _limit: Int? = 10

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
    fun setLimit(): VocabSelector {
        _limit = null
        return this
    }

    fun select(): ArrayList<Vocab> {
        return if (_buckets.isEmpty()) {
            if (_shuffle) {
                if (_limit != null) {
                    AppDatabase.instance.vocabDao().selectShuffled(_limit!!)
                } else {
                    AppDatabase.instance.vocabDao().selectShuffled()
                }
            } else {
                if (_limit != null) {
                    AppDatabase.instance.vocabDao().select(_limit!!)
                } else {
                    AppDatabase.instance.vocabDao().select()
                }
            }
        } else {
            if (_shuffle) {
                if (_limit != null) {
                    AppDatabase.instance.vocabDao().selectFromBucketsShuffled(
                        *_buckets.toArray(arrayOfNulls<String>(_buckets.size) as Array<String>),
                        limit=_limit!!
                    )
                } else {
                    AppDatabase.instance.vocabDao().selectFromBucketsShuffled(
                        *_buckets.toArray(arrayOfNulls<String>(_buckets.size) as Array<String>)
                    )
                }
            } else {
                if (_limit != null) {
                    AppDatabase.instance.vocabDao().selectFromBuckets(
                        *_buckets.toArray(arrayOfNulls<String>(_buckets.size) as Array<String>),
                        limit=_limit!!
                    )
                } else {
                    AppDatabase.instance.vocabDao().selectFromBuckets(
                        *_buckets.toArray(arrayOfNulls<String>(_buckets.size) as Array<String>)
                    )
                }
            }
        }.toCollection(ArrayList())
    }
}
