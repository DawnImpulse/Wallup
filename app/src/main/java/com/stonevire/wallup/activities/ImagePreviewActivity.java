package com.stonevire.wallup.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.utils.DisplayCalculations;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagePreviewActivity extends AppCompatActivity {

    @BindView(R.id.content_image_preview_image)
    PanoramaImageView contentImagePreviewImage;
    @BindView(R.id.activity_image_preview_info_button)
    AppCompatImageView activityImagePreviewInfoButton;
    @BindView(R.id.activity_image_preview_cross_button)
    AppCompatImageView activityImagePreviewCrossButton;
    @BindView(R.id.activity_image_preview_fab_layout)
    RelativeLayout activityImagePreviewFabLayout;
    @BindView(R.id.activity_image_preview_details_layout)
    RelativeLayout activityImagePreviewDetailsLayout;


    private GyroscopeObserver gyroscopeObserver;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);

        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(1.5);
        //contentImagePreviewImage.setGyroscopeObserver(gyroscopeObserver);
        contentImagePreviewImage.setEnableScrollbar(false);

        Intent i = getIntent();
        contentImagePreviewImage.setTransitionName(i.getStringExtra("transName"));
        getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER));
        getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(i.getStringExtra("url")))
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, this);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                contentImagePreviewImage.setImageBitmap(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, UiThreadImmediateExecutorService.getInstance());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.activity_image_preview_fab_layout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.activity_image_preview_fab_layout:
                if (activityImagePreviewCrossButton.getAlpha() == 0.0) {
                    fabDrawableAnimation(0);
                    detailsPageAnimation(0);
                } else {
                    fabDrawableAnimation(1);
                    detailsPageAnimation(1);
                }
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
}
