package com.stonevire.wallup.callbacks;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Saksham on 2017 11 09
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 09 - v4A - Initial
 */

public class ImagesObjectCallback {
    private JSONObject error;
    private JSONArray jsonArray;
    private int callbackId;

    public ImagesObjectCallback(JSONObject error, JSONArray jsonArray, int callbackId) {
        this.error = error;
        this.jsonArray = jsonArray;
        this.callbackId = callbackId;
    }

    public JSONObject getError() {
        return error;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public int getCallbackId() {
        return callbackId;
    }
}
