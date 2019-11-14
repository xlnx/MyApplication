package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.LibraryCardViewBinding

class LibraryCardView : LinearLayout {

    data class BucketMeta (
        val name: String,
        val total: Int,
        val starred: Int,
        val isBookmarks: Boolean = false
    )

    val binding: LibraryCardViewBinding

    constructor(context: Context, bucket: BucketMeta): super(context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.library_card_view, this, true)
        binding.bucket = bucket

        findViewById<CardView>(R.id.cardView).apply {
            setOnClickListener({_->
                val intent = Intent(GlobalResource.activity, VocabListActivity::class.java).apply {
                    putExtra("name", bucket.name)
                    putExtra("isBookmarks", bucket.isBookmarks)
                }
                GlobalResource.activity.startActivity(intent)
            })
        }
    }
}