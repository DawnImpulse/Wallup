/*
 * Copyright 2017 Saksham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stonevire.wallup.activities

import android.animation.Animator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.balysv.materialripple.MaterialRippleLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.stonevire.wallup.R
import com.stonevire.wallup.fragments.CuratedFragment
import com.stonevire.wallup.fragments.LatestFragment
import com.stonevire.wallup.fragments.TrendingFragment
import com.stonevire.wallup.network.volley.VolleyWrapper

import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged

/**
 * Created by DawnImpulse
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - Code edit
 */

class MainActivity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.container)
    internal var viewPager: ViewPager? = null
    @BindView(R.id.tabs)
    internal var tabLayout: TabLayout? = null
    @BindView(R.id.include_search_layout_powered_text)
    internal var includeSearchLayoutPoweredText: LinearLayout? = null
    @BindView(R.id.include_search_layout_recycler)
    internal var includeSearchLayoutRecycler: RecyclerView? = null
    @BindView(R.id.include_search_layout_external)
    internal var includeSearchLayoutExternal: LinearLayout? = null
    @BindView(R.id.activity_main_search_overflow)
    internal var activityMainSearchOverflow: RelativeLayout? = null
    @BindView(R.id.activity_main_search_close)
    internal var activityMainSearchClose: AppCompatImageView? = null
    @BindView(R.id.activity_main_voice_search)
    internal var activityMainVoiceSearch: AppCompatImageView? = null
    @BindView(R.id.activity_main_search_text)
    internal var activityMainSearchText: EditText? = null
    @BindView(R.id.nav_drawer_user_image)
    internal var navDrawerUserImage: SimpleDraweeView? = null
    @BindView(R.id.nav_drawer_user_full_name)
    internal var navDrawerUserFullName: TextView? = null
    @BindView(R.id.appbar)
    internal var appbar: AppBarLayout? = null
    @BindView(R.id.nav_drawer_collections)
    internal var navDrawerCollections: MaterialRippleLayout? = null
    @BindView(R.id.nav_drawer_profile)
    internal var navDrawerProfile: MaterialRippleLayout? = null
    @BindView(R.id.nav_drawer_live_images)
    internal var navDrawerLiveImages: MaterialRippleLayout? = null
    @BindView(R.id.nav_drawer_settings)
    internal var navDrawerSettings: MaterialRippleLayout? = null
    //-------------------------------------------------

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mVolleyWrapper: VolleyWrapper? = null
    private var params: Map<String, String>? = null
    private var searchTextBoxEmpty = true // to check if edit text is currently empty or not

    /**
     * On Create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mVolleyWrapper = VolleyWrapper(this)
        params = HashMap()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        activityMainVoiceSearch!!.setColorFilter(ContextCompat.getColor(this, R.color.white))
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        viewPager!!.offscreenPageLimit = 2

        toolbar!!.setOnTouchListener(this)
        activityMainSearchOverflow!!.setOnTouchListener(this)

        //Navigation drawer enabled
        initNavigationDrawer()

        activityMainSearchClose!!.setOnClickListener(this)
        activityMainVoiceSearch!!.setOnClickListener(this)
        navDrawerProfile!!.setOnClickListener(this)
        navDrawerCollections!!.setOnClickListener {
            val intent = Intent(this@MainActivity, CollectionsActivity::class.java)
            startActivity(intent)
        }
        navDrawerLiveImages!!.setOnClickListener(this)
        navDrawerSettings!!.setOnClickListener(this)

    }

    /**
     * Menu Options Create
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Menu Options Click
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var anim: Animator? = null
        var cx = 0
        var cy = 0
        var radius = 0f

        if (item.itemId == R.id.search) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                cx = activityMainSearchOverflow!!.width
                cy = activityMainSearchOverflow!!.height / 2
                radius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                anim = ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, 0f, radius)

                activityMainSearchOverflow!!.visibility = View.VISIBLE
                toolbar!!.visibility = View.GONE
                anim!!.start()
                includeSearchLayoutExternal!!.visibility = View.VISIBLE
                tabLayout!!.visibility = View.GONE
                viewPager!!.visibility = View.INVISIBLE
            } else {
                activityMainSearchOverflow!!.visibility = View.VISIBLE
                toolbar!!.visibility = View.GONE
                includeSearchLayoutExternal!!.visibility = View.VISIBLE
                tabLayout!!.visibility = View.GONE
                viewPager!!.visibility = View.INVISIBLE
            }
        }

        if (item.itemId == R.id.action_about) {
            val intent = Intent(this@MainActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        //Intent to Live Images Activity
        if (item.itemId == R.id.action_live_images) {
            val intent = Intent(this@MainActivity, LiveImagesActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * On Activity Result - for voice search text
     *
     * @param requestCode,resultCode,data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results[0]

            activityMainSearchText!!.setText(spokenText)
            activityMainVoiceSearch!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_close))
            searchTextBoxEmpty = false
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    /**
     * On Text Change - listen to text change on search text
     *
     * @param text
     */
    @OnTextChanged(R.id.activity_main_search_text)
    protected fun onTextChanged(text: CharSequence) {
        if (text.toString().length != 0) {
            activityMainVoiceSearch!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_close))
            searchTextBoxEmpty = false
        } else {
            activityMainVoiceSearch!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_microphone))
            activityMainVoiceSearch!!.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }
    }

    /**
     * On Touch Listener - Use to make sure only the visible layout is touchable not the other one
     *
     * @param v,event
     * @return true/false
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (v.id) {
            R.id.activity_main_search_overflow -> return if (activityMainSearchOverflow!!.visibility == View.VISIBLE) {
                false
            } else {
                true
            }
            R.id.toolbar -> return if (toolbar!!.visibility == View.VISIBLE) {
                false
            } else {
                true
            }
        }
        return false
    }

    /**
     * Back press - S/W
     *
     * @return
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * Back Pressed - H/W also for search bar
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        if (activityMainSearchOverflow!!.visibility == View.VISIBLE) {
            if (searchTextBoxEmpty)
                closeSearchBar()
            else
                activityMainSearchText!!.text = null //empty text
        } else {
            finish()
        }
    }

    /**
     * Setting Up ViewPager
     *
     * @param viewPager
     */
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(LatestFragment(), "LATEST")
        adapter.addFragment(TrendingFragment(), "TRENDING")
        adapter.addFragment(CuratedFragment(), "CURATED")

        viewPager.adapter = adapter
    }

    /**
     * On Click  - search close, voice search, drawer profile,
     * drawer collection, drawer live image, drawer settings
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_main_search_close -> closeSearchBar()

            R.id.activity_main_voice_search -> if (searchTextBoxEmpty)
                displaySpeechRecognizer()
            else
                activityMainSearchText!!.setText(null)

            R.id.nav_drawer_profile -> {
            }

            R.id.nav_drawer_collections -> {
                val intent = Intent(this@MainActivity, CollectionsActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_drawer_live_images -> {
            }

            R.id.nav_drawer_settings -> {
            }
        }
    }

    /**
     * Fragments Adapter
     */
    private inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        internal fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    /**
     * Closing Search Bar with animation
     */
    private fun closeSearchBar() {

        var cx = 0
        var cy = 0
        var initialRadius = 0f
        var anim: Animator? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cx = activityMainSearchOverflow!!.width
            cy = activityMainSearchOverflow!!.height / 2
            initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            anim = ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, initialRadius, 0f)

            toolbar!!.visibility = View.VISIBLE
            anim!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    activityMainSearchOverflow!!.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })

            anim.start()
            tabLayout!!.visibility = View.VISIBLE
            includeSearchLayoutExternal!!.visibility = View.INVISIBLE
            viewPager!!.visibility = View.VISIBLE
        } else {
            toolbar!!.visibility = View.VISIBLE
            tabLayout!!.visibility = View.VISIBLE
            includeSearchLayoutExternal!!.visibility = View.INVISIBLE
            viewPager!!.visibility = View.VISIBLE
        }
    }

    /**
     * Displaying Speech Recognizer
     */
    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, 1)
    }

    /**
     * Navigation Drawer Initialize
     */
    fun initNavigationDrawer() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        val drawerLayout = findViewById<View>(R.id.main_content) as DrawerLayout

        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            override fun onDrawerClosed(v: View?) {
                super.onDrawerClosed(v)
            }

            override fun onDrawerOpened(v: View?) {
                super.onDrawerOpened(v)
            }
        }


        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

}
