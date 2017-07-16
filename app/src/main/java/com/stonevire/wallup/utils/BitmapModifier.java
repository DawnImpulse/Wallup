package com.stonevire.wallup.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

/**
 * Created by Saksham on 7/16/2017.
 */

public class BitmapModifier {

    public static Palette colorSwatch(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled()) {
            return Palette.from(bitmap).generate();
        }
        return null;
    }
}
