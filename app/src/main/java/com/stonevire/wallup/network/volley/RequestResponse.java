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
    public void onResponse(String response, int callback);
}
