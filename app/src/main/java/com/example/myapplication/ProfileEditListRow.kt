package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ProfileEditListRowBinding
import com.example.myapplication.databinding.ProfileListRowBinding
import kotlinx.coroutines.selects.select

class ProfileEditListRow : LinearLayout {

    data class ProfileEntry (
        var bucket: String,
        var ratio: Int
    )

    val binding: ProfileEditListRowBinding
    val entry: ProfileEntry

    constructor(context: Context, buckets: ArrayList<String>, profileEntry: ProfileEntry,
                onRemove: (View) -> Unit): super(context) {
        entry = profileEntry
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_edit_list_row, this, true)
        binding.apply {
            entry = profileEntry
            candidates = buckets
        }

        findViewById<CardView>(R.id.profileEntryCard).setOnLongClickListener { _->
            onRemove(this)
            true
        }

        findViewById<Spinner>(R.id.bucketSpinner).apply {
            val index = buckets.indexOf(profileEntry.bucket)
            post( { setSelection(index) })
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    profileEntry.bucket = parent?.getItemAtPosition(position) as String
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        }
    }

}