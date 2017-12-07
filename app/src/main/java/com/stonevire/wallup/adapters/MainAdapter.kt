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
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.google.android.gms.ads.NativeExpressAdView
import com.stonevire.wallup.R
import com.stonevire.wallup.activities.ImagePreviewActivity
import com.stonevire.wallup.activities.UserProfileActivity
import com.stonevire.wallup.interfaces.OnLoadMoreListener
import com.stonevire.wallup.utils.Const
import com.stonevire.wallup.utils.StringModifier

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Saksham on 2017 07 15
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - URL changes
 * DawnImpulse - 2017 10 06 - v4A - Changing layout for Main images
 * DawnImpulse - 2017 10 05 - v4A - Fixing repeating images in recycler
 * DawnImpulse - 2017 10 04 - v4A - Using Glide
 */

class MainAdapter
/**
 * Constructor
 *
 * @param context
 * @param array
 * @param recyclerView
 */
(internal var mContext: Context, internal var imagesArray: JSONArray, internal var mRecyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var mLoadMoreListener: OnLoadMoreListener? = null

    internal var isLoading: Boolean = false
    internal var VIEW_TYPE_LOADING = 0
    internal var VIEW_TYPE_ITEM = 1
    internal var VIEW_TYPE_AD = 3
    private val visibleThreshold = 5
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0

    internal var currentPosition: Int = 0

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

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val v: View
        if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_main, parent, false)
            return FeedHolder(v)
        } else if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false)
            return LoadingViewHolder(v)
        } else if (viewType == VIEW_TYPE_AD) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_native_ad, parent, false)
            return AdHolder(v)
        }
        return null
    }

    /**
     * Bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FeedHolder) {
            try {
                val `object` = imagesArray.getJSONObject(position)
                val user = `object`.getJSONObject(Const.IMAGE_USER)
                val urls = `object`.getJSONObject(Const.IMAGE_URLS)
                val profileImage = user.getJSONObject(Const.PROFILE_IMAGES)

                holder.image.setBackgroundColor(Color.parseColor(`object`.getString(Const.IMAGE_COLOR)))
                holder.firstName.text = StringModifier.camelCase(user.getString(Const.USER_FIRST_NAME))

                val lastName = user.getString(Const.USER_LAST_NAME)
                if (lastName.length == 0 || lastName == "null" || lastName == null) {
                    holder.lastName.text = " "
                } else
                    holder.lastName.text = " " + StringModifier.camelCase(lastName)

                Glide.with(mContext)
                        .load(urls.getString(Const.IMAGE_RAW) + "?h=720")
                        .into(holder.image)

                Glide.with(mContext)
                        .load(profileImage.getString(Const.USER_IMAGE_LARGE))
                        .into(holder.authorImage)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewCompat.setTransitionName(holder.authorImage, user.getString(Const.USERNAME))
                    ViewCompat.setTransitionName(holder.firstName, user.getString(Const.USER_FIRST_NAME))
                    ViewCompat.setTransitionName(holder.lastName, user.getString(Const.USER_LAST_NAME))
                    ViewCompat.setTransitionName(holder.image, user.getString(Const.USERNAME))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return imagesArray.length()
    }

    override fun getItemViewType(position: Int): Int {
        return if (imagesArray.isNull(position)) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
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
    private inner class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var authorImage: CircleImageView
        internal var image: ImageView
        internal var firstName: TextView
        internal var lastName: TextView
        internal var authorLayout: RelativeLayout

        init {
            authorImage = itemView.findViewById<View>(R.id.inflator_main_author_image) as CircleImageView
            image = itemView.findViewById<View>(R.id.inflator_main_drawee) as ImageView
            firstName = itemView.findViewById<View>(R.id.inflator_latest_main_first_name) as TextView
            lastName = itemView.findViewById<View>(R.id.inflator_main_author_last_name) as TextView
            authorLayout = itemView.findViewById<View>(R.id.inflator_main_author_layout) as RelativeLayout

            authorLayout.setOnClickListener(this)
            image.setOnClickListener(this)
        }

        override fun onClick(v: View) {

            var `object`: JSONObject? = null
            try {
                `object` = imagesArray.getJSONObject(adapterPosition)
                val user = `object`!!.getJSONObject(Const.IMAGE_USER)

                when (v.id) {
                    R.id.inflator_main_author_layout -> {
                        val intent = Intent(mContext, UserProfileActivity::class.java)
                        intent.putExtra(Const.IMAGE_USER, user.toString())

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE, ViewCompat.getTransitionName(authorImage))
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_1, ViewCompat.getTransitionName(firstName))
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_2, ViewCompat.getTransitionName(lastName))

                            val pairImage = Pair.create(authorImage as View, ViewCompat.getTransitionName(authorImage))
                            val pairFirstName = Pair.create(firstName as View, ViewCompat.getTransitionName(firstName))
                            val pairLastName = Pair.create(lastName as View, ViewCompat.getTransitionName(lastName))

                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, pairImage, pairFirstName, pairLastName)
                            mContext.startActivity(intent, options.toBundle())
                        } else {
                            mContext.startActivity(intent)
                        }
                    }

                    R.id.inflator_main_drawee -> {
                        val intent1 = Intent(mContext, ImagePreviewActivity::class.java)
                        intent1.putExtra(Const.IMAGE_OBJECT, `object`.toString())
                        intent1.putExtra(Const.IS_DIRECT_OBJECT, "true")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent1.putExtra(Const.TRANS_LATEST_TO_PREVIEW, ViewCompat.getTransitionName(image))
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, image, ViewCompat.getTransitionName(image))
                            mContext.startActivity(intent1, options.toBundle())
                        } else {
                            mContext.startActivity(intent1)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Ad View Holder
     */
    private inner class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mAdView: NativeExpressAdView

        init {
            mAdView = itemView.findViewById<View>(R.id.inflator_native_ad_view) as NativeExpressAdView
        }
    }
}
