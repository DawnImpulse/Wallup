package com.stonevire.wallup.network.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Saksham on 6/21/2017.
 */

public class VolleyWrapper {

    private Context context;
    private RequestResponse listener;
    RequestQueue requestQueue;

    public VolleyWrapper(Context context)
    {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void setListener(RequestResponse listener)
    {
        this.listener = listener;
    }

    public void postCall(String URL, Map<String,String> params, final int callbackID)
    {
        CustomPostRequest customPostRequest = new CustomPostRequest(Request.Method.POST,
                URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //successful response
                listener.onResponse(response,callbackID); //callback
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { //error
                listener.onErrorResponse(error,callbackID); //callback
            }
        });

        requestQueue.add(customPostRequest);
    }

    public void getCall(String URL, final int callbackID)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //successful response
                listener.onResponse(response,callbackID); //callback
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { //error
                listener.onErrorResponse(error,callbackID); //callback
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
