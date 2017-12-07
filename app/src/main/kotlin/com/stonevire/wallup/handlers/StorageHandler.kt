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
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

object StorageHandler {

    /**
     * Returns number of bitmaps in cache
     *
     * @return
     */
    fun getBitmapsInCache(mContext: Context): Int {
        return getInternalBitmapsPath(mContext).listFiles().size
    }

    /**
     * Return the internal path for Bitmap
     *
     * @param mContext
     * @return
     */
    fun getInternalBitmapsPath(mContext: Context): File {
        val cw = ContextWrapper(mContext)
        return cw.getDir("bitmaps", Context.MODE_PRIVATE)
    }

    /**
     * Returns the internal path for a given bitmap
     *
     * @param mContext
     * @param fileName
     * @return
     */
    fun getInternalPathForBitmap(mContext: Context, fileName: String): File {
        return File(getInternalBitmapsPath(mContext), fileName)
    }

    /**
     * Returns the bitmap from internal storage
     *
     * @param mContext
     * @param fileName
     * @return
     */
    fun getInternalBitmap(mContext: Context, fileName: String): Bitmap? {
        var bitmap: Bitmap? = null
        var fos: FileInputStream? = null
        try {
            fos = FileInputStream(getInternalPathForBitmap(mContext, fileName))
            bitmap = BitmapFactory.decodeStream(fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return bitmap
    }

    /**
     * Delete file internally
     *
     * @param mContext
     * @param fileName
     * @return
     */
    fun deleteFileInternally(mContext: Context, fileName: String): Boolean {
        return getInternalPathForBitmap(mContext, fileName).delete()
    }


    /**
     * Storing/caching bitmap internally
     *
     * @param bitmap
     * @param name
     */
    fun storeBitmapInternally(context: Context, bitmap: Bitmap, name: String): Boolean {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(getInternalPathForBitmap(context, name))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false

        } finally {
            try {
                assert(fos != null)
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Verify bitmap exist or not , if yes then is it !=null
     *
     * @param mContext
     * @param mName
     * @return
     */
    fun verifyBitmapInternally(mContext: Context, mName: String): Boolean {
        val bitmapsPath = getInternalBitmapsPath(mContext)
        val filesList = bitmapsPath.listFiles()

        for (bitmapFile in filesList) {
            if (bitmapFile.name == mName) {
                return bitmapFile.length() > 0
            }
        }
        return false
    }
}
