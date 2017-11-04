package com.stonevire.wallup.services;

/**
 * Created by Saksham on 2017 11 04
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 04 - v4A - Initial
 */

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Use to set wallpaper on homescreen
 * +++ calculating hcf of image width & height
 * +++ handling of cropping images
 */
public class WallpaperHandler {

    private static Context mContext;

    /**
     * Get hcf of width & height of an image
     *
     * @param width  - Width of device
     * @param height - Height of device
     * @return
     */
    private static int calculateHcf(int width, int height) {
        while (height != 0) {
            int t = height;
            height = width % height;
            width = t;
        }
        return width;
    }

    /**
     * Handling of bitmap cropping based on device screen
     * Could be used in future for external cropping too
     *
     * @param originalBitmap
     * @return
     */
    public static Bitmap bitmapCropper(Bitmap originalBitmap) {

        int scaleHcf,
                scaleX,
                scaleY,
                originalWidth,
                originalHeight,
                width = 0,
                height = 0;

        Point point;
        WindowManager mWindowManager;
        Display display;
        Bitmap modifiedBitmap = null,
                scaledBitmap = null;

        point = new Point();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        display = mWindowManager.getDefaultDisplay();
        display.getSize(point); //The point now has display dimens

        originalWidth = originalBitmap.getWidth();
        originalHeight = originalBitmap.getHeight();
        scaleHcf = calculateHcf(point.x, point.y);

        // If bitmap is null or some other problem
        if (originalWidth == 0) {
            return null;
        }

        /* Get X & Y scaling increment factor
        *  If ratio i.e. hcf is less than 20 then use it else divide it by 8
        */
        scaleX = ((point.x / scaleHcf) > 20) ? (point.x / scaleHcf) / 8 : (point.x / scaleHcf);
        scaleY = ((point.y / scaleHcf) > 20) ? (point.y / scaleHcf) / 8 : (point.y / scaleHcf);

        //Loop while incrementing width and height by scaling factors
        while (width < originalWidth && height < originalHeight) {
            width += scaleX;
            height += scaleY;
        }

        //Decrease one scaling factor so it wont exceed the max bitmap length
        width -= scaleX;
        height -= scaleY;

        //Get the starting point to crop the original Bitmap
        int startingPointX = (originalWidth - width) / 2;
        int startingPointY = (originalHeight - height) / 2;

        // if we get starting point less than 0 then make it 0
        startingPointX = (startingPointX < 0) ? 0 : startingPointX;
        startingPointY = (startingPointY < 0) ? 0 : startingPointY;

        //Create cropped version of original bitmap
        modifiedBitmap = Bitmap.createBitmap(originalBitmap, startingPointX, startingPointY, width, height);
        //Create final scaled bitmap based on exact screen size
        Bitmap finalBitmap = Bitmap.createScaledBitmap(modifiedBitmap, point.x, point.y, false);

        originalBitmap.recycle();
        modifiedBitmap.recycle();

        return finalBitmap;
    }

    /**
     * Use to set wallpaper on screen
     *
     * @param originalBitmap - original bitmap from storage
     */
    public void setHomescreenWallpaper(Bitmap originalBitmap) throws IOException {
        WallpaperManager wallpaperManager =
                WallpaperManager.getInstance(mContext);
        wallpaperManager.setBitmap(bitmapCropper(originalBitmap));
    }

    /**
     * Use to set lock screen wallpaper on Android 24+ (Oreo)
     *
     * @param originalBitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setLockScreenWallpaper(Bitmap originalBitmap) throws IOException {
        WallpaperManager.getInstance(mContext).setBitmap(
                originalBitmap, null, true, WallpaperManager.FLAG_LOCK);
    }
}
