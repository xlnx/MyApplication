package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ProfileListRowBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfileFragment : Fragment() {

    lateinit var fn: ()->Unit

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val self = this
        val listView = view?.findViewById<ListView>(R.id.profileListView)!!

        class Adapter: BaseAdapter(), PopupMenu.OnMenuItemClickListener {

            lateinit var profile: Profile

            override fun getCount(): Int {
                return GlobalResource.profiles.size
            }
            override fun getItem(position: Int): Any {
                return position
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var binding = if (convertView == null) {
                    val binding: ProfileListRowBinding =
                        DataBindingUtil.inflate(layoutInflater, R.layout.profile_list_row, null, false)
                    binding.root.tag = binding
                    binding
                } else {
                    convertView.tag as ProfileListRowBinding
                }
                val self1 = this
                profile = GlobalResource.profiles[position]
                val inner = profile
                binding.profile = profile
                binding.root.findViewById<CardView>(R.id.profileEntryCard)!!.apply {
                    setOnClickListener({ _ ->
                        if (inner.editable) {
                            val intent =
                                Intent(
                                    GlobalResource.activity,
                                    ProfileEditActivity::class.java
                                ).apply {
                                    putExtra("id", inner.id)
                                }
                            self.startActivityForResult(intent, 0)
                        }
                    })
                    setOnLongClickListener { v ->
//                        AlertDialog.Builder(self.activity)
//                            .setMessage(profile.toString())
//                            .create()
//                            .show()
                        if (inner.editable) {
                            val popup = PopupMenu(self.activity, v)
                            popup.setOnMenuItemClickListener(self1)
                            popup.inflate(R.menu.profile_ops_menu)
                            popup.show()
                        } else {
                            GlobalResource.prefs.edit()
                                .putInt("profile", inner.id)
                                .apply()
                        }
                        true
                    }
                }
                return binding.root
            }

            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.set -> {
                        GlobalResource.prefs.edit()
                            .putInt("profile", profile.id)
                            .apply()
                    }
                    R.id.delete -> {
                        GlobalResource.apply {
                            db.profileDao().delete(profile)
                            if (profile.id == prefs.getInt("profile", 0)) {
                                prefs.edit()
                                    .putInt("profile", db.profileDao().selectDefault()[0].id)
                                    .apply()
                            }
                            profiles.clear()
                            profiles.addAll(db.profileDao().select())
                            listView.adapter = Adapter()
                        }
                    }
                }
                return true
            }
        }
        GlobalResource.apply {
            fn = {
                profiles.clear()
                profiles.addAll(db.profileDao().select())
                listView.adapter = Adapter()
            }
        }
        listView.adapter = Adapter()

        view?.findViewById<FloatingActionButton>(R.id.floatingActionButton)?.setOnClickListener { _ ->
            GlobalResource.apply {
                db.profileDao().insert(Profile(name="Untitled"))
                profiles.clear()
                profiles.addAll(db.profileDao().select())
            }
            listView.adapter = Adapter()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }
}
