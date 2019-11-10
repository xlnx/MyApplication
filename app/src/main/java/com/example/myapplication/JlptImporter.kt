package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream
import java.io.IOException

class JlptImporter(context: Context) {

    private val database: SQLiteDatabase

    init {
        val dbFile = context.getDatabasePath("$DATABASE_NAME")
        if (!dbFile.exists()){
            try {
                val checkDB = context.openOrCreateDatabase("$DATABASE_NAME", Context.MODE_PRIVATE,null)
                checkDB.close()
                val iss = context.assets.open("$DATABASE_NAME")
                val os = FileOutputStream(dbFile)

                val buffer = ByteArray(1024)
                while (iss.read(buffer) > 0) {
                    os.write(buffer)
                }
                os.flush()
                os.close()
                iss.close()
            }catch (e: IOException){
                throw RuntimeException("Error opening db")
            }
        }
        database = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun importInto(vocabDao: VocabDao) {
        val selectQuery = "SELECT * from vocab"
        val c = database.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            val vocabs = ArrayList<Vocab>()

            val levelIdx = c.getColumnIndex("level")
            val contentIdx = c.getColumnIndex("content")
            val pronounceIdx = c.getColumnIndex("pronounce")
            val typeIdx = c.getColumnIndex("type")
            val definitionIdx = c.getColumnIndex("definition")

            do {
                vocabs.add(Vocab(
                    bucket = "N${c.getInt(levelIdx)}",
                    content = c.getString(contentIdx),
                    pronounce = c.getString(pronounceIdx),
                    type = c.getString(typeIdx),
                    definition = c.getString(definitionIdx)
                ))
            } while (c.moveToNext())

            val array = arrayOfNulls<Vocab>(vocabs.size)
            vocabs.toArray(array)

            vocabDao.insertAll(*array as Array<Vocab>)
        }
    }

    companion object {
        val DATABASE_NAME = "nx.vocab.db"
    }
}