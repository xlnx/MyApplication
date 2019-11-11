package com.example.myapplication

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("com.example.myapplication", MODE_PRIVATE)
        AppDatabase.connect(applicationContext)

        if (!prefs.getBoolean("jlpt_imported", false)) {
            val jlpt = JlptImporter(this)
            jlpt.importInto(AppDatabase.instance.vocabDao())
            prefs.edit().putBoolean("jlpt_imported", true).commit()
        }
    }
}
