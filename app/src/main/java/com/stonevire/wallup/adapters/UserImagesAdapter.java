package com.stonevire.wallup.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.interfaces.LoadMoreListener;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 7/3/2017.
 */

public class UserImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_AD = 2;

    private LoadMoreListener mLoadMoreListener;

    JSONArray mImagesArray;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    RecyclerView mRecyclerView;
    private String append = "?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=720&fit=max";

    /**
     * Constructor
     * @param context,imagesArray,recyclerView
     */
    public UserImagesAdapter(Context context, JSONArray imagesArray, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        mImagesArray = imagesArray;

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
     * On Create View Holder
     * @param parent,viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_user_images, parent, false);
            return new UserImagesHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_AD) {
            //view = LayoutInflater.from(mContext).inflate(R.layout.inflator_native_ad, parent, false);
            //return new AdViewHolder(view);
        }
        return null;
    }

    /**
     * On Bind View Holder
     * @param holder,position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserImagesHolder) {
            try {
                JSONObject imageObject = mImagesArray.getJSONObject(position);
                JSONObject urls = imageObject.getJSONObject(Const.IMAGE_URLS);

                WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

                DisplayMetrics displaymetrics = new DisplayMetrics();       //-------------------- Getting width of screen
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;

                ViewGroup.LayoutParams lp = ((UserImagesHolder) holder).draweeView.getLayoutParams();
                lp.width = width/3;
                lp.height = width/3;
                ((UserImagesHolder) holder).draweeView.requestLayout();
                ((UserImagesHolder) holder).draweeView.setBackgroundColor(
                        Color.parseColor(imageObject.getString(Const.IMAGE_COLOR)));
                ((UserImagesHolder) holder).draweeView.setImageURI(urls.getString(Const.IMAGE_RAW) + append);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof LoadingViewHolder) {

        } else if (holder instanceof AdViewHolder) {

            /*AdRequest mAdRequest = new AdRequest.Builder().build();
            ((AdViewHolder) holder).mAdView.loadAd(mAdRequest);
            ((AdViewHolder) holder).mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    ((AdViewHolder) holder).mAdView.setVisibility(View.VISIBLE);
                }

            });*/
        }
    }

    /**
     * Get count of Items
     * @return Count of Items
     */
    @Override
    public int getItemCount() {
        return mImagesArray == null ? 0 : mImagesArray.length();
    }

    /**
     * Return the Type of view to show on Current View
     * @param position
     * @return View Type (int)
     */
    @Override
    public int getItemViewType(int position) {
        try {
            return mImagesArray.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * On Load More Listener (defined)
     * @param mLoadMoreListener
     */
    public void setOnLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    /**
     * Set Loaded false when view is loaded after On Loading
     */
    public void setLoaded() {
        isLoading = false;
    }


    /**
     * User Images View Holder
     */
    private class UserImagesHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView draweeView;

        public UserImagesHolder(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.inflator_user_images_drawee);
        }
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
     * Ad View Holder
     */
    private static class AdViewHolder extends RecyclerView.ViewHolder {
        public NativeExpressAdView mAdView;

        public AdViewHolder(View itemView) {
            super(itemView);
            mAdView = (NativeExpressAdView) itemView.findViewById(R.id.inflator_native_ad_view);
        }
    }
}
