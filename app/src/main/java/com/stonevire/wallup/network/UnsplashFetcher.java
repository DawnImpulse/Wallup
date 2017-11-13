package com.stonevire.wallup.network;

import android.content.Context;

import com.android.volley.VolleyError;
import com.stonevire.wallup.interfaces.ImageCallback;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 2017 11 09
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 09 - v4A - Initial
 */

public class UnsplashFetcher implements RequestResponse {

    Context mContext;
    ImageCallback mImageCallback;

    public UnsplashFetcher(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Image onCallback listener
     *
     * @param mImageCallback
     */
    public void onCallbackListener(ImageCallback mImageCallback) {
        this.mImageCallback = mImageCallback;
    }

    /**
     * Fetching images from unsplash API
     *
     * @param url
     * @param count
     * @param callbackId
     */
    public void fetchImages(String url, int count, int callbackId) {
        VolleyWrapper volleyWrapper = new VolleyWrapper(mContext);
        volleyWrapper.getCallArray(url + Const.PER_PAGE + count, callbackId);
        volleyWrapper.setListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        JSONObject errorObject = new JSONObject();
        try {
            errorObject.put(Const.VOLLEY_ERROR, true);
            errorObject.put(Const.MESSAGE, volleyError.getMessage());
            errorObject.put(Const.CAUSE, volleyError.getCause());
            errorObject.put(Const.STACK_TRACE, volleyError.getStackTrace());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mImageCallback.onCallback(errorObject, null, callback);
    }

    @Override
    public void onResponse(JSONObject response, int callback) {

    }

    @Override
    public void onResponse(JSONArray response, int callback) {
        mImageCallback.onCallback(null,response,callback);
    }

    @Override
    public void onResponse(String response, int callback) {

    }
}
