package com.stonevire.wallup.network.volley;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Saksham on 5/14/2017.
 */

public interface RequestResponse {
    public void onErrorResponse(VolleyError volleyError, String requesterId);
    public void onResponse(JSONObject response, String requesterId);
}
