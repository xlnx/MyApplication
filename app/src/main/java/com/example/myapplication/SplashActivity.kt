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

class GlobalResource {
    companion object {
        lateinit var db: AppDatabase
        lateinit var tts: TextToSpeech
        lateinit var activity: MainActivity
        lateinit var prefs: SharedPreferences
        lateinit var vocabs: ArrayList<Vocab>
    }
}

class SplashActivity : Activity(), TextToSpeech.OnInitListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        println("recreate")

        GlobalResource.prefs = getSharedPreferences("com.example.myapplication", AppCompatActivity.MODE_PRIVATE)
        GlobalResource.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app.db"
        )
            .allowMainThreadQueries()
            .build()
        GlobalResource.tts = TextToSpeech(applicationContext, this)

        if (!GlobalResource.prefs.getBoolean("jlpt_imported", false)) {
            val jlpt = JlptImporter(this)
            jlpt.importInto(GlobalResource.db.vocabDao())
            GlobalResource.prefs.edit().putBoolean("jlpt_imported", true).apply()
        }

        if (GlobalResource.prefs.getBoolean("night_mode_enabled", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        GlobalResource.vocabs = VocabSelector.selectByCurrentProfile()
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