package com.stonevire.wallup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.activities.ImagePreviewActivity;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
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

    private OnLoadMoreListener mLoadMoreListener;

    JSONArray mImagesArray;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    RecyclerView mRecyclerView;

    /**
     * Constructor
     *
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
                /*mLinearLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (mImagesArray.isNull(position))
                            return 3;
                        return 1;
                    }
                });*/

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
     *
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
        }
        return null;
    }

    /**
     * On Bind View Holder
     *
     * @param holder,position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserImagesHolder) {
            try {
                JSONObject imageObject = mImagesArray.getJSONObject(position);
                JSONObject urls = imageObject.getJSONObject(Const.IMAGE_URLS);
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

                //-------------------- Getting width of screen
                /*DisplayMetrics displaymetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;

                ViewGroup.LayoutParams lp = ((UserImagesHolder) holder).draweeView.getLayoutParams();
                lp.width = width / 3;
                lp.height = width / 3;*/

                ((UserImagesHolder) holder).draweeView.setBackgroundColor(
                        Color.parseColor(imageObject.getString(Const.IMAGE_COLOR)));
                ((UserImagesHolder) holder).draweeView.setImageURI(urls.getString(Const.IMAGE_REGULAR));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewCompat.setTransitionName(((UserImagesHolder) holder).draweeView, imageObject.getString(Const.IMAGE_ID));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * Get count of Items
     *
     * @return Count of Items
     */
    @Override
    public int getItemCount() {
        return mImagesArray == null ? 0 : mImagesArray.length();
    }

    /**
     * Return the Type of view to show on Current View
     *
     * @param position
     * @return View Type (int)
     */
    @Override
    public int getItemViewType(int position) {

        if (mImagesArray.isNull(position))
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_ITEM;
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
     * Set Loaded false when view is loaded after On Loading
     */
    public void setLoaded() {
        isLoading = false;
    }


    /**
     * User Images View Holder
     */
    private class UserImagesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView draweeView;

        public UserImagesHolder(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.inflator_user_images_drawee);
            draweeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.inflator_user_images_drawee:
                        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
                        intent.putExtra(Const.IMAGE_OBJECT, mImagesArray.getJSONObject(getAdapterPosition()).toString());
                        intent.putExtra(Const.IS_DIRECT_OBJECT,"true");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_USER_TO_PREVIEW, ViewCompat.getTransitionName(draweeView));
                            ActivityOptionsCompat options1 = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) mContext, draweeView, ViewCompat.getTransitionName(draweeView));
                            mContext.startActivity(intent, options1.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
}
