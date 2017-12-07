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
package com.stonevire.wallup.handlers

import android.content.Context
import android.graphics.Bitmap
import com.pixplicity.easyprefs.library.Prefs
import com.stonevire.wallup.interfaces.OnCallbackListener
import com.stonevire.wallup.network.ImageFetcher
import com.stonevire.wallup.utils.Const
import org.json.JSONObject

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

object CacheHandler {

    /**
     * Handles the management of internal bitmap cache
     * +++ intermediate for setting live image(wallpaper)
     *
     * @param mContext
     */
    fun setCacheHandler(mContext: Context) {

        if (StorageHandler.getBitmapsInCache(mContext) == 0) {
            ImageHandler.fetchAndSetImage(mContext)
            cacheImages(Prefs.getInt(Const.IMAGES_CACHE_SIZE, 1))
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
    private fun cacheImages(noOfImages: Int) {

    }

    /**
     * cache the image internally after fetching
     *
     * @param url
     * @return
     */
    fun cacheBitmapAndVerify(url: String, name: String, mContext: Context): Boolean {
        val mImageFetcher = ImageFetcher(mContext)
        mImageFetcher.getImageFromUrl(url, Const.CALLBACK_2)
        mImageFetcher.onCallbackListener(object : OnCallbackListener {
            override fun onCallback(error: JSONObject?, response: Any?, callbackId: Int) {
                if (error != null) {
                    //log error
                } else {
                    val mBitmap = response as Bitmap
                    mBitmap?.let {
                        if (StorageHandler.storeBitmapInternally(mContext, mBitmap, name)) {
                            if (StorageHandler.verifyBitmapInternally(mContext, name)) {
                                Prefs.putString(Const.CURRENT_IMAGE_AS_WALLPAPER, name)
                                ImageHandler.setFirstImageInCache(mContext)
                            } else {
                                //log error
                            }
                        }
                    }
                }
            }
        })

        return false
    }
}
