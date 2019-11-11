package com.example.myapplication

import android.content.Context
import androidx.room.*

@Entity(
    indices = [Index("bucket")]
)
data class Vocab(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bucket: String,
    val content: String,
    val pronounce: String = "",
    val type: String = "",
    val definition: String = "",
    var star: Boolean = false
)

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab LIMIT :limit")
    fun select(limit: Int = 2147483647): List<Vocab>

    @Query("SELECT * FROM vocab WHERE bucket IN (:buckets) LIMIT :limit")
    fun selectFromBuckets(vararg buckets: String, limit: Int = 2147483647): List<Vocab>

    @Query("SELECT * FROM vocab ORDER BY RANDOM() LIMIT :limit")
    fun selectShuffled(limit: Int = 2147483647): List<Vocab>

    @Query("SELECT * FROM vocab WHERE bucket IN (:buckets) ORDER BY RANDOM() LIMIT :limit")
    fun selectFromBucketsShuffled(vararg buckets: String, limit: Int = 2147483647): List<Vocab>

    @Query("DELETE FROM vocab WHERE bucket=:bucket")
    fun dropBucket(bucket: String)

    @Insert
    fun insertAll(vararg vocabs: Vocab)

    @Update
    fun update(vocab: Vocab)

    @Delete
    fun delete(vocab: Vocab)
}

@Database(
    entities = [Vocab::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabDao(): VocabDao

    companion object {

        private lateinit var db: AppDatabase
        val instance get() = db

        fun connect(context: Context) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app.db"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
