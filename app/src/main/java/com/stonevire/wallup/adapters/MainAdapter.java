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

package com.stonevire.wallup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.NativeExpressAdView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.activities.ImagePreviewActivity;
import com.stonevire.wallup.activities.UserProfileActivity;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.StringModifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Saksham on 2017 07 15
 * Last Branch Update - v4A
 * Updates :
 * DawnImpulse - 2017 10 07 - v4A - URL changes
 * DawnImpulse - 2017 10 06 - v4A - Changing layout for Main images
 * DawnImpulse - 2017 10 05 - v4A - Fixing repeating images in recycler
 * DawnImpulse - 2017 10 04 - v4A - Using Glide
 */

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JSONArray imagesArray;
    Context mContext;
    RecyclerView mRecyclerView;
    OnLoadMoreListener mLoadMoreListener;

    boolean isLoading;
    int VIEW_TYPE_LOADING = 0;
    int VIEW_TYPE_ITEM = 1;
    int VIEW_TYPE_AD = 3;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    int currentPosition;

    /**
     * Constructor
     *
     * @param context
     * @param array
     * @param recyclerView
     */
    public MainAdapter(Context context, JSONArray array, RecyclerView recyclerView) {
        mContext = context;
        imagesArray = array;
        mRecyclerView = recyclerView;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                final LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                totalItemCount = mLinearLayoutManager.getItemCount();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_main, parent, false);
            return new FeedHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(v);
        } else if (viewType == VIEW_TYPE_AD) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_native_ad, parent, false);
            return new AdHolder(v);
        }
        return null;
    }

    /**
     * Bind view holder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedHolder) {
            try {
                final JSONObject object = imagesArray.getJSONObject(position);
                JSONObject user = object.getJSONObject(Const.IMAGE_USER);
                JSONObject urls = object.getJSONObject(Const.IMAGE_URLS);
                JSONObject profileImage = user.getJSONObject(Const.PROFILE_IMAGES);

                ((FeedHolder) holder).image.setBackgroundColor(Color.parseColor(object.getString(Const.IMAGE_COLOR)));
                ((FeedHolder) holder).firstName.setText(StringModifier.camelCase(user.getString(Const.USER_FIRST_NAME)));

                String lastName = user.getString(Const.USER_LAST_NAME);
                if (lastName.length() == 0 || lastName.equals("null") || lastName.equals(null)) {
                    ((FeedHolder) holder).lastName.setText(" ");
                } else
                    ((FeedHolder) holder).lastName.setText(" " + StringModifier.camelCase(lastName));

                Glide.with(mContext)
                        .load(urls.getString(Const.IMAGE_RAW) + "?h=720")
                        .into(((FeedHolder) holder).image);

                Glide.with(mContext)
                        .load(profileImage.getString(Const.USER_IMAGE_LARGE))
                        .into(((FeedHolder) holder).authorImage);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewCompat.setTransitionName(((FeedHolder) holder).authorImage, user.getString(Const.USERNAME));
                    ViewCompat.setTransitionName(((FeedHolder) holder).firstName, user.getString(Const.USER_FIRST_NAME));
                    ViewCompat.setTransitionName(((FeedHolder) holder).lastName, user.getString(Const.USER_LAST_NAME));
                    ViewCompat.setTransitionName(((FeedHolder) holder).image, user.getString(Const.USERNAME));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return imagesArray.length();
    }

    @Override
    public int getItemViewType(int position) {
        return imagesArray.isNull(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * On Load More Listener (defined)
     *
     * @param mLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    /**
     * Set Loaded to false when view is loaded after On Load More
     */
    public void setLoaded() {
        isLoading = false;
    }

    /**
     * Loading View Holder
     */
    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.inflator_loading_view_progress);
        }
    }

    /**
     * Feed Holder
     */
    private class FeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView authorImage;
        ImageView image;
        TextView firstName;
        TextView lastName;
        RelativeLayout authorLayout;

        public FeedHolder(View itemView) {
            super(itemView);
            authorImage = (CircleImageView) itemView.findViewById(R.id.inflator_main_author_image);
            image = (ImageView) itemView.findViewById(R.id.inflator_main_drawee);
            firstName = (TextView) itemView.findViewById(R.id.inflator_latest_main_first_name);
            lastName = (TextView) itemView.findViewById(R.id.inflator_main_author_last_name);
            authorLayout = (RelativeLayout) itemView.findViewById(R.id.inflator_main_author_layout);

            authorLayout.setOnClickListener(this);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            JSONObject object = null;
            try {
                object = imagesArray.getJSONObject(getAdapterPosition());
                JSONObject user = object.getJSONObject(Const.IMAGE_USER);

                switch (v.getId()) {
                    case R.id.inflator_main_author_layout:
                        Intent intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra(Const.IMAGE_USER, user.toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE, ViewCompat.getTransitionName(authorImage));
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_1, ViewCompat.getTransitionName(firstName));
                            intent.putExtra(Const.TRANS_LATEST_TO_PROFILE_2, ViewCompat.getTransitionName(lastName));

                            Pair<View, String> pairImage = Pair.create((View) authorImage, ViewCompat.getTransitionName(authorImage));
                            Pair<View, String> pairFirstName = Pair.create((View) firstName, ViewCompat.getTransitionName(firstName));
                            Pair<View, String> pairLastName = Pair.create((View) lastName, ViewCompat.getTransitionName(lastName));

                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) mContext, pairImage, pairFirstName, pairLastName);
                            mContext.startActivity(intent, options.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }

                        break;

                    case R.id.inflator_main_drawee:
                        Intent intent1 = new Intent(mContext, ImagePreviewActivity.class);
                        intent1.putExtra(Const.IMAGE_OBJECT, object.toString());
                        intent1.putExtra(Const.IS_DIRECT_OBJECT, "true");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent1.putExtra(Const.TRANS_LATEST_TO_PREVIEW, ViewCompat.getTransitionName(image));
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) mContext, image, ViewCompat.getTransitionName(image));
                            mContext.startActivity(intent1, options.toBundle());
                        } else {
                            mContext.startActivity(intent1);
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ad View Holder
     */
    private class AdHolder extends RecyclerView.ViewHolder {
        public NativeExpressAdView mAdView;

        public AdHolder(View itemView) {
            super(itemView);
            mAdView = (NativeExpressAdView) itemView.findViewById(R.id.inflator_native_ad_view);
        }
    }
}
