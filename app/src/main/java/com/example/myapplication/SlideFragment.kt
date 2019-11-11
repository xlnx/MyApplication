package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.varunest.sparkbutton.SparkButton

class SlideFragment :Fragment() {

    class CardAdapter(private val context: Context, var items: ArrayList<Vocab>) : PagerAdapter() {

        override fun getCount(): Int {
            return items.size
        }
        override fun isViewFromObject(view: View, other: Any): Boolean {
            return view == other
        }
        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.vocab_card, container, false)
            container.addView(view)
            bind(items[position], view)
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
        }
    }

    private lateinit var cardAdapter: CardAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cardAdapter = CardAdapter(
            context = activity!!,
            items = VocabSelector()
                .addBucket("N5")
                .setLimit()
                .setShuffle(false)
                .select()
        )

        view!!.findViewById<ViewPager>(R.id.viewPager).adapter = cardAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slice, container, false)
    }
}
