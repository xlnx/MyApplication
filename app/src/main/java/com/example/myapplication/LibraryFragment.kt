package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class LibraryFragment : Fragment() {

    class LibraryAdapter(context: Context, private val data: ArrayList<LibraryCardView.BucketMeta>) :
            ArrayAdapter<LibraryCardView.BucketMeta>(context, R.layout.library_card_view, data) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return LibraryCardView(context, data[position])
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<ListView>(R.id.listView)?.adapter = LibraryAdapter(activity!!, GlobalResource.buckets)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.library_fragment, container, false)
    }
}