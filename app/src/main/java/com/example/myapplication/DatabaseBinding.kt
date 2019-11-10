package com.example.myapplication

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
    @Query("SELECT * FROM vocab")
    fun getAll(): List<Vocab>

    @Query("SELECT * FROM vocab WHERE bucket=:bucket")
    fun getAllByBucket(bucket: String): List<Vocab>

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
