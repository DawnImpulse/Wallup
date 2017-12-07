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

package com.stonevire.wallup.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.drawee.view.SimpleDraweeView
import com.stonevire.wallup.R
import com.stonevire.wallup.adapters.UserImagesAdapter
import com.stonevire.wallup.interfaces.OnLoadMoreListener
import com.stonevire.wallup.network.volley.RequestResponse
import com.stonevire.wallup.network.volley.VolleyWrapper
import com.stonevire.wallup.utils.Const
import com.stonevire.wallup.utils.StringModifier

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import de.hdodenhof.circleimageview.CircleImageView

class UserProfileActivity : AppCompatActivity(), RequestResponse {
    internal var mVolleyWrapper: VolleyWrapper

    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.activity_user_profile_random_image)
    internal var activityUserProfileRandomImage: SimpleDraweeView? = null
    @BindView(R.id.activity_user_profile_first_name)
    internal var activityUserProfileFirstName: TextView? = null
    @BindView(R.id.activity_user_profile_last_name)
    internal var activityUserProfileLastName: TextView? = null
    @BindView(R.id.toolbar_layout)
    internal var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    @BindView(R.id.content_user_profile_recycler)
    internal var mRecyclerView: RecyclerView? = null
    @BindView(R.id.app_bar)
    internal var appBar: AppBarLayout? = null
    @BindView(R.id.content_user_profile_nested)
    internal var contentUserProfileNested: LinearLayout? = null
    @BindView(R.id.activity_user_profile_author_image)
    internal var activityUserProfileAuthorImage: CircleImageView? = null
    @BindView(R.id.activity_user_profile_unsplash_icon)
    internal var activityUserProfileUnsplashIcon: AppCompatImageView? = null
    @BindView(R.id.activity_user_profile_username)
    internal var activityUserProfileUsername: TextView? = null

    internal var intent: Intent

    internal var author: JSONObject? = null
    internal var author_image: JSONObject
    internal var imagesArray: JSONArray
    internal var links: JSONObject
    internal var mUserImagesAdapter: UserImagesAdapter

    internal var page = 1

    /**
     * On Create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        intent = getIntent()
        mVolleyWrapper = VolleyWrapper(this)
        imagesArray = JSONArray()

        try {
            author = JSONObject(getIntent().getStringExtra(Const.IMAGE_USER))
            author_image = author!!.getJSONObject(Const.PROFILE_IMAGES)
            links = author!!.getJSONObject(Const.LINKS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activityUserProfileAuthorImage!!.transitionName = intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE)
                activityUserProfileFirstName!!.transitionName = intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE_1)
                activityUserProfileLastName!!.transitionName = intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE_2)

                window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)
                window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)
            }

            mVolleyWrapper.getCallArray(links.getString(Const.USER_PHOTOS) + Const.UNSPLASH_ID + "&per_page=30&page=" + page,
                    Const.USER_IMAGES_CALLBACK)
            mVolleyWrapper.setListener(this)


            //contentUserProfileUsername.setPaintFlags(contentUserProfileUsername.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        activityUserProfileUnsplashIcon!!.setColorFilter(ContextCompat.getColor(this, R.color.white))
    }

    /**
     * On Start
     */
    override fun onStart() {
        super.onStart()

        if (author != null) {
            try {
                activityUserProfileRandomImage!!.setImageURI(Const.UNSPLASH_USER_RANDOM +
                        author!!.getString(Const.USERNAME) + "/1920x1080")

                Glide.with(this)
                        .load(author_image.getString(Const.USER_IMAGE_LARGE))
                        .into(activityUserProfileAuthorImage!!)

                activityUserProfileUsername!!.text = "@" + author!!.getString(Const.USERNAME)
                activityUserProfileFirstName!!.text = StringModifier.camelCase(author!!.getString(Const.USER_FIRST_NAME))
                collapsingToolbarLayout!!.title = author!!.getString(Const.IMAGE_USER_NAME) //full name
                collapsingToolbarLayout!!.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
                collapsingToolbarLayout!!.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white))

                val lastName = author!!.getString(Const.USER_LAST_NAME)

                if (lastName.length == 0 || lastName == "null" || lastName == null) {
                    activityUserProfileLastName!!.text = " "
                } else
                    activityUserProfileLastName!!.text = " " + StringModifier.camelCase(lastName)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(this, "Please Refresh", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * On Error Response
     *
     * @param volleyError,onCallback
     */
    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show()
        Log.d("Test", volleyError.toString())
    }

    /**
     * On Response - JSON Object
     *
     * @param response,onCallback
     */
    override fun onResponse(response: JSONObject, callback: Int) {
        when (callback) {
            Const.USER_IMAGES_CALLBACK -> if (response.has(Const.ERRORS)) {
                try {
                    Toast.makeText(this, response.getString(Const.ERRORS), Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * On Response - JSON Array
     *
     * @param response,onCallback
     */
    override fun onResponse(response: JSONArray, callback: Int) {
        when (callback) {
            Const.USER_IMAGES_CALLBACK -> {
                page++
                imagesArray = response
                mUserImagesAdapter = UserImagesAdapter(this, response, mRecyclerView)
                mRecyclerView!!.layoutManager = LinearLayoutManager(this)
                mRecyclerView!!.adapter = mUserImagesAdapter

                mUserImagesAdapter.setOnLoadMoreListener {
                    imagesArray.put(null)
                    mUserImagesAdapter.notifyItemInserted(imagesArray.length() - 1)
                    try {
                        mVolleyWrapper.getCallArray(links.getString(Const.USER_PHOTOS) + Const.UNSPLASH_ID +
                                "&per_page=30&page=" + page, Const.USER_IMAGES_LOADING_CALLBACK)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            Const.USER_IMAGES_LOADING_CALLBACK -> {
                page++
                imagesArray.remove(imagesArray.length() - 1)
                mUserImagesAdapter.notifyItemRemoved(imagesArray.length())

                for (i in 0 until response.length()) {
                    try {
                        imagesArray.put(response.getJSONObject(i))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                mUserImagesAdapter.notifyDataSetChanged()
                mUserImagesAdapter.setLoaded()
            }
        }
    }

    /**
     * On Response - String
     *
     * @param response,onCallback
     */
    override fun onResponse(response: String, callback: Int) {

    }

    /**
     * On Back Pressed
     */
    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    @OnClick(R.id.activity_user_profile_unsplash_badge)
    fun onViewClicked() {
        var url: String? = null
        try {
            url = links.getString(Const.USER_HTML) + Const.UTM_PARAMETERS
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}
