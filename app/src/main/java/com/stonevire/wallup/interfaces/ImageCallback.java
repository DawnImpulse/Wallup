package com.stonevire.wallup.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Saksham on 2017 11 13
 * Last Branch Update - v
 * Updates :
 * Saksham - 2017 11 13 - v - Initial
 */

public interface ImageCallback {
    void onCallback(JSONObject error, JSONArray response, int callbackId);
}
