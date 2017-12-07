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

import android.content.Context
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout

import com.stonevire.wallup.R
import com.stonevire.wallup.utils.DisplayCalculations

import butterknife.BindView
import butterknife.ButterKnife

class AboutActivity : AppCompatActivity() {

    @BindView(R.id.content_about_linear1)
    internal var contentAboutLinear1: LinearLayout? = null
    @BindView(R.id.content_about_linear2)
    internal var contentAboutLinear2: LinearLayout? = null
    @BindView(R.id.toolbar_layout)
    internal var collapsingToolbar: CollapsingToolbarLayout? = null

    /**
     * On Create
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        settingWidth()
        collapsingToolbar!!.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
        collapsingToolbar!!.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white))

    }

    /**
     * Setting the width of linear layouts in Card 1
     */
    private fun settingWidth() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displaymetrics = DisplayMetrics()       //-------------------- Getting width of screen
        wm.defaultDisplay.getMetrics(displaymetrics)
        var width = displaymetrics.widthPixels
        width -= DisplayCalculations.dpToPx(16, this)

        val lp1 = contentAboutLinear1!!.layoutParams
        val lp2 = contentAboutLinear2!!.layoutParams

        lp1.width = width / 2
        lp1.height = width / 2
        lp2.width = width / 2
        lp2.height = width / 2

        contentAboutLinear1!!.requestLayout()
        contentAboutLinear2!!.requestLayout()
    }
}
