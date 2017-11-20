package com.stonevire.wallup.handlers;

import android.content.Context;
import android.graphics.Bitmap;

import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.interfaces.OnCallbackListener;
import com.stonevire.wallup.network.UnsplashFetcher;
import com.stonevire.wallup.utils.Const;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

public class ImageHandler {

    /**
     * Fetches an image from unsplash and set it as wallpaper
     */
    public static void fetchAndSetImage(final Context mContext) {
        UnsplashFetcher unsplashFetcher;
        unsplashFetcher = new UnsplashFetcher(mContext);

        unsplashFetcher.fetchImages(Prefs.getString(Const.WALLPAPER_URL, ""), 1, Const.CALLBACK_1);
        unsplashFetcher.onCallbackListener(new OnCallbackListener() {
            @Override
            public <E> void onCallback(JSONObject error, E response, int callbackId) {
                if (error != null) {
                    fetchAndSetImage(mContext);
                    //log error
                } else {
                    CacheHandler.cacheBitmapAndVerify(UrlHandler.UnsplashUrlModify("url"), "name", mContext);
                }
            }
        });
    }


    /**
     * Set the first image in cache as wallpaper
     *
     * @param mContext
     */
    public static void setFirstImageInCache(Context mContext) {
        File[] filesList;
        File internalBitmapFiles = StorageHandler.getInternalBitmapsPath(mContext);

        if (internalBitmapFiles.listFiles().length > 0) {
            filesList = internalBitmapFiles.listFiles();

            //sorting the files list based on date modified
            Arrays.sort(filesList, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });

            Prefs.putString(Const.CURRENT_IMAGE_AS_WALLPAPER, filesList[0].getName());
            setImage(mContext);
        }
    }

    /**
     * * Set the next image in cache
     * + uses current image as wallpaper to find next
     *
     * @param mContext
     */
    public static void setImage(Context mContext) {
        Bitmap cachedBitmap = StorageHandler.getInternalBitmap(mContext,
                Prefs.getString(Const.CURRENT_IMAGE_AS_WALLPAPER, null));

        //if cached bitmap is null then delete it and set new wallpaper accordingly
        if (cachedBitmap == null) {
            StorageHandler.deleteFileInternally(mContext, Prefs.getString(Const.CURRENT_IMAGE_AS_WALLPAPER, null));
            if (StorageHandler.getBitmapsInCache(mContext) != 0)
                setFirstImageInCache(mContext);
            else
                fetchAndSetImage(mContext);
        } else
            try {
                WallpaperHandler.setHomescreenWallpaper(cachedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
