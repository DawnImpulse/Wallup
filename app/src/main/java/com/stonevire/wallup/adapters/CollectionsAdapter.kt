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

package com.stonevire.wallup.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView
import com.stonevire.wallup.R
import com.stonevire.wallup.interfaces.OnLoadMoreListener
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Saksham on 9/13/2017.
 */

class CollectionsAdapter
/**
 * Public Constructor
 *
 * @param context
 * @param array
 * @param recyclerView
 */
(internal var mContext: Context, internal var collectionsArray: JSONArray, internal var mRecyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var mLoadMoreListener: OnLoadMoreListener? = null

    internal var isLoading: Boolean = false
    internal var VIEW_TYPE_LOADING = 0
    internal var VIEW_TYPE_ITEM = 1
    internal var VIEW_TYPE_AD = 3
    private val visibleThreshold = 5
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0

    internal var collectionObject: JSONObject
    internal var coverPhotoObject: JSONObject
    internal var userObject: JSONObject

    init {

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val mLinearLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager

                totalItemCount = mLinearLayoutManager.itemCount
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val v: View

        if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_collections, parent, false)
            return CollectionsHolder(v)

        } else if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false)
            return LoadingViewHolder(v)
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CollectionsHolder) {

            try {
                collectionObject = collectionsArray.getJSONObject(position)
                coverPhotoObject = collectionObject.getJSONObject(Const.COVER_PHOTO)
                userObject = collectionObject.getJSONObject(Const.USER)

                val coverPhotoUrls = coverPhotoObject.getJSONObject(Const.IMAGE_URLS)
                val authorUrls = userObject.getJSONObject(Const.PROFILE_IMAGES)

                holder.coverImage.setBackgroundColor(Color.parseColor(coverPhotoObject.getString(Const.IMAGE_COLOR)))
                holder.coverImage.setImageURI(coverPhotoUrls.getString(Const.IMAGE_REGULAR))
                holder.authorImage.setImageURI(authorUrls.getString(Const.USER_IMAGE_LARGE))
                holder.authorName.text = userObject.getString(Const.IMAGE_USER_NAME)
                holder.collectionName.text = collectionObject.getString(Const.COLLECTION_TITLE)
                holder.totalPhotos.text = collectionObject.getString(Const.TOTAL_PHOTOS) + " photos"

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return collectionsArray.length()
    }

    override fun getItemViewType(position: Int): Int {
        return if (collectionsArray.isNull(position)) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    /**
     * On Load More Listener (defined)
     *
     * @param mLoadMoreListener
     */
    fun setOnLoadMoreListener(mLoadMoreListener: OnLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener
    }

    /**
     * Set Loaded to false when view is loaded after On Load More
     */
    fun setLoaded() {
        isLoading = false
    }

    /**
     * Loading View Holder
     */
    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById<View>(R.id.inflator_loading_view_progress) as ProgressBar
        }
    }

    /**
     * Feed Holder
     */
    private inner class CollectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var coverImage: SimpleDraweeView
        internal var authorName: TextView
        internal var authorLayout: LinearLayout
        internal var authorImage: SimpleDraweeView
        internal var totalPhotos: TextView
        internal var collectionName: TextView

        init {

            coverImage = itemView.findViewById<View>(R.id.inflator_collection_cover_image) as SimpleDraweeView
            authorName = itemView.findViewById<View>(R.id.inflator_collection_author_name) as TextView
            authorLayout = itemView.findViewById<View>(R.id.inflator_collection_author_layout) as LinearLayout
            authorImage = itemView.findViewById<View>(R.id.inflator_collection_author_image) as SimpleDraweeView
            totalPhotos = itemView.findViewById<View>(R.id.inflator_collection_photos) as TextView
            collectionName = itemView.findViewById<View>(R.id.inflator_collection_collections_name) as TextView
        }
    }
}
