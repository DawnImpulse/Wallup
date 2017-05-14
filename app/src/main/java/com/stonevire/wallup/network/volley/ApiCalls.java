package com.stonevire.wallup.network.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Saksham on 5/14/2017.
 */

public class ApiCalls implements RequestResponse{

    private Context context;
    private String requesterId;
    private RequestResponse listenerObject;
    private RequestQueue apiCallsQueue;

    /**
     * Constructor
     * @param context
     */
    public ApiCalls(Context context)
    {
        this.context = context;
    }

    /**
     * Enabling Listener
     * @param listenerObject
     */
    public void setResponseListener(RequestResponse listenerObject) {
         this.listenerObject = listenerObject;
    }

    /**
     * JSON Post Method
     * @param url
     * @param params
     * @param requesterId
     */
    public void JSONPost(String url,Map<String,String> params,String requesterId)
    {
        this.requesterId = requesterId;
        CustomRequest customRequestObject = new CustomRequest(Request.Method.POST,
                url,params,this,this);
    }

    /**
     * Error Response Listener
     * @param volleyError
     * @param requesterId
     */
    @Override
    public void onErrorResponse(VolleyError volleyError, String requesterId) {

    }

    /**
     * On Response Listener
     * @param response
     * @param requesterId
     */
    @Override
    public void onResponse(JSONObject response, String requesterId) {

    }
}
