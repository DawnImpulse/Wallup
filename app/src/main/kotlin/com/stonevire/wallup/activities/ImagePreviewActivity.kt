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

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.facebook.drawee.view.SimpleDraweeView
import com.gjiazhe.panoramaimageview.GyroscopeObserver
import com.stonevire.wallup.R
import com.stonevire.wallup.adapters.TagsAdapter
import com.stonevire.wallup.callbacks.MessageEvent
import com.stonevire.wallup.network.volley.RequestResponse
import com.stonevire.wallup.network.volley.VolleyWrapper
import com.stonevire.wallup.storage.BitmapStorage
import com.stonevire.wallup.utils.BitmapModifier
import com.stonevire.wallup.utils.ColorModifier
import com.stonevire.wallup.utils.Const
import com.stonevire.wallup.utils.DateModifier
import com.stonevire.wallup.utils.DisplayCalculations
import com.stonevire.wallup.utils.Permissions
import com.stonevire.wallup.utils.StringModifier

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by DawnImpulse
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - Code editing
 * DawnImpulse - 2017 10 07 - v4A - Changing URL
 * DawnImpulse - 2017 10 04 - v4A - Using Glide
 */

class ImagePreviewActivity : AppCompatActivity(), RequestResponse {

    @BindView(R.id.content_image_preview_image)
    internal var contentImagePreviewImage: ImageView? = null
    @BindView(R.id.activity_image_preview_info_button)
    internal var activityImagePreviewInfoButton: AppCompatImageView? = null
    @BindView(R.id.activity_image_preview_cross_button)
    internal var activityImagePreviewCrossButton: AppCompatImageView? = null
    @BindView(R.id.activity_image_preview_fab_layout)
    internal var activityImagePreviewFabLayout: RelativeLayout? = null
    @BindView(R.id.activity_image_preview_details_layout)
    internal var activityImagePreviewDetailsLayout: FrameLayout? = null
    @BindView(R.id.activity_image_preview_camera_make)
    internal var activityImagePreviewCameraMake: TextView? = null
    @BindView(R.id.activity_image_preview_camera_model)
    internal var activityImagePreviewCameraModel: TextView? = null
    @BindView(R.id.activity_image_preview_shutter)
    internal var activityImagePreviewShutter: TextView? = null
    @BindView(R.id.activity_image_preview_focal)
    internal var activityImagePreviewFocal: TextView? = null
    @BindView(R.id.activity_image_preview_aperture)
    internal var activityImagePreviewAperture: TextView? = null
    @BindView(R.id.activity_image_preview_iso)
    internal var activityImagePreviewIso: TextView? = null
    @BindView(R.id.activity_image_preview_dimensions)
    internal var activityImagePreviewDimensions: TextView? = null
    @BindView(R.id.activity_image_preview_published)
    internal var activityImagePreviewPublished: TextView? = null
    @BindView(R.id.activity_image_preview_tag_recycler)
    internal var activityImagePreviewTagRecycler: RecyclerView? = null
    @BindView(R.id.activity_image_preview_author_image)
    internal var activityImagePreviewAuthorImage: SimpleDraweeView? = null
    @BindView(R.id.activity_image_preview_author_first_name)
    internal var activityImagePreviewAuthorFirstName: TextView? = null
    @BindView(R.id.activity_image_preview_author_last_name)
    internal var activityImagePreviewAuthorLastName: TextView? = null
    @BindView(R.id.activity_image_preview_location)
    internal var activityImagePreviewLocation: TextView? = null
    @BindView(R.id.t1)
    internal var t1: TextView? = null
    @BindView(R.id.t2)
    internal var t2: TextView? = null
    @BindView(R.id.t3)
    internal var t3: TextView? = null
    @BindView(R.id.t4)
    internal var t4: TextView? = null
    @BindView(R.id.t5)
    internal var t5: TextView? = null
    @BindView(R.id.t6)
    internal var t6: TextView? = null
    @BindView(R.id.t7)
    internal var t7: TextView? = null
    @BindView(R.id.t8)
    internal var t8: TextView? = null
    @BindView(R.id.activity_image_preview_download)
    internal var activityImagePreviewDownload: Button? = null
    @BindView(R.id.activity_image_preview_wallpaper)
    internal var activityImagePreviewWallpaper: Button? = null
    //--------------------------------------------------

    private var gyroscopeObserver: GyroscopeObserver? = null
    private var mIntent: Intent? = null
    private var mainObject: JSONObject? = null
    private var imageObject: JSONObject? = null
    private var imageUrlsObject: JSONObject? = null
    private var authorObject: JSONObject? = null
    private var authorImages: JSONObject? = null
    private var exif: JSONObject? = null
    private val tagsArray: JSONArray? = null
    private val mTagsAdapter: TagsAdapter? = null
    private var mVolleyWrapper: VolleyWrapper? = null
    private val mBitmap: Bitmap? = null

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        supportPostponeEnterTransition()
        ButterKnife.bind(this)
        mIntent = intent
        mVolleyWrapper = VolleyWrapper(this)
        gyroscopeObserver = GyroscopeObserver()
        gyroscopeObserver!!.setMaxRotateRadian(1.5)
        /*contentImagePreviewImage.setGyroscopeObserver(gyroscopeObserver);
        contentImagePreviewImage.setEnableScrollbar(false);*/

        //setting transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentImagePreviewImage!!.transitionName = mIntent!!.getStringExtra(Const.TRANS_NEW_TO_PREVIEW_3)

            window.sharedElementEnterTransition = DraweeTransition
                    .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)
            window.sharedElementReturnTransition = DraweeTransition
                    .createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)
        }
        imageObjectParsing()
        imageDetails()
    }

    /**
     * On start - for event bus
     */
    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * On stop - for event bus
     */
    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * On resume - for gyroscope
     */
    override fun onResume() {
        super.onResume()
        // Register GyroscopeObserver.
        gyroscopeObserver!!.register(this)
        gettingBitmap()
    }

    /**
     * On pause - for gyroscope
     */
    override fun onPause() {
        super.onPause()
        // Unregister GyroscopeObserver.
        gyroscopeObserver!!.unregister()
        //contentImagePreviewImage.setImageBitmap(null);
    }

    /**
     * On click - fab, download, author
     *
     * @param v - view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.activity_image_preview_fab_layout, R.id.activity_image_preview_author_layout, R.id.activity_image_preview_download)
    fun onViewClicked(v: View) {
        when (v.id) {

        //Clicking on FAB button

            R.id.activity_image_preview_fab_layout -> if (activityImagePreviewCrossButton!!.alpha.toDouble() == 0.0) {
                fabDrawableAnimation(0)
                detailsPageAnimation(0)
            } else {
                fabDrawableAnimation(1)
                detailsPageAnimation(1)
            }

        //Clicking on Author Layout

            R.id.activity_image_preview_author_layout -> {

                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra(Const.IMAGE_USER, authorObject!!.toString())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        ViewCompat.setTransitionName(activityImagePreviewAuthorFirstName, authorObject!!.getString(Const.USER_FIRST_NAME))
                        ViewCompat.setTransitionName(activityImagePreviewAuthorLastName, authorObject!!.getString(Const.USER_LAST_NAME))
                        ViewCompat.setTransitionName(activityImagePreviewAuthorImage, authorObject!!.getString(Const.USERNAME))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE, ViewCompat.getTransitionName(activityImagePreviewAuthorImage))
                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_1, ViewCompat.getTransitionName(activityImagePreviewAuthorFirstName))
                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_2, ViewCompat.getTransitionName(activityImagePreviewAuthorLastName))

                    val pairImage = Pair.create<View, String>(activityImagePreviewAuthorImage as View?,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorImage))
                    val pairFirstName = Pair.create<View, String>(activityImagePreviewAuthorFirstName as View?,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorFirstName))
                    val pairLastName = Pair.create<View, String>(activityImagePreviewAuthorLastName as View?,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorLastName))

                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairImage, pairFirstName, pairLastName)
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(intent)
                }
            }

        //Clicking on Download button

            R.id.activity_image_preview_download -> {

                val mIntent = Intent(this@ImagePreviewActivity, Permissions::class.java)
                startActivity(mIntent)
            }
        }

    }


    /**
     * Image Object parsing
     */
    private fun imageObjectParsing() {
        try {
            //Object coming from other activity
            if (!mIntent!!.hasExtra(Const.IS_DIRECT_OBJECT)) {
                //getting image details for intent
                mainObject = JSONObject(mIntent!!.getStringExtra(Const.IMAGE_OBJECT))
                imageObject = JSONObject(mainObject!!.getString(Const.DETAILS).replace("\\", ""))
                contentImagePreviewImage!!.setBackgroundColor(Color.parseColor(imageObject!!.getString(Const.IMAGE_COLOR)))
                //start of transition
                supportStartPostponedEnterTransition()

                if (imageObject!!.has(Const.EXIF))
                    exif = imageObject!!.getJSONObject(Const.EXIF)

            } //Object from main activity
            else {
                imageObject = JSONObject(mIntent!!.getStringExtra(Const.IMAGE_OBJECT))
                contentImagePreviewImage!!.setBackgroundColor(Color.parseColor(imageObject!!.getString(Const.IMAGE_COLOR)))
                //start of transition
                supportStartPostponedEnterTransition()

                //get call for recent image details
                mVolleyWrapper!!.getCall(Const.UNSPLASH_GET_PHOTO +
                        imageObject!!.getString(Const.IMAGE_ID) + Const.UNSPLASH_ID, Const.IMAGE_PREVIEW_DETAIL_CALLBACK)
                mVolleyWrapper!!.setListener(this)
            }

            imageUrlsObject = imageObject!!.getJSONObject(Const.IMAGE_URLS)
            authorObject = imageObject!!.getJSONObject(Const.IMAGE_USER)
            authorImages = authorObject!!.getJSONObject(Const.PROFILE_IMAGES)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * Getting Bitmap from Glide
     */
    private fun gettingBitmap() {
        try {
            Glide.with(this)
                    .load(imageUrlsObject!!.getString(Const.IMAGE_RAW) + "?h=720")
                    .asBitmap()
                    .into<>(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            contentImagePreviewImage!!.setImageBitmap(mBitmap)
                            colorApplier(ColorModifier.getNonDarkColor(BitmapModifier.colorSwatch(mBitmap),
                                    this@ImagePreviewActivity))
                        }

                    })
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    /**
     * Apply the non Dark color to all highlighting parts of layout
     *
     * @param color - Color to be used
     */
    private fun colorApplier(color: Int) {

        t1!!.setTextColor(color)
        t2!!.setTextColor(color)
        t3!!.setTextColor(color)
        t4!!.setTextColor(color)
        t5!!.setTextColor(color)
        t6!!.setTextColor(color)
        t7!!.setTextColor(color)
        t8!!.setTextColor(color)

        //Get gradient drawable of the 3 buttons and then set color
        val gd = activityImagePreviewFabLayout!!.background.current as GradientDrawable
        val gd1 = activityImagePreviewDownload!!.background.current as GradientDrawable
        val gd2 = activityImagePreviewWallpaper!!.background.current as GradientDrawable
        gd.setColor(color)
        gd1.setColor(color)
        gd2.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        //gd2.setColor(color);
        //gd2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.DST_IN));
        //gd2.set
    }

    /**
     * Setting Image Details
     */
    private fun imageDetails() {
        try {
            //Exif is present in image details
            if (exif != null) {

                //camera make
                if (exif!!.has(Const.CAMERA_MAKE) && exif!!.getString(Const.CAMERA_MAKE) != "null")
                    activityImagePreviewCameraMake!!.text = exif!!.getString(Const.CAMERA_MAKE)

                //camera model
                if (exif!!.has(Const.CAMERA_MODEL) && exif!!.getString(Const.CAMERA_MODEL) != "null")
                    activityImagePreviewCameraModel!!.text = exif!!.getString(Const.CAMERA_MODEL)

                //camera shutter speed
                if (exif!!.has(Const.CAMERA_SHUTTER_SPEED) && exif!!.getString(Const.CAMERA_SHUTTER_SPEED) != "null")
                    activityImagePreviewShutter!!.text = exif!!.getString(Const.CAMERA_SHUTTER_SPEED)

                //camera focal length
                if (exif!!.has(Const.CAMERA_FOCAL_LENGTH) && exif!!.getString(Const.CAMERA_FOCAL_LENGTH) != "null")
                    activityImagePreviewFocal!!.text = exif!!.getString(Const.CAMERA_FOCAL_LENGTH)

                //camera aperture
                if (exif!!.has(Const.CAMERA_APERTURE) && exif!!.getString(Const.CAMERA_APERTURE) != "null")
                    activityImagePreviewAperture!!.text = exif!!.getString(Const.CAMERA_APERTURE)

                //camera iso
                if (exif!!.has(Const.CAMERA_ISO) && exif!!.getString(Const.CAMERA_ISO) != "null")
                    activityImagePreviewIso!!.text = exif!!.getString(Const.CAMERA_ISO)
            }

            //Applying modified date on details page
            activityImagePreviewPublished!!.text = DateModifier.toDateFullMonthYear(imageObject!!.getString(Const.IMAGE_CREATED)
                    .substring(0, imageObject!!.getString(Const.IMAGE_CREATED).indexOf("T")))

            //image width x height
            activityImagePreviewDimensions!!.text = (imageObject!!.getString(Const.IMAGE_WIDTH)
                    + " x " + imageObject!!.getString(Const.IMAGE_HEIGHT))

            //user image
            activityImagePreviewAuthorImage!!.setImageURI(authorImages!!.getString(Const.USER_IMAGE_LARGE))

            //user name
            activityImagePreviewAuthorFirstName!!.text = StringModifier.camelCase(authorObject!!.getString(Const.USER_FIRST_NAME))

            //last name
            val lastName = authorObject!!.optString(Const.USER_LAST_NAME)
            if (lastName.length == 0 || lastName == "null" || lastName == null) {
                activityImagePreviewAuthorLastName!!.text = " "
            } else
                activityImagePreviewAuthorLastName!!.text = " " + StringModifier.camelCase(lastName)

            //location
            if (imageObject!!.has(Const.LOCATION_OBJECT)) {
                val locationObject = imageObject!!.getJSONObject(Const.LOCATION_OBJECT)
                activityImagePreviewLocation!!.text = locationObject.getString(Const.LOCATION_TITLE)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * To set fab Animation of Drawable
     *
     * @param i , 0 - fading Info Drawable and visa versa
     */
    private fun fabDrawableAnimation(i: Int) {

        var fadeOut: ObjectAnimator? = null
        var fadeIn: ObjectAnimator? = null
        var rotationInfo: Animation? = null
        var rotationCross: Animation? = null
        var mAnimationSet: AnimatorSet? = null

        //Fading Info Drawable
        if (i == 0) {
            fadeOut = ObjectAnimator.ofFloat(activityImagePreviewInfoButton, "alpha", 1f, 0f)
            fadeIn = ObjectAnimator.ofFloat(activityImagePreviewCrossButton, "alpha", 0f, 1f)
            rotationInfo = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_info_away)
            fadeIn!!.duration = 500

        } // Fading Cross Drawable
        else {
            fadeOut = ObjectAnimator.ofFloat(activityImagePreviewCrossButton, "alpha", 1f, 0f)
            fadeIn = ObjectAnimator.ofFloat(activityImagePreviewInfoButton, "alpha", 0f, 1f)
            rotationInfo = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_info_back)
            fadeIn!!.duration = 1000
        }

        rotationCross = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_cross)
        fadeOut!!.duration = 500

        mAnimationSet = AnimatorSet()
        mAnimationSet.play(fadeOut)
        mAnimationSet.play(fadeIn)
        mAnimationSet.start()
        activityImagePreviewInfoButton!!.startAnimation(rotationInfo)
        activityImagePreviewCrossButton!!.startAnimation(rotationCross)
    }

    /**
     * Show/Hide details overlay
     *
     * @param i , 0 - show
     */
    private fun detailsPageAnimation(i: Int) {
        var anim: Animator? = null
        var radius = 0f
        var cx = 0
        var cy = 0

        //showing the details section with animation
        if (i == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cx = activityImagePreviewDetailsLayout!!.width - DisplayCalculations.dpToPx(44, this)
                cy = activityImagePreviewDetailsLayout!!.height - DisplayCalculations.dpToPx(44, this)
                radius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                anim = ViewAnimationUtils.createCircularReveal(activityImagePreviewDetailsLayout, cx, cy, 0f, radius)

                activityImagePreviewDetailsLayout!!.visibility = View.VISIBLE
                anim!!.start()
            } else {
                activityImagePreviewDetailsLayout!!.visibility = View.VISIBLE
            }

        }//disabling the details section with animation
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cx = activityImagePreviewDetailsLayout!!.width - DisplayCalculations.dpToPx(44, this)
                cy = activityImagePreviewDetailsLayout!!.height - DisplayCalculations.dpToPx(44, this)
                radius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                anim = ViewAnimationUtils.createCircularReveal(activityImagePreviewDetailsLayout, cx, cy, radius, 0f)
                anim!!.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        activityImagePreviewDetailsLayout!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })

                anim.start()

            } else {
                activityImagePreviewDetailsLayout!!.visibility = View.GONE
            }
        }
    }

    /**
     * Error response
     * @param volleyError
     * @param callback
     */
    override fun onErrorResponse(volleyError: VolleyError, callback: Int) {
        Log.d("Test", volleyError.toString())
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show()
    }

    /**
     * Image object response
     * @param response
     * @param callback
     */
    override fun onResponse(response: JSONObject, callback: Int) {
        if (callback == Const.IMAGE_PREVIEW_DETAIL_CALLBACK) {
            if (response.has(Const.IMAGE_ID)) {
                try {
                    val exif = response.getJSONObject(Const.EXIF)
                    run {
                        if (exif != null) {

                            //camera make
                            if (exif.has(Const.CAMERA_MAKE) && exif.getString(Const.CAMERA_MAKE) != "null")
                                activityImagePreviewCameraMake!!.text = exif.getString(Const.CAMERA_MAKE)

                            //camera model
                            if (exif.has(Const.CAMERA_MODEL) && exif.getString(Const.CAMERA_MODEL) != "null")
                                activityImagePreviewCameraModel!!.text = exif.getString(Const.CAMERA_MODEL)

                            //camera shutter speed
                            if (exif.has(Const.CAMERA_SHUTTER_SPEED) && exif.getString(Const.CAMERA_SHUTTER_SPEED) != "null")
                                activityImagePreviewShutter!!.text = exif.getString(Const.CAMERA_SHUTTER_SPEED)

                            //camera focal length
                            if (exif.has(Const.CAMERA_FOCAL_LENGTH) && exif.getString(Const.CAMERA_FOCAL_LENGTH) != "null")
                                activityImagePreviewFocal!!.text = exif.getString(Const.CAMERA_FOCAL_LENGTH)

                            //camera aperture
                            if (exif.has(Const.CAMERA_APERTURE) && exif.getString(Const.CAMERA_APERTURE) != "null")
                                activityImagePreviewAperture!!.text = exif.getString(Const.CAMERA_APERTURE)

                            //camera iso
                            if (exif.has(Const.CAMERA_ISO) && exif.getString(Const.CAMERA_ISO) != "null")
                                activityImagePreviewIso!!.text = exif.getString(Const.CAMERA_ISO)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else if (response.has(Const.ERRORS)) {
                try {
                    Log.d("Test", response.getString(Const.ERRORS))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * Not useful here
     * @param response
     * @param callback
     */
    override fun onResponse(response: JSONArray, callback: Int) {

    }

    /**
     * Not useful here
     * @param response
     * @param callback
     */
    override fun onResponse(response: String, callback: Int) {

    }

    /**
     * Back press H/W
     */
    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    //Event bus message for saving image after storage permission
    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        if (event.message == "Storage Permission Available") {

            try {
                var result = false
                result = BitmapStorage.saveToInternalStorage(mBitmap, imageObject!!.getString(Const.IMAGE_ID) + ".jpg")
                Toast.makeText(this, java.lang.Boolean.toString(result), Toast.LENGTH_SHORT).show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }
}
