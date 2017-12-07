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

package com.stonevire.wallup.services

import android.app.WallpaperInfo
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.ContextWrapper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder

import com.facebook.drawee.backends.pipeline.Fresco
import com.pixplicity.easyprefs.library.Prefs
import com.stonevire.wallup.singleton.WallpaperServiceSingleton
import com.stonevire.wallup.utils.Const

import timber.log.BuildConfig
import timber.log.Timber

/**
 * Created by Saksham on 7/18/2017.
 */

class LiveImagesService : WallpaperService() {
    internal var j = 0
    internal var mSurfaceHolder: SurfaceHolder
    internal var mEngine1: LiveImagesServiceEngine1
    internal var mEngine2: LiveImagesServiceEngine2

    override fun onCreateEngine(): WallpaperService.Engine {
        if (!Fresco.hasBeenInitialized())
            Fresco.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Log.d("Wallup", "On Create Engine")

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()

        if (j == 0) {
            mEngine1 = LiveImagesServiceEngine1()
            j = 1
            return mEngine1
        } else {
            mEngine2 = LiveImagesServiceEngine2()
            j = 0
            return mEngine2
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Test", "In Destroy")
    }

    private inner class LiveImagesServiceEngine1 : WallpaperService.Engine() {
        internal var i = 1
        internal var drawOk: Boolean = false
        internal var cachedSize = 5

        override fun onDestroy() {
            super.onDestroy()
            Log.d("Test", "In Destroy " + i)

        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            cachedSize = Prefs.getInt(Const.IMAGES_CACHE_SIZE, 5)
            super.onCreate(surfaceHolder)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            mSurfaceHolder = holder
            Log.d("Test", "Surface Created " + i)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (j != 0)
                WallpaperServiceSingleton.instance.removeHandler()
            Log.d("Test", "Surface Destroyed " + i)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                WallpaperServiceSingleton.instance.WallpaperHandler(applicationContext, mSurfaceHolder)
                Log.d("Test", "Surface Visibility True " + i)
            } else {
                WallpaperServiceSingleton.instance.removeHandler()
                Log.d("Test", "Surface Visibility False" + i)
            }

        }


        internal fun wallpaperWasSetByAnotherApp(): Boolean {
            val wp_mngr = WallpaperManager.getInstance(applicationContext)
            val info = wp_mngr.wallpaperInfo
            return if (info == null) {
                // wallpaper is a static image
                true
            } else {
                // wallpaper is live, check implementing service
                info.component != ComponentName(applicationContext, javaClass)
            }
        }
    }

    private inner class LiveImagesServiceEngine2 : WallpaperService.Engine() {
        internal var i = 2
        internal var drawOk: Boolean = false
        internal var cachedSize = 5

        override fun onDestroy() {
            super.onDestroy()
            Log.d("Test", "In Destroy " + i)

        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            cachedSize = Prefs.getInt(Const.IMAGES_CACHE_SIZE, 5)
            super.onCreate(surfaceHolder)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.d("Test", "Surface Created " + i)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (j != 1)
                WallpaperServiceSingleton.instance.removeHandler()
            Log.d("Test", "Surface Destroyed " + i)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                WallpaperServiceSingleton.instance.WallpaperHandler(applicationContext, surfaceHolder)
                Log.d("Test", "Surface Visibility True " + i)
            } else {
                WallpaperServiceSingleton.instance.removeHandler()
                Log.d("Test", "Surface Visibility False" + i)
            }

        }


        internal fun wallpaperWasSetByAnotherApp(): Boolean {
            val wp_mngr = WallpaperManager.getInstance(applicationContext)
            val info = wp_mngr.wallpaperInfo
            return if (info == null) {
                // wallpaper is a static image
                true
            } else {
                // wallpaper is live, check implementing service
                info.component != ComponentName(applicationContext, javaClass)
            }
        }
    }

}
