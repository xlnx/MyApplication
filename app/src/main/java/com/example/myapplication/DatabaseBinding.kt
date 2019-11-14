package com.example.myapplication

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Entity(
    indices = [Index("bucket")]
)
data class Vocab(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var bucket: String,
    var content: String,
    var pronounce: String = "",
    var type: String = "",
    var definition: String = "",
    var star: Boolean = false
)

@Dao
interface VocabDao {
    @RawQuery
    fun select(query: SupportSQLiteQuery): List<Vocab>

    @RawQuery
    fun count(query: SupportSQLiteQuery): Int

    @Query("SELECT DISTINCT bucket FROM vocab ORDER BY bucket")
    fun selectBuckets(): List<String>

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
}
