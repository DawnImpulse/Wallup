package com.stonevire.wallup.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.TagsAdapter;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.storage.BitmapStorage;
import com.stonevire.wallup.utils.BitmapModifier;
import com.stonevire.wallup.utils.ColorModifier;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.DateModifier;
import com.stonevire.wallup.utils.DisplayCalculations;
import com.stonevire.wallup.utils.MessageEvent;
import com.stonevire.wallup.utils.Permissions;
import com.stonevire.wallup.utils.StringModifier;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagePreviewActivity extends AppCompatActivity implements RequestResponse {

    @BindView(R.id.content_image_preview_image)
    PanoramaImageView contentImagePreviewImage;
    @BindView(R.id.activity_image_preview_info_button)
    AppCompatImageView activityImagePreviewInfoButton;
    @BindView(R.id.activity_image_preview_cross_button)
    AppCompatImageView activityImagePreviewCrossButton;
    @BindView(R.id.activity_image_preview_fab_layout)
    RelativeLayout activityImagePreviewFabLayout;
    @BindView(R.id.activity_image_preview_details_layout)
    FrameLayout activityImagePreviewDetailsLayout;
    @BindView(R.id.activity_image_preview_camera_make)
    TextView activityImagePreviewCameraMake;
    @BindView(R.id.activity_image_preview_camera_model)
    TextView activityImagePreviewCameraModel;
    @BindView(R.id.activity_image_preview_shutter)
    TextView activityImagePreviewShutter;
    @BindView(R.id.activity_image_preview_focal)
    TextView activityImagePreviewFocal;
    @BindView(R.id.activity_image_preview_aperture)
    TextView activityImagePreviewAperture;
    @BindView(R.id.activity_image_preview_iso)
    TextView activityImagePreviewIso;
    @BindView(R.id.activity_image_preview_dimensions)
    TextView activityImagePreviewDimensions;
    @BindView(R.id.activity_image_preview_published)
    TextView activityImagePreviewPublished;
    @BindView(R.id.activity_image_preview_tag_recycler)
    RecyclerView activityImagePreviewTagRecycler;
    @BindView(R.id.activity_image_preview_author_image)
    SimpleDraweeView activityImagePreviewAuthorImage;
    @BindView(R.id.activity_image_preview_author_first_name)
    TextView activityImagePreviewAuthorFirstName;
    @BindView(R.id.activity_image_preview_author_last_name)
    TextView activityImagePreviewAuthorLastName;
    @BindView(R.id.activity_image_preview_location)
    TextView activityImagePreviewLocation;
    @BindView(R.id.t1)
    TextView t1;
    @BindView(R.id.t2)
    TextView t2;
    @BindView(R.id.t3)
    TextView t3;
    @BindView(R.id.t4)
    TextView t4;
    @BindView(R.id.t5)
    TextView t5;
    @BindView(R.id.t6)
    TextView t6;
    @BindView(R.id.t7)
    TextView t7;
    @BindView(R.id.t8)
    TextView t8;
    @BindView(R.id.activity_image_preview_download)
    Button activityImagePreviewDownload;
    @BindView(R.id.activity_image_preview_wallpaper)
    Button activityImagePreviewWallpaper;

    private GyroscopeObserver gyroscopeObserver;
    Intent mIntent;
    JSONObject mainObject, imageObject, imageUrlsObject, authorObject, authorImages, exif = null;
    JSONArray tagsArray = null;
    TagsAdapter mTagsAdapter;
    VolleyWrapper mVolleyWrapper;

    //The Bitmap of the Image
    Bitmap mBitmap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        supportPostponeEnterTransition();
        ButterKnife.bind(this);
        mIntent = getIntent();
        mVolleyWrapper = new VolleyWrapper(this);

        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(1.5);
        contentImagePreviewImage.setGyroscopeObserver(gyroscopeObserver);
        contentImagePreviewImage.setEnableScrollbar(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentImagePreviewImage.setTransitionName(mIntent.getStringExtra(Const.TRANS_NEW_TO_PREVIEW_3));

            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
        imageObjectParsing();
        imageDetails();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this);
        gettingBitmap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
        //contentImagePreviewImage.setImageBitmap(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.activity_image_preview_fab_layout, R.id.activity_image_preview_author_layout,
            R.id.activity_image_preview_download})
    public void onViewClicked(View v) {
        switch (v.getId()) {

            //Clicking on FAB button

            case R.id.activity_image_preview_fab_layout:
                if (activityImagePreviewCrossButton.getAlpha() == 0.0) {
                    fabDrawableAnimation(0);
                    detailsPageAnimation(0);
                } else {
                    fabDrawableAnimation(1);
                    detailsPageAnimation(1);
                }
                break;

            //Clicking on Author Layout

            case R.id.activity_image_preview_author_layout:

                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Const.IMAGE_USER, authorObject.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        ViewCompat.setTransitionName(activityImagePreviewAuthorFirstName, authorObject.getString(Const.USER_FIRST_NAME));
                        ViewCompat.setTransitionName(activityImagePreviewAuthorLastName, authorObject.getString(Const.USER_LAST_NAME));
                        ViewCompat.setTransitionName(activityImagePreviewAuthorImage, authorObject.getString(Const.USERNAME));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE, ViewCompat.getTransitionName(activityImagePreviewAuthorImage));
                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_1, ViewCompat.getTransitionName(activityImagePreviewAuthorFirstName));
                    intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_2, ViewCompat.getTransitionName(activityImagePreviewAuthorLastName));

                    Pair<View, String> pairImage = Pair.create((View) activityImagePreviewAuthorImage,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorImage));
                    Pair<View, String> pairFirstName = Pair.create((View) activityImagePreviewAuthorFirstName,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorFirstName));
                    Pair<View, String> pairLastName = Pair.create((View) activityImagePreviewAuthorLastName,
                            ViewCompat.getTransitionName(activityImagePreviewAuthorLastName));

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, pairImage, pairFirstName, pairLastName);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;

            //Clicking on Download button

            case R.id.activity_image_preview_download :

                Intent mIntent = new Intent(ImagePreviewActivity.this,Permissions.class);
                startActivity(mIntent);
                break;

        }

    }


    /**
     * Image Object parsing
     */
    private void imageObjectParsing() {
        try {
            if (!mIntent.hasExtra(Const.IS_DIRECT_OBJECT)) {
                mainObject = new JSONObject(mIntent.getStringExtra(Const.IMAGE_OBJECT));
                String details = mainObject.getString(Const.DETAILS);
                String details1 = details.replace("\\", "");
                imageObject = new JSONObject(details1);

                if (mainObject.has(Const.TAGS)) {
                    String tag = mainObject.getString(Const.TAGS).replace("\\", "");
                    tagsArray = new JSONArray(tag);
                }
            } else {
                imageObject = new JSONObject(mIntent.getStringExtra(Const.IMAGE_OBJECT));
                mVolleyWrapper.getCall(Const.UNSPLASH_GET_PHOTO +
                        imageObject.getString(Const.IMAGE_ID) + Const.UNSPLASH_ID, Const.IMAGE_PREVIEW_DETAIL_CALLBACK);
                mVolleyWrapper.setListener(this);
            }
            imageUrlsObject = imageObject.getJSONObject(Const.IMAGE_URLS);
            authorObject = imageObject.getJSONObject(Const.IMAGE_USER);
            authorImages = authorObject.getJSONObject(Const.PROFILE_IMAGES);

            if (imageObject.has(Const.EXIF))
                exif = imageObject.getJSONObject(Const.EXIF);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting Bitmap from Fresco
     */
    private void gettingBitmap() {
        ImageRequest request = null;
        try {

            request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(imageUrlsObject.getString(Const.IMAGE_REGULAR)))
                    .build();
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            final DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(request, this);

            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    try {
                        if (dataSource.isFinished() && bitmap != null) {
                            mBitmap = bitmap.copy(bitmap.getConfig(),true);
                            if (mBitmap != null)
                            {
                                contentImagePreviewImage.setImageBitmap(mBitmap);
                                supportStartPostponedEnterTransition();
                                colorApplier(ColorModifier.getNonDarkColor(BitmapModifier.colorSwatch(mBitmap), ImagePreviewActivity.this));
                            }
                            dataSource.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (dataSource != null) {
                        dataSource.close();
                    }
                }
            }, UiThreadImmediateExecutorService.getInstance());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply the non Dark color to all highlighting parts of layout
     *
     * @param color - Color to be used
     */
    private void colorApplier(int color) {

        t1.setTextColor(color);
        t2.setTextColor(color);
        t3.setTextColor(color);
        t4.setTextColor(color);
        t5.setTextColor(color);
        t6.setTextColor(color);
        t7.setTextColor(color);
        t8.setTextColor(color);

        //Get gradient drawable of the 3 buttons and then set color
        GradientDrawable gd = (GradientDrawable) activityImagePreviewFabLayout.getBackground().getCurrent();
        GradientDrawable gd1 = (GradientDrawable) activityImagePreviewDownload.getBackground().getCurrent();
        GradientDrawable gd2 = (GradientDrawable) activityImagePreviewWallpaper.getBackground().getCurrent();
        gd.setColor(color);
        gd1.setColor(color);
        gd2.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        //gd2.setColor(color);
        //gd2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.DST_IN));
        //gd2.set
    }

    /**
     * Setting Image Details
     */
    private void imageDetails() {
        try {
            if (exif != null) {
                if (exif.has(Const.CAMERA_MAKE) && !exif.getString(Const.CAMERA_MAKE).equals("null"))
                    activityImagePreviewCameraMake.setText(exif.getString(Const.CAMERA_MAKE));
                if (exif.has(Const.CAMERA_MODEL) && !exif.getString(Const.CAMERA_MODEL).equals("null"))
                    activityImagePreviewCameraModel.setText(exif.getString(Const.CAMERA_MODEL));
                if (exif.has(Const.CAMERA_SHUTTER_SPEED) && !exif.getString(Const.CAMERA_SHUTTER_SPEED).equals("null"))
                    activityImagePreviewShutter.setText(exif.getString(Const.CAMERA_SHUTTER_SPEED));
                if (exif.has(Const.CAMERA_FOCAL_LENGTH) && !exif.getString(Const.CAMERA_FOCAL_LENGTH).equals("null"))
                    activityImagePreviewFocal.setText(exif.getString(Const.CAMERA_FOCAL_LENGTH));
                if (exif.has(Const.CAMERA_APERTURE) && !exif.getString(Const.CAMERA_APERTURE).equals("null"))
                    activityImagePreviewAperture.setText(exif.getString(Const.CAMERA_APERTURE));
                if (exif.has(Const.CAMERA_ISO) && !exif.getString(Const.CAMERA_ISO).equals("null"))
                    activityImagePreviewIso.setText(exif.getString(Const.CAMERA_ISO));
            }

            activityImagePreviewPublished.setText(DateModifier.toDateFullMonthYear(imageObject.getString(Const.IMAGE_CREATED)
                    .substring(0, imageObject.getString(Const.IMAGE_CREATED).indexOf("T"))));

            activityImagePreviewDimensions.setText(imageObject.getString(Const.IMAGE_WIDTH)
                    + " x " + imageObject.getString(Const.IMAGE_HEIGHT));

            if (tagsArray != null) {
                mTagsAdapter = new TagsAdapter(this, tagsArray);
                activityImagePreviewTagRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                activityImagePreviewTagRecycler.setAdapter(mTagsAdapter);
            }

            activityImagePreviewAuthorImage.setImageURI(authorImages.getString(Const.USER_IMAGE_LARGE));
            activityImagePreviewAuthorFirstName.setText(StringModifier.camelCase(authorObject.getString(Const.USER_FIRST_NAME)));

            String lastName = authorObject.getString(Const.USER_LAST_NAME);
            if (lastName.length()==0 || lastName.equals("null") || lastName.equals(null))
            {
                activityImagePreviewAuthorLastName.setText(" " );
            }else
                activityImagePreviewAuthorLastName.setText(" " + StringModifier.camelCase(lastName));

            if (imageObject.has(Const.LOCATION_OBJECT)) {
                JSONObject locationObject = imageObject.getJSONObject(Const.LOCATION_OBJECT);
                activityImagePreviewLocation.setText(locationObject.getString(Const.LOCATION_TITLE));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * To set fab Animation of Drawable
     *
     * @param i , 0 - fading Info Drawable and visa versa
     */
    private void fabDrawableAnimation(int i) {
        ObjectAnimator fadeOut = null, fadeIn = null;
        Animation rotationInfo, rotationCross;
        if (i == 0) { //Fading Info Drawable
            fadeOut = ObjectAnimator.ofFloat(activityImagePreviewInfoButton, "alpha", 1f, 0f);
            fadeIn = ObjectAnimator.ofFloat(activityImagePreviewCrossButton, "alpha", 0f, 1f);
            rotationInfo = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_info_away);
            fadeIn.setDuration(500);
        } else { // Fading Cross Drawable
            fadeOut = ObjectAnimator.ofFloat(activityImagePreviewCrossButton, "alpha", 1f, 0f);
            fadeIn = ObjectAnimator.ofFloat(activityImagePreviewInfoButton, "alpha", 0f, 1f);
            rotationInfo = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_info_back);
            fadeIn.setDuration(1000);
        }
        rotationCross = AnimationUtils.loadAnimation(this, R.anim.rotation_fab_cross);
        fadeOut.setDuration(500);

        AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut);
        mAnimationSet.play(fadeIn);

        mAnimationSet.start();
        activityImagePreviewInfoButton.startAnimation(rotationInfo);
        activityImagePreviewCrossButton.startAnimation(rotationCross);
    }

    /**
     * Show/Hide Details Overlay
     *
     * @param i , 0 - show
     */
    private void detailsPageAnimation(int i) {
        if (i == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = activityImagePreviewDetailsLayout.getWidth() - DisplayCalculations.dpToPx(44, this);
                int cy = activityImagePreviewDetailsLayout.getHeight() - DisplayCalculations.dpToPx(44, this);
                float radius = (float) Math.hypot(cx, cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(activityImagePreviewDetailsLayout, cx, cy, 0, radius);

                activityImagePreviewDetailsLayout.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                activityImagePreviewDetailsLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = activityImagePreviewDetailsLayout.getWidth() - DisplayCalculations.dpToPx(44, this);
                int cy = activityImagePreviewDetailsLayout.getHeight() - DisplayCalculations.dpToPx(44, this);
                float radius = (float) Math.hypot(cx, cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(activityImagePreviewDetailsLayout, cx, cy, radius, 0);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        activityImagePreviewDetailsLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
            } else {
                activityImagePreviewDetailsLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Log.d("Test", volleyError.toString());
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response, int callback) {
        if (callback == Const.IMAGE_PREVIEW_DETAIL_CALLBACK) {
            if (response.has(Const.IMAGE_ID)) {
                try {
                    JSONObject exif = response.getJSONObject(Const.EXIF);
                    {
                        if (exif != null) {
                            if (exif.has(Const.CAMERA_MAKE) && !exif.getString(Const.CAMERA_MAKE).equals("null"))
                                activityImagePreviewCameraMake.setText(exif.getString(Const.CAMERA_MAKE));
                            if (exif.has(Const.CAMERA_MODEL) && !exif.getString(Const.CAMERA_MODEL).equals("null"))
                                activityImagePreviewCameraModel.setText(exif.getString(Const.CAMERA_MODEL));
                            if (exif.has(Const.CAMERA_SHUTTER_SPEED) && !exif.getString(Const.CAMERA_SHUTTER_SPEED).equals("null"))
                                activityImagePreviewShutter.setText(exif.getString(Const.CAMERA_SHUTTER_SPEED));
                            if (exif.has(Const.CAMERA_FOCAL_LENGTH) && !exif.getString(Const.CAMERA_FOCAL_LENGTH).equals("null"))
                                activityImagePreviewFocal.setText(exif.getString(Const.CAMERA_FOCAL_LENGTH));
                            if (exif.has(Const.CAMERA_APERTURE) && !exif.getString(Const.CAMERA_APERTURE).equals("null"))
                                activityImagePreviewAperture.setText(exif.getString(Const.CAMERA_APERTURE));
                            if (exif.has(Const.CAMERA_ISO) && !exif.getString(Const.CAMERA_ISO).equals("null"))
                                activityImagePreviewIso.setText(exif.getString(Const.CAMERA_ISO));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (response.has(Const.ERRORS)) {
                try {
                    Log.d("Test", response.getString(Const.ERRORS));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResponse(JSONArray response, int callback) {

    }

    @Override
    public void onResponse(String response, int callback) {

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals("Storage Permission Available"))
        {
            boolean result = BitmapStorage.saveToInternalStorage(mBitmap,"wall-1.jpg");
            Toast.makeText(this, Boolean.toString(result), Toast.LENGTH_SHORT).show();
        }
    }
}
