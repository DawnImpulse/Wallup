package com.stonevire.wallup.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saksham on 7/12/2017.
 */

public class DateModifier {

    public static String toDateFullMonthYear(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat sdfNew = new SimpleDateFormat("dd MMMM, yyyy");
        try {
            Date old = sdf.parse(date);
            return sdfNew.format(old);
        } catch (ParseException e) {
            return date;
        }
    }
}
