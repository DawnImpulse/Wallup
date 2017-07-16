package com.stonevire.wallup.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.interfaces.LoadMoreListener;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.DateModifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 7/15/2017.
 */

public class LatestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JSONArray imagesArray;
    Context mContext;
    RecyclerView mRecyclerView;
    LoadMoreListener mLoadMoreListener;

    boolean isLoading;
    int VIEW_TYPE_LOADING = 0;
    int VIEW_TYPE_ITEM = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public LatestAdapter(Context context, JSONArray array, RecyclerView recyclerView) {
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_latest, parent, false);
            return new FeedHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedHolder) {
            try {
                JSONObject object = imagesArray.getJSONObject(position);
                JSONObject user = object.getJSONObject(Const.IMAGE_USER);
                JSONObject urls = object.getJSONObject(Const.IMAGE_URLS);
                JSONObject profileImage = user.getJSONObject(Const.IMAGE_USER_IMAGES);

                ((FeedHolder) holder).date.setText(DateModifier.toDateFullMonthYear(object.getString(Const.IMAGE_CREATED)));
                ((FeedHolder) holder).firstName.setText(user.getString(Const.USER_FIRST_NAME));
                ((FeedHolder) holder).lastName.setText(" " + user.getString(Const.USER_LAST_NAME));
                ((FeedHolder) holder).authorImage.setImageURI(profileImage.getString(Const.USER_IMAGE_LARGE));
                ((FeedHolder) holder).image.setImageURI(urls.getString(Const.IMAGE_REGULAR));

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
        try {
            return imagesArray.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } catch (JSONException e) {
            e.printStackTrace();
            return VIEW_TYPE_LOADING;
        }
    }

    /**
     * On Load More Listener (defined)
     *
     * @param mLoadMoreListener
     */
    public void setOnLoadMoreListener(LoadMoreListener mLoadMoreListener) {
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
    private class FeedHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView authorImage;
        SimpleDraweeView image;
        TextView date;
        TextView firstName;
        TextView lastName;

        public FeedHolder(View itemView) {
            super(itemView);
            authorImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_latest_author_image);
            image = (SimpleDraweeView) itemView.findViewById(R.id.inflator_latest_drawee);
            date = (TextView) itemView.findViewById(R.id.inflator_latest_date);
            firstName = (TextView) itemView.findViewById(R.id.inflator_latest_author_first_name);
            lastName = (TextView) itemView.findViewById(R.id.inflator_latest_author_last_name);
        }
    }

    /**
     * Ad View Holder
     */
    private class AdHolder extends RecyclerView.ViewHolder {

        public AdHolder(View itemView) {
            super(itemView);
        }
    }
}