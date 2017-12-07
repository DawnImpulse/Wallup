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

package com.stonevire.wallup.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.android.volley.VolleyError
import com.eyalbira.loadingdots.LoadingDots
import com.stonevire.wallup.R
import com.stonevire.wallup.adapters.MainAdapter
import com.stonevire.wallup.interfaces.OnLoadMoreListener
import com.stonevire.wallup.network.volley.RequestResponse
import com.stonevire.wallup.network.volley.VolleyWrapper
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder

class CuratedFragment : Fragment(), RequestResponse, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_curated_recycler)
    internal var fragmentCuratedRecycler: RecyclerView? = null
    @BindView(R.id.fragment_curated_swipe)
    internal var fragmentCuratedSwipe: SwipeRefreshLayout? = null
    @BindView(R.id.fragment_curated_loading)
    internal var fragmentCuratedLoading: LoadingDots? = null
    internal var unbinder: Unbinder

    internal var imagesArray: JSONArray
    internal var mVolleyWrapper: VolleyWrapper
    internal var mCuratedAdapter: MainAdapter

    internal var page: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        imagesArray = JSONArray()
        val view = inflater!!.inflate(R.layout.fragment_curated, container, false)
        unbinder = ButterKnife.bind(this, view)
        mVolleyWrapper = VolleyWrapper(activity)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (imagesArray.length() == 0) {
            page = 1
            mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=1", Const.CURATED_CALLBACK)
            mVolleyWrapper.setListener(this)
        }

        fragmentCuratedSwipe!!.setOnRefreshListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    override fun onRefresh() {
        page = 1
        mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=1", Const.CURATED_CALLBACK)
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        fragmentCuratedSwipe!!.isRefreshing = false

        Log.d("Test", volleyError.toString())
        Toast.makeText(activity, volleyError.toString(), Toast.LENGTH_SHORT).show()

        if (callback == Const.CURATED_LOAD_MORE)
            mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=" + page, Const.CURATED_LOAD_MORE)
    }

    override fun onResponse(response: JSONObject, callback: Int) {

    }

    override fun onResponse(response: JSONArray, callback: Int) {
        fragmentCuratedLoading!!.visibility = View.GONE

        if (callback == Const.CURATED_CALLBACK) {
            page++
            imagesArray = response
            mCuratedAdapter = MainAdapter(activity, imagesArray, fragmentCuratedRecycler)
            fragmentCuratedRecycler!!.layoutManager = LinearLayoutManager(activity)
            fragmentCuratedRecycler!!.adapter = mCuratedAdapter
            fragmentCuratedRecycler!!.isNestedScrollingEnabled = true

            mCuratedAdapter.setOnLoadMoreListener {
                imagesArray.put(null)
                mCuratedAdapter.notifyItemInserted(imagesArray.length())

                mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=" + page, Const.CURATED_LOAD_MORE)
            }

            fragmentCuratedSwipe!!.isRefreshing = false

        } else if (callback == Const.CURATED_LOAD_MORE) {
            page++
            imagesArray.remove(imagesArray.length() - 1)
            mCuratedAdapter.notifyItemRemoved(imagesArray.length() - 1)

            for (i in 0 until response.length()) {
                try {
                    imagesArray.put(response.getJSONObject(i))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            mCuratedAdapter.notifyDataSetChanged()
            mCuratedAdapter.setLoaded()
        }
    }

    override fun onResponse(response: String, callback: Int) {

    }
}
