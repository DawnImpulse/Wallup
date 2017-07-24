package com.stonevire.wallup.services;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
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
    int j = 0;
    LiveImagesServiceEngine1 mEngine;

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

        //e = new LiveImagesServiceEngine();
        if (j == 0) {
            mEngine = new LiveImagesServiceEngine1();
            return mEngine;
        } else
            return new LiveImagesServiceEngine2();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test", "In Destroy");
    }

    private class LiveImagesServiceEngine1 extends Engine {
        int i = 1;
        File mFile;
        boolean drawOk;
        boolean earlyFetch = false;
        boolean saveDraw = false;
        int position;
        int cachedSize = 5;
        SurfaceHolder mHolder;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = wm.getDefaultDisplay();
        private LiveImagesServiceEngine1 mLiveImagesServiceEngine;
        private Handler handler = new Handler();
        private boolean visible = true;

        public LiveImagesServiceEngine1() {
            j = 1;
        }

        SurfaceHolder.Callback view;

        @Override
        public boolean isPreview() {
            return super.isPreview();
        }


       /* @Override
        public void onDestroy() {
            synchronized (this) {
                super.onDestroy();
                if (drawOk)
                    onVisibilityChanged(true);
                Log.d("Test", "In Destroy " + i);
            }
        }*/

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            if (isPreview())
                i = 1;
            cachedSize = Prefs.getInt(Const.LIVE_IMAGES_CACHE_SIZE, 5);
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            if (j == 1) {
                //getSurfaceHolder().removeCallback(7/);
            }
            Log.d("Test", "Surface Created " + i);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            //WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed " + i);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                //handler.post(drawRunner);
                WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
                drawOk = true;
                Log.d("Test", "Surface Visibility True " + i);
            } else {
                //handler.removeCallbacks(drawRunner);
                WallpaperServiceSingleton.getInstance().removeHandler();
                drawOk = false;
                Log.d("Test", "Surface Visibility False" + i);
            }

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
            Log.d("Test", "Surface Changed");

        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
            WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
            Log.d("Test", "Surface Redraw Needed");

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
        File mFile;
        boolean drawOk;
        boolean earlyFetch = false;
        boolean saveDraw = false;
        int position;
        int cachedSize = 5;
        SurfaceHolder mHolder;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = wm.getDefaultDisplay();
        private Handler handler = new Handler();
        private boolean visible = true;

        public LiveImagesServiceEngine2() {
            j = 0;
        }

        @Override
        public boolean isPreview() {
            return super.isPreview();
        }


        /*@Override
        public void onDestroy() {
            synchronized (this) {
                super.onDestroy();
                if (drawOk)
                    onVisibilityChanged(true);
                Log.d("Test", "In Destroy " + i);
            }
        }*/

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            if (isPreview())
                i = 1;
            cachedSize = Prefs.getInt(Const.LIVE_IMAGES_CACHE_SIZE, 5);
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
            WallpaperServiceSingleton.getInstance().removeHandler();
            Log.d("Test", "Surface Destroyed " + i);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {

            super.onVisibilityChanged(visible);
            if (visible) {
                //handler.post(drawRunner);
                WallpaperServiceSingleton.getInstance().WallpaperHandler(getApplicationContext(), getSurfaceHolder());
                drawOk = true;
                Log.d("Test", "Surface Visibility True " + i);
            } else {
                //handler.removeCallbacks(drawRunner);
                WallpaperServiceSingleton.getInstance().removeHandler();
                drawOk = false;
                Log.d("Test", "Surface Visibility False" + i);

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
            Log.d("Test", "Surface Redraw Needed");

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
