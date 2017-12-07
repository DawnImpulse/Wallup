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

import android.os.Build
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


/**
 * A simple [Fragment] subclass.
 */
class LatestFragment : Fragment(), RequestResponse, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_latest_recycler)
    internal var fragmentLatestRecycler: RecyclerView? = null
    @BindView(R.id.fragment_latest_swipe)
    internal var fragmentLatestSwipe: SwipeRefreshLayout? = null
    @BindView(R.id.fragment_latest_loading)
    internal var fragmentLatestLoading: LoadingDots? = null
    internal var unbinder: Unbinder

    internal var mVolleyWrapper: VolleyWrapper
    internal var imagesArray: JSONArray
    internal var mFeedAdapter: MainAdapter

    internal var page: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_latest, container, false)
        unbinder = ButterKnife.bind(this, view)
        mVolleyWrapper = VolleyWrapper(activity)
        imagesArray = JSONArray()
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (imagesArray.length() == 0) {
            page = 1
            mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=1", Const.LATEST_CALLBACK)
            mVolleyWrapper.setListener(this)
        }

        fragmentLatestSwipe!!.setOnRefreshListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {

        fragmentLatestSwipe!!.isRefreshing = false
        Log.d("Test", volleyError.toString())
        Toast.makeText(activity, volleyError.toString(), Toast.LENGTH_SHORT).show()

        if (callback == Const.LATEST_LOAD_MORE)
            mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=" + page, Const.LATEST_LOAD_MORE)
    }

    override fun onResponse(response: JSONObject, callback: Int) {}

    override fun onResponse(response: JSONArray, callback: Int) {
        fragmentLatestLoading!!.visibility = View.GONE
        if (callback == Const.LATEST_CALLBACK) {
            page++
            imagesArray = response
            mFeedAdapter = MainAdapter(activity, imagesArray, fragmentLatestRecycler)
            fragmentLatestRecycler!!.layoutManager = LinearLayoutManager(activity)
            fragmentLatestRecycler!!.adapter = mFeedAdapter
            fragmentLatestRecycler!!.isNestedScrollingEnabled = true

            mFeedAdapter.setOnLoadMoreListener {
                imagesArray.put(null)
                mFeedAdapter.notifyItemInserted(imagesArray.length())

                mVolleyWrapper.getCallArray(Const.UNSPLASH_TRENDING_IMAGES + "&page=" + page, Const.LATEST_LOAD_MORE)
            }

            fragmentLatestSwipe!!.isRefreshing = false
        } else if (callback == Const.LATEST_LOAD_MORE) {
            try {
                page++
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    imagesArray.remove(imagesArray.length() - 1)
                    mFeedAdapter.notifyItemRemoved(imagesArray.length())
                }
                for (i in 0 until response.length()) {
                    imagesArray.put(response.getJSONObject(i))
                }
                mFeedAdapter.notifyDataSetChanged()
                mFeedAdapter.setLoaded()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    override fun onResponse(response: String, callback: Int) {

    }

    override fun onRefresh() {
        page = 1
        mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=1", Const.LATEST_CALLBACK)
    }
}// Required empty public constructor
