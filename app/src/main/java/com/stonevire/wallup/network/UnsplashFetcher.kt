package com.stonevire.wallup.network

import android.content.Context

import com.android.volley.VolleyError
import com.stonevire.wallup.interfaces.OnCallbackListener
import com.stonevire.wallup.network.volley.RequestResponse
import com.stonevire.wallup.network.volley.VolleyWrapper
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Saksham on 2017 11 09
 * Last Branch Update - v4A
 * Updates :
 * Saksham - 2017 11 09 - v4A - Initial
 */

class UnsplashFetcher(private val mContext: Context) : RequestResponse {
    private var mImageCallback: OnCallbackListener? = null

    /**
     * Image onCallback listener
     *
     * @param mImageCallback
     */
    fun onCallbackListener(mImageCallback: OnCallbackListener) {
        this.mImageCallback = mImageCallback
    }

    /**
     * Fetching images from unsplash API
     *
     * @param url
     * @param count
     * @param callbackId
     */
    fun fetchImages(url: String, count: Int, callbackId: Int) {
        val volleyWrapper = VolleyWrapper(mContext)
        volleyWrapper.getCallArray(url + Const.PER_PAGE + count, callbackId)
        volleyWrapper.setListener(this)
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        val errorObject = JSONObject()
        try {
            errorObject.put(Const.VOLLEY_ERROR, true)
            errorObject.put(Const.MESSAGE, volleyError.message)
            errorObject.put(Const.CAUSE, volleyError.cause)
            errorObject.put(Const.STACK_TRACE, volleyError.stackTrace)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mImageCallback!!.onCallback<Any>(errorObject, null, callback)
    }

    override fun onResponse(response: JSONObject, callback: Int) {

    }

    override fun onResponse(response: JSONArray, callback: Int) {
        mImageCallback!!.onCallback(null, response, callback)
    }

    override fun onResponse(response: String, callback: Int) {

    }
}
