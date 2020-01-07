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

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var editable: Boolean = true,
    var profile: String = "[]",
    var name: String
)

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile ORDER BY name")
    fun select(): List<Profile>

    @Query("SELECT * FROM profile WHERE id=:id ORDER BY name")
    fun selectById(id: Int): List<Profile>

    @Update
    fun update(profile: Profile)

    @Insert
    fun insert(profile: Profile)

    @Delete
    fun delete(profile: Profile)

    @Query("SELECT * FROM profile WHERE NOT editable")
    fun selectDefault(): List<Profile>
}

@Database(
    entities = [Vocab::class, Profile::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabDao(): VocabDao
    abstract fun profileDao(): ProfileDao
}
