/*
 * Copyright 2017 Saksham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stonevire.wallup.network.volley

import android.content.Context

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Saksham on 6/21/2017.
 */

class VolleyWrapper(private val context: Context) {
    private var listener: RequestResponse? = null
    internal var requestQueue: RequestQueue

    init {
        requestQueue = Volley.newRequestQueue(context)
    }

    fun setListener(listener: RequestResponse) {
        this.listener = listener
    }

    fun postCall(URL: String, params: Map<String, String>, callbackID: Int) {
        val customPostRequest = CustomPostRequest(Request.Method.POST,
                URL, params, Response.Listener { response ->
            //successful response
            listener!!.onResponse(response, callbackID) //onCallback
        }, Response.ErrorListener { error ->
            //error
            listener!!.onErrorResponse(error, callbackID) //onCallback
        })

        requestQueue.add(customPostRequest)
    }

    fun getCall(URL: String, callbackID: Int) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                URL, null, Response.Listener { response ->
            //successful response
            listener!!.onResponse(response, callbackID) //onCallback
        }, Response.ErrorListener { error ->
            //error
            listener!!.onErrorResponse(error, callbackID) //onCallback
        })

        requestQueue.add(jsonObjectRequest)
    }

    fun getCallArray(URL: String, callbackID: Int) {
        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET,
                URL, null, Response.Listener { response ->
            //successful response
            listener!!.onResponse(response, callbackID) //onCallback
        }, Response.ErrorListener { error ->
            //error
            listener!!.onErrorResponse(error, callbackID) //onCallback
        })

        requestQueue.add(jsonObjectRequest)
    }
}
