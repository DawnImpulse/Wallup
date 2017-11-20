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

package com.stonevire.wallup.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.UserImagesAdapter;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.StringModifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.content_user_profile_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.content_user_profile_nested)
    LinearLayout contentUserProfileNested;
    @BindView(R.id.activity_user_profile_author_image)
    CircleImageView activityUserProfileAuthorImage;
    @BindView(R.id.activity_user_profile_unsplash_icon)
    AppCompatImageView activityUserProfileUnsplashIcon;
    @BindView(R.id.activity_user_profile_username)
    TextView activityUserProfileUsername;

    Intent intent;

    JSONObject author;
    JSONObject author_image;
    JSONArray imagesArray;
    JSONObject links;
    UserImagesAdapter mUserImagesAdapter;

    int page = 1;

    /**
     * On Create
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        intent = getIntent();
        mVolleyWrapper = new VolleyWrapper(this);
        imagesArray = new JSONArray();

        try {
            author = new JSONObject(getIntent().getStringExtra(Const.IMAGE_USER));
            author_image = author.getJSONObject(Const.PROFILE_IMAGES);
            links = author.getJSONObject(Const.LINKS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activityUserProfileAuthorImage.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE));
                activityUserProfileFirstName.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE_1));
                activityUserProfileLastName.setTransitionName(intent.getStringExtra(Const.TRANS_NEW_TO_PROFILE_2));

                getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER));
                getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
            }

            mVolleyWrapper.getCallArray(links.getString(Const.USER_PHOTOS) + Const.UNSPLASH_ID + "&per_page=30&page=" + page,
                    Const.USER_IMAGES_CALLBACK);
            mVolleyWrapper.setListener(this);


            //contentUserProfileUsername.setPaintFlags(contentUserProfileUsername.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityUserProfileUnsplashIcon.setColorFilter(ContextCompat.getColor(this, R.color.white));
    }

    /**
     * On Start
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (author != null) {
            try {
                activityUserProfileRandomImage.setImageURI(Const.UNSPLASH_USER_RANDOM +
                        author.getString(Const.USERNAME) + "/1920x1080");

                Glide.with(this)
                        .load(author_image.getString(Const.USER_IMAGE_LARGE))
                        .into(activityUserProfileAuthorImage);

                activityUserProfileUsername.setText("@" + author.getString(Const.USERNAME));
                activityUserProfileFirstName.setText(StringModifier.camelCase(author.getString(Const.USER_FIRST_NAME)));
                collapsingToolbarLayout.setTitle(author.getString(Const.IMAGE_USER_NAME)); //full name
                collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
                collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

                String lastName = author.getString(Const.USER_LAST_NAME);

                if (lastName.length() == 0 || lastName.equals("null") || lastName.equals(null)) {
                    activityUserProfileLastName.setText(" ");
                } else
                    activityUserProfileLastName.setText(" " + StringModifier.camelCase(lastName));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please Refresh", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On Error Response
     *
     * @param volleyError,onCallback
     */
    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Toast.makeText(this, volleyError.toString(), Toast.LENGTH_SHORT).show();
        Log.d("Test", volleyError.toString());
    }

    /**
     * On Response - JSON Object
     *
     * @param response,onCallback
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
     *
     * @param response,onCallback
     */
    @Override
    public void onResponse(JSONArray response, int callback) {
        switch (callback) {
            case Const.USER_IMAGES_CALLBACK:
                page++;
                imagesArray = response;
                mUserImagesAdapter = new UserImagesAdapter(this, response, mRecyclerView);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(mUserImagesAdapter);

                mUserImagesAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        imagesArray.put(null);
                        mUserImagesAdapter.notifyItemInserted(imagesArray.length() - 1);
                        try {
                            mVolleyWrapper.getCallArray(links.getString(Const.USER_PHOTOS) + Const.UNSPLASH_ID +
                                    "&per_page=30&page=" + page, Const.USER_IMAGES_LOADING_CALLBACK);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;

            case Const.USER_IMAGES_LOADING_CALLBACK:
                page++;
                imagesArray.remove(imagesArray.length() - 1);
                mUserImagesAdapter.notifyItemRemoved(imagesArray.length());

                for (int i = 0; i < response.length(); i++) {
                    try {
                        imagesArray.put(response.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mUserImagesAdapter.notifyDataSetChanged();
                mUserImagesAdapter.setLoaded();

        }
    }

    /**
     * On Response - String
     *
     * @param response,onCallback
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

    @OnClick(R.id.activity_user_profile_unsplash_badge)
    public void onViewClicked() {
        String url = null;
        try {
            url = links.getString(Const.USER_HTML) + Const.UTM_PARAMETERS;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
