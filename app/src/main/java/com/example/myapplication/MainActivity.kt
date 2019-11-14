package com.example.myapplication

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        GlobalResource.activity = this

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = object: FragmentPagerAdapter(supportFragmentManager) {

            val fragments = {
                val res = ArrayList<Fragment>()
                res.add(slideFragment)
                res.add(profileFragment)
                res.add(libraryFragment)
                res
            } ()

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }
            override fun getCount(): Int { return 3; }

        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.setCurrentItem(p0!!.position, false)
                invalidateOptionsMenu()
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when (viewPager.currentItem) {
            0 -> {
                menuInflater.inflate(R.menu.main_menu, menu)
            }
            else -> {
                menuInflater.inflate(R.menu.aux_menu, menu)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actionNight -> {
            val enabled = !GlobalResource.prefs.getBoolean("night_mode_enabled", false)
            GlobalResource.prefs.edit().putBoolean("night_mode_enabled", enabled).apply()
            if (enabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
//            delegate.applyDayNight()
//            recreate()
            true
        }
        R.id.actionRefresh -> {
            GlobalResource.vocabs = VocabSelector.selectByCurrentProfile()
            slideFragment.updateVocabs()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val slideFragment = {
            val x = SlideFragment()
            x.retainInstance = true
            x
        } ()
        val profileFragment = {
            val x = ProfileFragment()
            x.retainInstance = true
            x
        } ()
        val libraryFragment = {
            val x = LibraryFragment()
            x.retainInstance = true
            x
        } ()
    }
}
