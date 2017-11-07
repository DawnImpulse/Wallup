package com.stonevire.wallup.handlers;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.utils.Const;

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

public class CacheHandler {

    /**
     * Handles the management of internal bitmap cache
     * +++ intermediate for setting live image(wallpaper)
     *
     * @param mContext
     */
    public static void setCacheHandler(Context mContext) {

        if (StorageHandler.getBitmapsInCache(mContext) == 0) {
            ImageHandler.fetchAndSetImage();
            cacheImages(Prefs.getInt(Const.IMAGES_CACHE_SIZE, 1));
        } else {
            if (StorageHandler.getInternalPathForBitmap(mContext,
                    Prefs.getString(Const.CURRENT_IMAGE_AS_WALLPAPER, null)) != null) {


            } else {

            }
        }
    }

    /**
     * Cache images internally
     *
     * @param noOfImages - No of images to cache
     */
    private static void cacheImages(int noOfImages) {

    }
}
