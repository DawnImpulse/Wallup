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

package com.stonevire.wallup.storage

import android.graphics.Bitmap
import android.os.Environment

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Saksham on 8/29/2017.
 */

object BitmapStorage {

    fun saveToInternalStorage(mBitmap: Bitmap, fileName: String): Boolean {
        var fos: FileOutputStream? = null
        try {
            //Get a FOS object with internal path
            fos = FileOutputStream(directoryInternal(fileName))
            //Compress the bitmap with FOS or saving the Bitmap
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false

        } finally {

            try {
                fos!!.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } // end of finally block

    }

    /**
     * Create or open the Wallup Internal Directory & return the file object
     *
     * @return - Directory file object
     */
    private fun directoryInternal(fileName: String): File {
        val Path = Environment.getExternalStorageDirectory().path.toString() + "/Wallup"
        val directory = File(Path)
        return File(directory, fileName)
    }

}
