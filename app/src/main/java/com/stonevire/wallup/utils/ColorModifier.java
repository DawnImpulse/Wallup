package com.stonevire.wallup.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.stonevire.wallup.R;

/**
 * Created by Saksham on 7/16/2017.
 */

public class ColorModifier {

    public static int getBlackOrWhite(int color, Context context)
    {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        if ((red*0.299 + green*0.587 + blue*0.114) > 186)
        {
            return ContextCompat.getColor(context,R.color.black);
        }else
        {
            return ContextCompat.getColor(context,R.color.white);
        }
    }
}
