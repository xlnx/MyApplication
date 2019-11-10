package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.varunest.sparkbutton.SparkButton

class MainActivity : AppCompatActivity() {

    class CardAdapter(private val context: Context) : PagerAdapter() {
        private lateinit var items: ArrayList<Vocab>
        private lateinit var views: ArrayList<View?>

        var vocabs: ArrayList<Vocab>
            get() = items
            set(newVocabs) {
                items = newVocabs
                views = newVocabs.map {null}.toCollection(ArrayList())
            }

        override fun getCount(): Int {
            return items.size
        }
        override fun isViewFromObject(view: View, other: Any): Boolean {
            return view == other
        }
        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
            views.remove(view)
        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.vocab_card, container, false)
            container.addView(view)
            bind(items[position], view)
            views[position] = view
            return view
        }
        private fun bind(data: Vocab, view: View) {
            val contentLabel = view.findViewById<TextView>(R.id.contentLabel)
            val pronounceLabel = view.findViewById<TextView>(R.id.pronounceLabel)
            val definitionLabel = view.findViewById<TextView>(R.id.definitionLabel)
            var starButton = view.findViewById<SparkButton>(R.id.starButton)
            contentLabel.text = data.content
            pronounceLabel.text = data.pronounce
            definitionLabel.text = data.definition
            starButton.setChecked(data.star)
//            val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
        }
    }

    private lateinit var db: AppDatabase
    private lateinit var viewPager: ViewPager
    private lateinit var cardAdapter: CardAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("com.example.myapplication", MODE_PRIVATE)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app.db"
        )
            .allowMainThreadQueries()
            .build()

        if (!prefs.getBoolean("jlpt_imported", false)) {
            val jlpt = JlptImporter(this)
            jlpt.importInto(db.vocabDao())
            prefs.edit().putBoolean("jlpt_imported", true).commit()
        }

        cardAdapter = CardAdapter(this)
        cardAdapter.vocabs = db.vocabDao().getAllByBucket("N4").toCollection(ArrayList())
//            .getNxVocabList(5)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = cardAdapter
    }

    fun sendMessage(view: View) {
//        val editText = findViewById<EditText>(R.id.editText)
//        val message = editText.text.toString()
//        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
//            val n5 = db.getNxVocabList(5)[0]
//            putExtra(EXTRA_MESSAGE, "${n5.id}|${n5.level}|${n5.pronounce}|${n5.content}|${n5.type}|${n5.definition}")
////                message)
//        }
//        startActivity(intent)
    }
}
