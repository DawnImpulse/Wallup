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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.android.volley.VolleyError
import com.stonevire.wallup.R
import com.stonevire.wallup.adapters.CollectionsAdapter
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
/**
 * Empty Default Constructor
 */
class FeaturedCollectionsFragment : Fragment(), RequestResponse, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_featured_collections_recycler)
    internal var mRecyclerView: RecyclerView? = null
    @BindView(R.id.fragment_featured_collections_swipe)
    internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    internal var volleyWrapper: VolleyWrapper
    internal var mCollectionsAdapter: CollectionsAdapter
    internal var collectionArray: JSONArray
    internal var unbinder: Unbinder

    internal var page: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_featured_collections, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        volleyWrapper = VolleyWrapper(activity)
        volleyWrapper.setListener(this)
        volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS, Const.COLLECTIONS_FEATURED_CALLBACK)
    }

    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Toast.makeText(activity, volleyError.toString(), Toast.LENGTH_SHORT).show()

        mSwipeRefreshLayout!!.isRefreshing = false

        if (callback == Const.COLLECTIONS_FEATURED_LOADING_CALLBACK)
            volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=" + page, Const.COLLECTIONS_FEATURED_LOADING_CALLBACK)
    }

    override fun onResponse(response: JSONObject, callback: Int) {

    }

    override fun onResponse(response: JSONArray, callback: Int) {
        if (callback == Const.COLLECTIONS_FEATURED_CALLBACK) {

            collectionArray = response
            mCollectionsAdapter = CollectionsAdapter(activity, collectionArray, mRecyclerView)
            mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
            mRecyclerView!!.adapter = mCollectionsAdapter
            mRecyclerView!!.isNestedScrollingEnabled = true
            page = 2

            mCollectionsAdapter.setOnLoadMoreListener {
                collectionArray.put(null)
                mCollectionsAdapter.notifyItemInserted(collectionArray.length())

                volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=" + page, Const.COLLECTIONS_FEATURED_LOADING_CALLBACK)
            }

            mSwipeRefreshLayout!!.isRefreshing = false

        } else if (callback == Const.COLLECTIONS_FEATURED_LOADING_CALLBACK) {
            page++
            collectionArray.remove(collectionArray.length() - 1)
            mCollectionsAdapter.notifyItemRemoved(collectionArray.length() - 1)

            for (i in 0 until response.length()) {
                try {
                    collectionArray.put(response.getJSONObject(i))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            mCollectionsAdapter.notifyDataSetChanged()
            mCollectionsAdapter.setLoaded()
        }

    }

    override fun onResponse(response: String, callback: Int) {

    }

    override fun onRefresh() {
        page = 1
        volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=1", Const.COLLECTIONS_FEATURED_CALLBACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }


}// Required empty public constructor
