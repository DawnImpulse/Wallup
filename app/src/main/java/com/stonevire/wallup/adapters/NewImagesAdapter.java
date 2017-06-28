package com.stonevire.wallup.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.interfaces.LoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 6/28/2017.
 */

public class NewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private LoadMoreListener mLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private JSONArray imagesArray;
    private RecyclerView mRecyclerView;

    String append = "?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=720&fit=max";

    public NewImagesAdapter(Context context, JSONArray imagesArray, RecyclerView recyclerView)
    {
        this.imagesArray   = imagesArray;
        this.mContext      = context;
        this.mRecyclerView = recyclerView;

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
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.inflator_new_image, parent, false);
            return new NewImagesHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof NewImagesHolder)
        {
            try {
                JSONObject jsonObject = imagesArray.getJSONObject(position);
                String details = jsonObject.getString("details");
                String new_details = details.replace("\\", "");

                JSONObject detailsObject = new JSONObject(new_details);
                JSONObject urls = detailsObject.getJSONObject("urls");

                ((NewImagesHolder) holder).draweeView.setBackgroundColor(Color.parseColor(detailsObject.getString("color")));
                ((NewImagesHolder) holder).draweeView.setImageURI(urls.getString("raw") + append);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(holder instanceof LoadingViewHolder)
        {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return imagesArray == null ? 0 : imagesArray.length();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return imagesArray.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return VIEW_TYPE_LOADING;
    }

    public void setOnLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private static class NewImagesHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView draweeView;

        public NewImagesHolder(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.inflator_new_image_drawee);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.inflator_loading_view_progress);
        }
    }
}
