package com.stonevire.wallup.handlers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Saksham on 2017 11 07
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 07 - v4A - Initial
 */

public class StorageHandler {

    /**
     * Returns number of bitmaps in cache
     *
     * @return
     */
    public static int getBitmapsInCache(Context mContext) {
        return getInternalBitmapsPath(mContext).listFiles().length;
    }

    /**
     * Return the internal path for Bitmap
     *
     * @param mContext
     * @return
     */
    public static File getInternalBitmapsPath(Context mContext) {
        ContextWrapper cw = new ContextWrapper(mContext);
        return cw.getDir("bitmaps", Context.MODE_PRIVATE);
    }

    /**
     * Returns the internal path for a given bitmap
     *
     * @param mContext
     * @param fileName
     * @return
     */
    public static File getInternalPathForBitmap(Context mContext, String fileName) {
        return new File(getInternalBitmapsPath(mContext), fileName);
    }

    /**
     * Returns the bitmap from internal storage
     *
     * @param mContext
     * @param fileName
     * @return
     */
    public static Bitmap getInternalBitmap(Context mContext, String fileName) {
        Bitmap bitmap = null;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(getInternalPathForBitmap(mContext, fileName));
            bitmap = BitmapFactory.decodeStream(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * Delete file internally
     *
     * @param mContext
     * @param fileName
     * @return
     */
    public static boolean deleteFileInternally(Context mContext, String fileName) {
        return getInternalPathForBitmap(mContext, fileName).delete();
    }


    /**
     * Storing/caching bitmap internally
     *
     * @param bitmap
     * @param name
     */
    public static boolean storeBitmapInternally(Context context, Bitmap bitmap, String name) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getInternalPathForBitmap(context, name));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
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
    public static boolean verifyBitmapInternally(Context mContext, String mName) {
        File bitmapsPath = getInternalBitmapsPath(mContext);
        File[] filesList = bitmapsPath.listFiles();

        for (File bitmapFile : filesList) {
            if (bitmapFile.getName().equals(mName)) {
                return bitmapFile.length() > 0;
            }
        }
        return false;
    }
}
