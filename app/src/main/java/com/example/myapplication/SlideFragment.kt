package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class SlideFragment: Fragment() {

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
            val view = VocabCardView(context, items[position])
            container.addView(view)
            return view
        }
    }

    fun updateVocabs() {
        view?.findViewById<ViewPager>(R.id.viewPager)?.adapter = CardAdapter(activity!!, GlobalResource.vocabs)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        updateVocabs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.slides_fragment, container, false)
    }
}
