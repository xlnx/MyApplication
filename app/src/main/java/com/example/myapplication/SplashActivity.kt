package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import java.util.*
import kotlin.collections.ArrayList

class GlobalResource {
    companion object {
        lateinit var db: AppDatabase
        lateinit var tts: TextToSpeech
        lateinit var activity: MainActivity
        lateinit var prefs: SharedPreferences
        lateinit var vocabs: ArrayList<Vocab>
        lateinit var buckets: ArrayList<LibraryCardView.BucketMeta>
        lateinit var profiles: ArrayList<Profile>
    }
}

class SplashActivity : Activity(), TextToSpeech.OnInitListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        println("recreate")
        val ctx = this

        GlobalResource.apply {

            prefs =
                getSharedPreferences("com.example.myapplication", AppCompatActivity.MODE_PRIVATE)
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "app.db"
            )
                .allowMainThreadQueries()
                .build()
            tts = TextToSpeech(applicationContext, ctx)

            if (!prefs.getBoolean("jlpt_imported", false)) {
                val jlpt = JlptImporter(ctx)
                jlpt.importInto(db.vocabDao())
                prefs.edit().putBoolean("jlpt_imported", true).apply()
            }

            if (prefs.getBoolean("night_mode_enabled", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            buckets = ArrayList()
            buckets.apply {
                val starred = VocabSelector()
                    .setStarred()
                    .count()
                add(
                    LibraryCardView.BucketMeta(
                        name = "Bookmarks",
                        total = starred,
                        starred = starred,
                        isBookmarks = true
                    )
                )
                for (name in db.vocabDao().selectBuckets()) {
                    val starred = VocabSelector()
                        .addBucket(name)
                        .setShuffle(false)
                        .setStarred()
                        .count()
                    val total = VocabSelector()
                        .addBucket(name)
                        .setShuffle(false)
                        .count()
                    println(total)
                    add(
                        LibraryCardView.BucketMeta(
                            name = name,
                            total = total,
                            starred = starred
                        )
                    )
                }
            }

            profiles = ArrayList()
            db.profileDao().select().toCollection(profiles)
            if (profiles.isEmpty()) {
                db.profileDao().insert(Profile(editable = false, name = "Random"))
                val profile = db.profileDao().select()[0]
                profiles.add(profile)
                prefs.edit()
                    .putInt("profile", profile.id)
                    .apply()
            }

            vocabs = VocabSelector.selectByCurrentProfile()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            GlobalResource.tts.setLanguage(Locale.JAPANESE)
        }
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}