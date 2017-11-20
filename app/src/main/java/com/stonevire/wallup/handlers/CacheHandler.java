package com.stonevire.wallup.handlers;

import android.content.Context;
import android.graphics.Bitmap;

import com.pixplicity.easyprefs.library.Prefs;
import com.stonevire.wallup.interfaces.OnCallbackListener;
import com.stonevire.wallup.network.ImageFetcher;
import com.stonevire.wallup.utils.Const;

import org.json.JSONObject;

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
            ImageHandler.fetchAndSetImage(mContext);
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

    /**
     * cache the image internally after fetching
     *
     * @param url
     * @return
     */
    public static boolean cacheBitmapAndVerify(String url, final String name, final Context mContext) {
        ImageFetcher mImageFetcher = new ImageFetcher(mContext);
        mImageFetcher.getImageFromUrl(url, Const.CALLBACK_2);
        mImageFetcher.onCallbackListener(new OnCallbackListener() {
            @Override
            public <E> void onCallback(JSONObject error, E response, int callbackId) {
                if (error != null) {
                    ImageHandler.fetchAndSetImage(mContext);
                } else {
                    Bitmap mBitmap = (Bitmap) response;
                    if (StorageHandler.storeBitmapInternally(mContext, mBitmap, name)) {
                        if (StorageHandler.verifyBitmapInternally(mContext, name)) {
                            Prefs.putString(Const.CURRENT_IMAGE_AS_WALLPAPER, name);
                            ImageHandler.setFirstImageInCache(mContext);
                        } else
                            ImageHandler.fetchAndSetImage(mContext);
                    }
                }
            }
        });

        return false;
    }
}
