package com.stonevire.wallup.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.singleton.WallpaperServiceSingleton;
import com.stonevire.wallup.utils.Const;

import java.io.File;

import timber.log.BuildConfig;
import timber.log.Timber;

/**
 * Created by Saksham on 7/18/2017.
 */

public class LiveImagesService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        if (!Fresco.hasBeenInitialized())
            Fresco.initialize(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        return new LiveImagesServiceEngine();
    }

    private class LiveImagesServiceEngine extends Engine {


        private Handler handler = new Handler();
        File mFile;

        private boolean visible = true;
        boolean drawOk;
        boolean earlyFetch = false;
        boolean saveDraw = false;
        int position;
        int cachedSize = 5;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = wm.getDefaultDisplay();


        public LiveImagesServiceEngine() {
            cachedSize = Prefs.getInt(Const.LIVE_IMAGES_CACHE_SIZE, 5);
            //mFile = directory();
//            handler.post(drawRunner);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("Test", "Surface Created");
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                //handler.post(drawRunner);
                WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
                drawOk = true;
                Log.d("Test", "Surface Visibility True");
            } else {
                //handler.removeCallbacks(drawRunner);
                WallpaperServiceSingleton.getInstance().removeHandler();
                drawOk = false;
                Log.d("Test", "Surface Visibility False");
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d("Test", "Surface Changed");
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
        }
    }
}
