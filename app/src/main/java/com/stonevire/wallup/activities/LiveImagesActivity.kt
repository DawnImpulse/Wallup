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

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView

import com.stonevire.wallup.R
import com.stonevire.wallup.services.LiveImagesService

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by DawnImpulse
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - Code edit
 */

class LiveImagesActivity : AppCompatActivity() {

    @BindView(R.id.activity_live_images_chevron_right)
    internal var activityLiveImagesChevronRight: AppCompatImageView? = null

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_images)
        ButterKnife.bind(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityLiveImagesChevronRight!!.setColorFilter(ContextCompat.getColor(this, R.color.black))
    }

    /**
     * View clicked - set live wallpaper
     */
    @OnClick(R.id.activity_live_images_set_wallpaper)
    fun onViewClicked() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this, LiveImagesService::class.java!!))
        startActivity(intent)
    }

    /**
     * On back press - H/W
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * On back press - S/W
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
