package com.stonevire.wallup.network

import android.content.Context
import android.graphics.Bitmap

import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.stonevire.wallup.interfaces.OnCallbackListener
import com.stonevire.wallup.utils.Const

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Saksham on 2017 11 14
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 14 - v4A - Initial
 */

class ImageFetcher(internal var mContext: Context) {

    private lateinit var mCallback: OnCallbackListener

    /**
     * On callback listener
     *
     * @param mCallback
     */
    fun onCallbackListener(mCallback: OnCallbackListener) {
        this.mCallback = mCallback
    }

    /**
     * Fetch an image via Glide into bitmap
     *
     * @param url
     * @param callbackId
     */
    fun getImageFromUrl(url: String, callbackId: Int) {
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>) {
                        if (resource != null)
                            mCallback.onCallback(null, resource, callbackId)
                        else {
                            val errorObject = JSONObject()
                            try {
                                errorObject.put(Const.MESSAGE, Const.NULL_BITMAP)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                            mCallback.onCallback(errorObject, null, callbackId)
                        }
                    }
                })
    }
}
