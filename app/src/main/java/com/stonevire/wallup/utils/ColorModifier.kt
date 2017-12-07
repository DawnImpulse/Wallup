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

package com.stonevire.wallup.utils

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette

import com.stonevire.wallup.R

/**
 * Created by Saksham on 7/16/2017.
 */

object ColorModifier {

    /**
     * For a particular color choose whether black or white color should go in Contrast
     *
     * @param mColor  - The input color
     * @param context - Context
     * @return - Black or White color (int)
     */
    fun getBlackOrWhite(mColor: String, context: Context): Int {
        //parsing color string to color integer
        val parsedColor = Color.parseColor(mColor)
        //getting red color intensity
        val red = Color.red(parsedColor)
        //getting blue color intensity
        val blue = Color.blue(parsedColor)
        //getting green color intensity
        val green = Color.green(parsedColor)

        // Check whether the color lies in scale favour to contrast WHITE or BLACK
        return if (red * 0.299 + green * 0.587 + blue * 0.114 > 186) {
            ContextCompat.getColor(context, R.color.black)
        } else {
            ContextCompat.getColor(context, R.color.white)
        }
    }

    fun getBlackOrWhiteInt(parsedColor: Int, context: Context): Int {

        //getting red color intensity
        val red = Color.red(parsedColor)
        //getting blue color intensity
        val blue = Color.blue(parsedColor)
        //getting green color intensity
        val green = Color.green(parsedColor)

        // Check whether the color lies in scale favour to contrast WHITE or BLACK
        return if (red * 0.299 + green * 0.587 + blue * 0.114 > 186) {
            ContextCompat.getColor(context, R.color.black)
        } else {
            ContextCompat.getColor(context, R.color.white)
        }
    }

    /**
     * Get a non Dark from the palette
     *
     * @param mPalette - The input palette
     * @param mContext - Context
     * @return - The required non Dark color
     */
    fun getNonDarkColor(mPalette: Palette, mContext: Context): Int {
        //the color variable we need to return
        var color: Int
        //variable to store whether color is darker or not
        var colorNonDark: Boolean

        //get the contrast color of Vibrant Color
        color = mPalette.getVibrantColor(ContextCompat.getColor(mContext, R.color.black))
        colorNonDark = isColorNonDark(color)
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color

        //get the contrast color of Dominant Color
        color = mPalette.getDominantColor(ContextCompat.getColor(mContext, R.color.black))
        colorNonDark = isColorNonDark(color)
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color

        //get the contrast color of Light Vibrant Color
        color = mPalette.getLightVibrantColor(ContextCompat.getColor(mContext, R.color.black))
        colorNonDark = isColorNonDark(color)
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color

        //get the contrast color of Light Vibrant Color
        color = mPalette.getMutedColor(ContextCompat.getColor(mContext, R.color.black))
        colorNonDark = isColorNonDark(color)
        //If contrast color is not white i.e black then return it
        if (colorNonDark)
            return color

        //get the contrast color of Light Vibrant Color
        color = mPalette.getLightMutedColor(ContextCompat.getColor(mContext, R.color.black))
        colorNonDark = isColorNonDark(color)
        //If contrast color is not white i.e black then return it
        return if (colorNonDark) color else ContextCompat.getColor(mContext, R.color.colorAccent)

    }

    /**
     * Check whether color is Not Dark
     *
     * @param color - Input Color
     * @return - true / false
     */
    private fun isColorNonDark(color: Int): Boolean {
        //getting red color intensity
        val red = Color.red(color)
        //getting blue color intensity
        val blue = Color.blue(color)
        //getting green color intensity
        val green = Color.green(color)

        // Check whether the color lies in scale favour to contrast WHITE or BLACK
        return if (red * 0.299 + green * 0.587 + blue * 0.114 > 80 && red * 0.299 + green * 0.587 + blue * 0.114 < 220) {
            true
        } else {
            false
        }
    }
}
