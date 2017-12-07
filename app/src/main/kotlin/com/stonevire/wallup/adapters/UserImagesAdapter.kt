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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar

import com.bumptech.glide.Glide
import com.stonevire.wallup.R
import com.stonevire.wallup.activities.ImagePreviewActivity
import com.stonevire.wallup.interfaces.OnLoadMoreListener
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Saksham on 7/3/2017.
 */

/**
 * Created by DawnImpulse on 2017 07 03
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - URL changes + glide
 */

class UserImagesAdapter
/**
 * Constructor
 *
 * @param context,imagesArray,recyclerView
 */
(var mContext: Context, internal var mImagesArray: JSONArray?, internal var mRecyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var mLoadMoreListener: OnLoadMoreListener? = null

    private var isLoading: Boolean = false
    private val visibleThreshold = 5
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0

    init {

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val mLinearLayoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                /*mLinearLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (mImagesArray.isNull(position))
                            return 3;
                        return 1;
                    }
                });*/

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

    /**
     * On Create View Holder
     *
     * @param parent,viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var view: View? = null
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_user_images, parent, false)
            return UserImagesHolder(view)
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false)
            return LoadingViewHolder(view)
        }
        return null
    }

    /**
     * On Bind View Holder
     *
     * @param holder,position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserImagesHolder) {
            try {
                val imageObject = mImagesArray!!.getJSONObject(position)
                val urls = imageObject.getJSONObject(Const.IMAGE_URLS)
                val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                //-------------------- Getting width of screen
                /*DisplayMetrics displaymetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;

                ViewGroup.LayoutParams lp = ((UserImagesHolder) holder).draweeView.getLayoutParams();
                lp.width = width / 3;
                lp.height = width / 3;*/

                holder.draweeView.setBackgroundColor(
                        Color.parseColor(imageObject.getString(Const.IMAGE_COLOR)))
                Glide.with(mContext)
                        .load(urls.getString(Const.IMAGE_RAW) + "?h=720")
                        .into(holder.draweeView)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewCompat.setTransitionName(holder.draweeView, imageObject.getString(Const.IMAGE_ID))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    /**
     * Get count of Items
     *
     * @return Count of Items
     */
    override fun getItemCount(): Int {
        return if (mImagesArray == null) 0 else mImagesArray!!.length()
    }

    /**
     * Return the Type of view to show on Current View
     *
     * @param position
     * @return View Type (int)
     */
    override fun getItemViewType(position: Int): Int {

        return if (mImagesArray!!.isNull(position))
            VIEW_TYPE_LOADING
        else
            VIEW_TYPE_ITEM
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
     * Set Loaded false when view is loaded after On Loading
     */
    fun setLoaded() {
        isLoading = false
    }


    /**
     * User Images View Holder
     */
    private inner class UserImagesHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var draweeView: ImageView

        init {
            draweeView = itemView.findViewById<View>(R.id.inflator_user_images_drawee) as ImageView
            draweeView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            try {
                when (v.id) {
                    R.id.inflator_user_images_drawee -> {
                        val intent = Intent(mContext, ImagePreviewActivity::class.java)
                        intent.putExtra(Const.IMAGE_OBJECT, mImagesArray!!.getJSONObject(adapterPosition).toString())
                        intent.putExtra(Const.IS_DIRECT_OBJECT, "true")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_USER_TO_PREVIEW, ViewCompat.getTransitionName(draweeView))
                            val options1 = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, draweeView, ViewCompat.getTransitionName(draweeView))
                            mContext.startActivity(intent, options1.toBundle())
                        } else {
                            mContext.startActivity(intent)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
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
}
