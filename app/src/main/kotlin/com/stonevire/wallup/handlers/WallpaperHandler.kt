package com.stonevire.wallup.handlers

/**
 * Created by Saksham on 2017 11 04
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 04 - v4A - Initial
 */

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.Display
import android.view.WindowManager

import java.io.IOException

/**
 * Use to set wallpaper on homescreen
 * +++ calculating hcf of image width & height
 * +++ handling of cropping images
 */
class WallpaperHandler {

    /**
     * Use to set lock screen wallpaper on Android 24+ (Oreo)
     *
     * @param originalBitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(IOException::class)
    fun setLockScreenWallpaper(originalBitmap: Bitmap) {
        WallpaperManager.getInstance(mContext).setBitmap(
                originalBitmap, null, true, WallpaperManager.FLAG_LOCK)
    }

    companion object {

        private val mContext: Context? = null

        /**
         * Get hcf of width & height of an image
         *
         * @param width  - Width of device
         * @param height - Height of device
         * @return
         */
        private fun calculateHcf(width: Int, height: Int): Int {
            var width = width
            var height = height
            while (height != 0) {
                val t = height
                height = width % height
                width = t
            }
            return width
        }

        /**
         * Handling of bitmap cropping based on device screen
         * Could be used in future for external cropping too
         *
         * @param originalBitmap
         * @return
         */
        fun bitmapCropper(originalBitmap: Bitmap): Bitmap? {

            val scaleHcf: Int
            val scaleX: Int
            val scaleY: Int
            val originalWidth: Int
            val originalHeight: Int
            var width = 0
            var height = 0

            val point: Point
            val mWindowManager: WindowManager
            val display: Display
            var modifiedBitmap: Bitmap? = null
            val scaledBitmap: Bitmap? = null

            point = Point()
            mWindowManager = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            display = mWindowManager.defaultDisplay
            display.getSize(point) //The point now has display dimens

            originalWidth = originalBitmap.width
            originalHeight = originalBitmap.height
            scaleHcf = calculateHcf(point.x, point.y)

            // If bitmap is null or some other problem
            if (originalWidth == 0) {
                return null
            }

            /* Get X & Y scaling increment factor
        *  If ratio i.e. hcf is less than 20 then use it else divide it by 8
        */
            scaleX = if (point.x / scaleHcf > 20) point.x / scaleHcf / 8 else point.x / scaleHcf
            scaleY = if (point.y / scaleHcf > 20) point.y / scaleHcf / 8 else point.y / scaleHcf

            //Loop while incrementing width and height by scaling factors
            while (width < originalWidth && height < originalHeight) {
                width += scaleX
                height += scaleY
            }

            //Decrease one scaling factor so it wont exceed the max bitmap length
            width -= scaleX
            height -= scaleY

            //Get the starting point to crop the original Bitmap
            var startingPointX = (originalWidth - width) / 2
            var startingPointY = (originalHeight - height) / 2

            // if we get starting point less than 0 then make it 0
            startingPointX = if (startingPointX < 0) 0 else startingPointX
            startingPointY = if (startingPointY < 0) 0 else startingPointY

            //Create cropped version of original bitmap
            modifiedBitmap = Bitmap.createBitmap(originalBitmap, startingPointX, startingPointY, width, height)
            //Create final scaled bitmap based on exact screen size
            val finalBitmap = Bitmap.createScaledBitmap(modifiedBitmap!!, point.x, point.y, false)

            originalBitmap.recycle()
            modifiedBitmap.recycle()

            return finalBitmap
        }

        /**
         * Use to set wallpaper on screen
         *
         * @param originalBitmap - original bitmap from storage
         */
        @Throws(IOException::class)
        fun setHomescreenWallpaper(originalBitmap: Bitmap) {
            val wallpaperManager = WallpaperManager.getInstance(mContext)
            wallpaperManager.setBitmap(bitmapCropper(originalBitmap))
        }
    }
}
