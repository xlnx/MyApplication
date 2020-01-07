package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.JsonWriter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ProfileEditActivityBinding
import org.json.JSONArray
import java.io.StringWriter

class ProfileEditActivity : Activity() {

    val views: ArrayList<ProfileEditListRow> = ArrayList()
    val buckets: ArrayList<String> = ArrayList()
    lateinit var binding: ProfileEditActivityBinding

    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.profile_edit_activity)

        val id = intent.extras!!["id"] as Int
        val profiles = GlobalResource.db.profileDao().selectById(id)
        assert(profiles.size == 1)
        profile = profiles[0]
        binding.profile = profile

        buckets.clear()
        GlobalResource.db.vocabDao().selectBuckets().toCollection(buckets)
        buckets.add("*")

        val listView = findViewById<ListView>(R.id.profileListView)!!

        class Adapter: ArrayAdapter<ProfileEditListRow>(this, R.layout.profile_edit_list_row, views) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return views[position]
            }
        }
        listView.adapter = Adapter()

        val onRemove = { view: View ->
            views.remove(view)
            listView.adapter = Adapter()
        }

        findViewById<Button>(R.id.addButton)!!.setOnClickListener({_->
            views.add(
                ProfileEditListRow(
                    this,
                    buckets,
                    ProfileEditListRow.ProfileEntry("*", 100),
                    onRemove
                )
            )
            listView.adapter = Adapter()
        })
        val arr = JSONArray(profile.profile)
        for (i in 0.until(arr.length())) {
            val obj = arr.getJSONObject(i)
            views.add(
                ProfileEditListRow(
                    this,
                    buckets,
                    ProfileEditListRow.ProfileEntry(obj.getString("bucket"), obj.getInt("ratio")),
                    onRemove
                )
            )
        }

        findViewById<Button>(R.id.confirmButton)!!.setOnClickListener({_->
            val sw = StringWriter()
            val jw = JsonWriter(sw)
            jw.beginArray()
            for (view in views) {
                jw.beginObject()
                jw.name("bucket").value(view.entry.bucket)
                jw.name("ratio").value(view.entry.ratio)
                jw.endObject()
            }
            jw.endArray()
            jw.flush()
            profile.profile = sw.buffer.toString()
            GlobalResource.db.profileDao().update(profile)
            finish()
        })
    }
}