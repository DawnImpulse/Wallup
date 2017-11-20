package com.stonevire.wallup.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.stonevire.wallup.interfaces.OnCallbackListener;
import com.stonevire.wallup.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 2017 11 14
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 14 - v4A - Initial
 */

public class ImageFetcher {

    OnCallbackListener mCallback;
    Context mContext;

    public ImageFetcher(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * On callback listener
     *
     * @param mCallback
     */
    public void onCallbackListener(OnCallbackListener mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * Fetch an image via Glide into bitmap
     *
     * @param url
     * @param callbackId
     */
    public void getImageFromUrl(String url, final int callbackId) {
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource != null)
                            mCallback.onCallback(null, resource, callbackId);
                        else {
                            JSONObject errorObject = new JSONObject();
                            try {
                                errorObject.put(Const.MESSAGE, Const.NULL_BITMAP);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mCallback.onCallback(errorObject, null, callbackId);
                        }
                    }
                });
    }
}
