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

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException

/**
 * Created by Saksham on 6/21/2017.
 */

class CustomPostRequest : Request<JSONObject> {
    private var listener: Response.Listener<JSONObject>? = null
    private var params: Map<String, String>? = null

    constructor(url: String, params: Map<String, String>,
                reponseListener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : super(Request.Method.GET, url, errorListener) {
        this.listener = reponseListener
        this.params = params
    }

    constructor(method: Int, url: String, params: Map<String, String>,
                reponseListener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : super(method, url, errorListener) {
        this.listener = reponseListener
        this.params = params
    }

    @Throws(com.android.volley.AuthFailureError::class)
    override fun getParams(): Map<String, String>? {
        return params
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        try {
            val jsonString = String(response.data,
                    HttpHeaderParser.parseCharset(response.headers))
            return Response.success(JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (je: JSONException) {
            return Response.error(ParseError(je))
        }

    }

    override fun deliverResponse(response: JSONObject) {
        // TODO Auto-generated method stub
        listener!!.onResponse(response)
    }
}
