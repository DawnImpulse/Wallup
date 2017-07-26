package com.stonevire.wallup.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.stonevire.wallup.R;

/**
 * Created by Saksham on 7/16/2017.
 */

public class ColorModifier {

    /**
     * For a particular color choose whether black or white color should go in Contrast
     *
     * @param mColor  - The input color
     * @param context - Context
     * @return - Black or White color (int)
     */
    public static int getBlackOrWhite(String mColor, Context context) {
        //parsing color string to color integer
        int parsedColor = Color.parseColor(mColor);
        //getting red color intensity
        int red = Color.red(parsedColor);
        //getting blue color intensity
        int blue = Color.blue(parsedColor);
        //getting green color intensity
        int green = Color.green(parsedColor);

        // Check whether the color lies in scale favour to contrast WHITE or BLACK
        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 186) {
            return ContextCompat.getColor(context, R.color.black);
        } else {
            return ContextCompat.getColor(context, R.color.white);
        }
    }

    /**
     * Get a non Dark from the palette
     *
     * @param mPalette - The input palette
     * @param mContext - Context
     * @return - The required non Dark color
     */
    public static int getNonDarkColor(Palette mPalette, Context mContext) {
        //the color variable we need to return
        int color;
        //variable to store whether color is darker or not
        boolean colorNonDark;

        //get the contrast color of Vibrant Color
        color = mPalette.getVibrantColor(ContextCompat.getColor(mContext, R.color.black));
        colorNonDark = isColorNonDark(color);
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color;

        //get the contrast color of Dominant Color
        color = mPalette.getDominantColor(ContextCompat.getColor(mContext, R.color.black));
        colorNonDark = isColorNonDark(color);
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color;

        //get the contrast color of Light Vibrant Color
        color = mPalette.getLightVibrantColor(ContextCompat.getColor(mContext, R.color.black));
        colorNonDark = isColorNonDark(color);
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color;

        //get the contrast color of Light Vibrant Color
        color = mPalette.getMutedColor(ContextCompat.getColor(mContext, R.color.black));
        colorNonDark = isColorNonDark(color);
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color;

        //get the contrast color of Light Vibrant Color
        color = mPalette.getLightMutedColor(ContextCompat.getColor(mContext, R.color.black));
        colorNonDark = isColorNonDark(color);
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color;

        return ContextCompat.getColor(mContext, R.color.colorAccent);
    }

    /**
     * Check whether color is Not Dark
     *
     * @param color - Input Color
     * @return - true / false
     */
    private static boolean isColorNonDark(int color) {
        //getting red color intensity
        int red = Color.red(color);
        //getting blue color intensity
        int blue = Color.blue(color);
        //getting green color intensity
        int green = Color.green(color);

        // Check whether the color lies in scale favour to contrast WHITE or BLACK
        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 80 && (red * 0.299 + green * 0.587 + blue * 0.114) < 220) {
            return true;
        } else {
            return false;
        }
    }
}
