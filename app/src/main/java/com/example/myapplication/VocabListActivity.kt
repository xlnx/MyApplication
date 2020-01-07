package com.example.myapplication

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ListColumnBinding
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkEventListener
import me.xdrop.fuzzywuzzy.FuzzySearch


class VocabListActivity : AppCompatActivity() {

//    private lateinit var vocabs: ArrayList<Vocab>

    private lateinit var listView: ListView
    private lateinit var vocabs: ArrayList<Vocab>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vocab_list_activity)

        listView = findViewById(R.id.vocabListView)

        val name = intent.extras!!["name"] as String
        val isBookmarks = intent.extras!!["isBookmarks"] as Boolean

        title = name
        vocabs = if (isBookmarks) {
            VocabSelector()
                .setStarred()
                .setShuffle(false)
                .select()
        } else {
            VocabSelector()
                .addBucket(name)
                .setShuffle(false)
                .select()
        }

        var strings = vocabs.map { x -> "${x.content}" }.toCollection(ArrayList())
        var indices = vocabs.mapIndexed { index, _ -> index }.toCollection(ArrayList())
        val inflater = LayoutInflater.from(this)

        listView.adapter = object : BaseAdapter(), Filterable {

            override fun getCount(): Int {
                return indices.size
            }
            override fun getItem(position: Int): Any {
                return position
            }
            override fun getItemId(position: Int): Long {
                return position.toLong()
            }
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var binding = if (convertView == null) {
                    val binding: ListColumnBinding = DataBindingUtil.inflate(inflater, R.layout.list_column, null, false)
                    binding.root.tag = binding
                    binding
                } else {
                    convertView.tag as ListColumnBinding
                }
                binding.vocab = vocabs[indices[position]]
                binding.root.findViewById<SparkButton>(R.id.starButton).apply {
                    isChecked = binding.vocab!!.star
                    setEventListener(object: SparkEventListener {
                        override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {}
                        override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {}
                        override fun onEvent(button: ImageView?, buttonState: Boolean) {
                            binding.vocab!!.star = buttonState
                            GlobalResource.db.vocabDao().update(binding.vocab!!)
                        }
                    })
                }
//                        .setImageResource(if (binding.vocab!!.star) {
//                            R.drawable.baseline_star_24dp
//                        } else {
//                            android.R.color.transparent
//                        })
                return binding.root
            }
            override fun getFilter(): Filter {
                return object: Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        indices = if (constraint.isNullOrEmpty()) {
                            vocabs.mapIndexed { index, _ -> index }.toCollection(ArrayList())
                        } else {
                            FuzzySearch.extractAll(constraint.toString().toLowerCase(), strings, 60)
                                .map { x -> x.index }.toCollection(ArrayList())
                        }
                        return FilterResults()
                    }
                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        notifyDataSetChanged()
                    }
                }
            }

        }
//            FilterListAdapter(this, vocabs.map { x->x.content }.toCollection(ArrayList()))

//        FuzzySearch.extractAll("asd", vocabs, {x->"${x.content}|${x.pronounce}"}, 50)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.vocab_list_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.apply {
            setSearchableInfo(manager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    (listView.adapter as Filterable).filter.filter(query)
                    return false
                }
                override fun onQueryTextSubmit(query: String?): Boolean {
                    (listView.adapter as Filterable).filter.filter(query)
//                    listView.adapter = FilterListAdapter(context, res)
//                    clearFocus()
//                    setQuery("", false)
//                    Toast.makeText(context, "Looking for $query", Toast.LENGTH_LONG).show()
//
                    return false
                }
            })
        }

        return true

    }
}
