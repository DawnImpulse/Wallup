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
import com.pixplicity.easyprefs.library.Prefs
import com.stonevire.wallup.interfaces.OnCallbackListener
import com.stonevire.wallup.network.UnsplashFetcher
import com.stonevire.wallup.utils.Const
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

object ImageHandler {

    /**
     * Fetches an image from unsplash and set it as wallpaper
     */
    fun fetchAndSetImage(mContext: Context) {
        val unsplashFetcher = UnsplashFetcher(mContext)

        unsplashFetcher.fetchImages(Prefs.getString(Const.WALLPAPER_URL, ""), 1, Const.CALLBACK_1)
        unsplashFetcher.onCallbackListener(object : OnCallbackListener {
            override fun onCallback(error: JSONObject?, response: Any?, callbackId: Int) {
                if (error != null) {
                    fetchAndSetImage(mContext)
                    //log error
                } else {
                    CacheHandler.cacheBitmapAndVerify(UrlHandler.UnsplashUrlModify("url"), "name", mContext)
                }
            }
        })
    }


    /**
     * Set the first image in cache as wallpaper
     *
     * @param mContext
     */
    fun setFirstImageInCache(mContext: Context) {
        val filesList: Array<File>
        val internalBitmapFiles = StorageHandler.getInternalBitmapsPath(mContext)

        if (internalBitmapFiles.listFiles().isNotEmpty()) {
            filesList = internalBitmapFiles.listFiles()
            //sorting the files list based on date modified
            Arrays.sort(filesList) { f1, f2 -> java.lang.Long.compare(f1.lastModified(), f2.lastModified()) }

            Prefs.putString(Const.CURRENT_IMAGE_AS_WALLPAPER, filesList[0].name)
            setImage(mContext)
        }
    }

    /**
     * * Set the next image in cache
     * + uses current image as wallpaper to find next
     *
     * @param mContext
     */
    fun setImage(mContext: Context) {
        val cachedBitmap = StorageHandler.getInternalBitmap(mContext,
                Prefs.getString(Const.CURRENT_IMAGE_AS_WALLPAPER, null))

        //if cached bitmap is null then delete it and set new wallpaper accordingly
        if (cachedBitmap == null) {
            StorageHandler.deleteFileInternally(mContext, Prefs.getString(Const.CURRENT_IMAGE_AS_WALLPAPER, null))
            if (StorageHandler.getBitmapsInCache(mContext) != 0)
                setFirstImageInCache(mContext)
            else
                fetchAndSetImage(mContext)
        } else
            try {
                WallpaperHandler.setHomescreenWallpaper(cachedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

    }

}
