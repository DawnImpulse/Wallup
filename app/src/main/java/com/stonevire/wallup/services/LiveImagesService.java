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

package com.stonevire.wallup.services;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.singleton.WallpaperServiceSingleton;
import com.stonevire.wallup.utils.Const;

import timber.log.BuildConfig;
import timber.log.Timber;

/**
 * Created by Saksham on 7/18/2017.
 */

public class LiveImagesService extends WallpaperService {
    int j = 0;
    SurfaceHolder mSurfaceHolder;
    LiveImagesServiceEngine1 mEngine1;
    LiveImagesServiceEngine2 mEngine2;

    @Override
    public Engine onCreateEngine() {
        if (!Fresco.hasBeenInitialized())
            Fresco.initialize(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Log.d("Wallup", "On Create Engine");

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (j == 0) {
            mEngine1 = new LiveImagesServiceEngine1();
            j = 1;
            return mEngine1;
        } else {
            mEngine2 = new LiveImagesServiceEngine2();
            j = 0;
            return mEngine2;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test", "In Destroy");
    }

    private class LiveImagesServiceEngine1 extends Engine {
        int i = 1;
        boolean drawOk;
        int cachedSize = 5;

        public LiveImagesServiceEngine1() {
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Test", "In Destroy " + i);

        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            cachedSize = Prefs.getInt(Const.IMAGES_CACHE_SIZE, 5);
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mSurfaceHolder = holder;
            Log.d("Test", "Surface Created " + i);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (j != 0)
                WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed " + i);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), mSurfaceHolder);
                Log.d("Test", "Surface Visibility True " + i);
            } else {
                WallpaperServiceSingleton.getInstance().removeHandler();
                Log.d("Test", "Surface Visibility False" + i);
            }

        }


        boolean wallpaperWasSetByAnotherApp() {
            WallpaperManager wp_mngr = WallpaperManager.getInstance(getApplicationContext());
            WallpaperInfo info = wp_mngr.getWallpaperInfo();
            if (info == null) {
                // wallpaper is a static image
                return true;
            } else {
                // wallpaper is live, check implementing service
                return !info.getComponent().equals(new ComponentName(getApplicationContext(), getClass()));
            }
        }
    }

    private class LiveImagesServiceEngine2 extends Engine {
        int i = 2;
        boolean drawOk;
        int cachedSize = 5;

        public LiveImagesServiceEngine2() {

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Test", "In Destroy " + i);

        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            cachedSize = Prefs.getInt(Const.IMAGES_CACHE_SIZE, 5);
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("Test", "Surface Created " + i);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (j != 1)
                WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed " + i);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
                Log.d("Test", "Surface Visibility True " + i);
            } else {
                WallpaperServiceSingleton.getInstance().removeHandler();
                Log.d("Test", "Surface Visibility False" + i);
            }

        }


        boolean wallpaperWasSetByAnotherApp() {
            WallpaperManager wp_mngr = WallpaperManager.getInstance(getApplicationContext());
            WallpaperInfo info = wp_mngr.getWallpaperInfo();
            if (info == null) {
                // wallpaper is a static image
                return true;
            } else {
                // wallpaper is live, check implementing service
                return !info.getComponent().equals(new ComponentName(getApplicationContext(), getClass()));
            }
        }
    }

}
