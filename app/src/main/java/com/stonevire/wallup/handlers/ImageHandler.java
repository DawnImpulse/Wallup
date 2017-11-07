package com.stonevire.wallup.handlers;

import android.content.Context;
import android.graphics.Bitmap;

import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.utils.Const;

import java.io.File;
import java.io.IOException;

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
    public static void fetchAndSetImage() {

    }


    /**
     * Set the first image in cache as wallpaper
     *
     * @param mContext
     */
    public static void setFirstImageInCache(Context mContext) {

        File internalBitmapFiles = StorageHandler.getInternalBitmapsPath(mContext);
        if (internalBitmapFiles.listFiles().length > 0) {
            //sort the files first and pick first image
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
                fetchAndSetImage();
        } else
            try {
                WallpaperHandler.setHomescreenWallpaper(cachedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


}
