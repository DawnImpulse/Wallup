package com.stonevire.wallup.interfaces;

import org.json.JSONObject;

/**
 * Created by Saksham on 2017 11 13
 * Last Branch Update - v
 * Updates :
 * Saksham - 2017 11 13 - v - Initial
 */

public interface OnCallbackListener {
    <E> void onCallback(JSONObject error, E response, int callbackId);
}
