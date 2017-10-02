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

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 9/13/2017.
 */

public class CollectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    JSONArray collectionsArray;
    RecyclerView mRecyclerView;
    OnLoadMoreListener mLoadMoreListener;

    boolean isLoading;
    int VIEW_TYPE_LOADING = 0;
    int VIEW_TYPE_ITEM = 1;
    int VIEW_TYPE_AD = 3;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    JSONObject collectionObject;
    JSONObject coverPhotoObject;
    JSONObject userObject;

    /**
     * Public Constructor
     *
     * @param context
     * @param array
     * @param recyclerView
     */
    public CollectionsAdapter(Context context, JSONArray array, RecyclerView recyclerView) {
        mContext = context;
        collectionsArray = array;
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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_collections, parent, false);
            return new CollectionsHolder(v);

        } else if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CollectionsHolder) {

            try {
                collectionObject = collectionsArray.getJSONObject(position);
                coverPhotoObject = collectionObject.getJSONObject(Const.COVER_PHOTO);
                userObject = collectionObject.getJSONObject(Const.USER);

                JSONObject coverPhotoUrls = coverPhotoObject.getJSONObject(Const.IMAGE_URLS);
                JSONObject authorUrls = userObject.getJSONObject(Const.PROFILE_IMAGES);

                ((CollectionsHolder) holder).coverImage.setBackgroundColor(Color.parseColor(coverPhotoObject.getString(Const.IMAGE_COLOR)));
                ((CollectionsHolder) holder).coverImage.setImageURI(coverPhotoUrls.getString(Const.IMAGE_REGULAR));
                ((CollectionsHolder) holder).authorImage.setImageURI(authorUrls.getString(Const.USER_IMAGE_LARGE));
                ((CollectionsHolder) holder).authorName.setText(userObject.getString(Const.IMAGE_USER_NAME));
                ((CollectionsHolder) holder).collectionName.setText(collectionObject.getString(Const.COLLECTION_TITLE));
                ((CollectionsHolder) holder).totalPhotos.setText(collectionObject.getString(Const.TOTAL_PHOTOS) + " photos");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return collectionsArray.length();
    }

    @Override
    public int getItemViewType(int position) {
        return collectionsArray.isNull(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
    private class CollectionsHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView coverImage;
        TextView authorName;
        LinearLayout authorLayout;
        SimpleDraweeView authorImage;
        TextView totalPhotos;
        TextView collectionName;

        public CollectionsHolder(View itemView) {
            super(itemView);

            coverImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_collection_cover_image);
            authorName = (TextView) itemView.findViewById(R.id.inflator_collection_author_name);
            authorLayout = (LinearLayout) itemView.findViewById(R.id.inflator_collection_author_layout);
            authorImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_collection_author_image);
            totalPhotos = (TextView) itemView.findViewById(R.id.inflator_collection_photos);
            collectionName = (TextView) itemView.findViewById(R.id.inflator_collection_collections_name);
        }
    }
}
