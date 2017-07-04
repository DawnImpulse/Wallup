package com.stonevire.wallup.network.volley;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Saksham on 6/21/2017.
 */

public interface RequestResponse {
    public void onErrorResponse(VolleyError volleyError, int callback);
    public void onResponse(JSONObject response, int callback);
    public void onResponse(JSONArray response, int callback);
    public void onRespose(String response, int callback);
}
