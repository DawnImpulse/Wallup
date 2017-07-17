package com.stonevire.wallup.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.UserImagesAdapter;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity implements RequestResponse {
    VolleyWrapper mVolleyWrapper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_user_profile_random_image)
    SimpleDraweeView activityUserProfileRandomImage;
    @BindView(R.id.activity_user_profile_first_name)
    TextView activityUserProfileFirstName;
    @BindView(R.id.activity_user_profile_last_name)
    TextView activityUserProfileLastName;
    @BindView(R.id.content_user_profile_image)
    SimpleDraweeView contentUserProfileImage;
    @BindView(R.id.content_user_profile_images)
    TextView contentUserProfileImages;
    @BindView(R.id.content_user_profile_likes)
    TextView contentUserProfileLikes;
    @BindView(R.id.content_user_profile_location)
    TextView contentUserProfileLocation;
    @BindView(R.id.content_user_profile_username)
    TextView contentUserProfileUsername;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.content_user_profile_recycler)
    RecyclerView mRecyclerView;

    Intent intent;

    JSONObject author;
    JSONObject author_images;
    JSONObject links;

    UserImagesAdapter mUserImagesAdapter;

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        intent           = getIntent();
        mVolleyWrapper   = new VolleyWrapper(this);

        try {
            author          = new JSONObject(getIntent().getStringExtra(Const.IMAGE_USER));
            author_images   = author.getJSONObject(Const.IMAGE_USER_IMAGES);
            links           = author.getJSONObject(Const.LINKS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentUserProfileImage.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PREVIEW));
                activityUserProfileFirstName.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PREVIEW_1));
                activityUserProfileLastName.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PREVIEW_2));

                getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER));
                getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
            }

            mVolleyWrapper.getCallArray(links.getString(Const.USER_PHOTOS) + Const.UNSPLASH_ID, Const.USER_IMAGES_CALLBACK);
            mVolleyWrapper.setListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On Start
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (author != null) {
            try {
                contentUserProfileImage.setImageURI(
                        author_images.getString(Const.USER_IMAGE_LARGE));
                contentUserProfileLikes.setText(author.getString(Const.USER_TOTAL_LIKES));
                contentUserProfileImages.setText(author.getString(Const.USER_TOTAL_PHOTOS));
                contentUserProfileUsername.setText("@" + author.getString(Const.USERNAME));
                activityUserProfileFirstName.setText(author.getString(Const.USER_FIRST_NAME));
                activityUserProfileRandomImage.setImageURI(Const.UNSPLASH_USER_RANDOM +
                        author.getString(Const.USERNAME) + "/1920x1080");

                collapsingToolbarLayout.setTitle(author.getString(Const.IMAGE_USER_NAME)); //full name
                collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
                collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

                if (!author.getString(Const.USER_LAST_NAME).equals("null")) {
                    activityUserProfileLastName.setText(author.getString(Const.USER_LAST_NAME));
                }
                if (!author.getString(Const.USER_LOCATION).equals("null")) {
                    contentUserProfileLocation.setText(author.getString(Const.USER_LOCATION));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please Refresh", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On Error Response
     * @param volleyError,callback
     */
    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show();
        Log.d("Test", volleyError.toString());
    }

    /**
     * On Response - JSON Object
     * @param response,callback
     */
    @Override
    public void onResponse(JSONObject response, int callback) {
        switch (callback) {
            case Const.USER_IMAGES_CALLBACK:
                if (response.has(Const.ERRORS)) {
                    try {
                        Toast.makeText(this, response.getString(Const.ERRORS), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    /**
     * On Response - JSON Array
     * @param response,callback
     */
    @Override
    public void onResponse(JSONArray response, int callback) {
        switch (callback) {
            case Const.USER_IMAGES_CALLBACK:
                mUserImagesAdapter = new UserImagesAdapter(this, response, mRecyclerView);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                mRecyclerView.setAdapter(mUserImagesAdapter);
                mRecyclerView.setNestedScrollingEnabled(true);

        }
    }

    /**
     * On Response - String
     * @param response,callback
     */
    @Override
    public void onResponse(String response, int callback) {

    }

    /**
     * On Back Pressed
     */
    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
