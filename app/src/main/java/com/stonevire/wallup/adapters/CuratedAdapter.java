package com.stonevire.wallup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.activities.ImagePreviewActivity;
import com.stonevire.wallup.activities.UserProfileActivity;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 7/16/2017.
 */

public class CuratedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JSONArray imagesArray;
    Context mContext;
    RecyclerView mRecyclerView;
    OnLoadMoreListener mLoadMoreListener;

    boolean isLoading;
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_ITEM = 1;
    static final int VIEW_TYPE_AD = 2;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public CuratedAdapter(Context context, JSONArray array, RecyclerView recyclerView) {
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
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                v = LayoutInflater.from(mContext).inflate(R.layout.inflator_curated, parent, false);
                return new CuratedHolder(v);
            case VIEW_TYPE_LOADING:
                v = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
                return new LoadingViewHolder(v);
            case VIEW_TYPE_AD: //
            default:
                v = LayoutInflater.from(mContext).inflate(R.layout.inflator_native_ad, parent, false);
                return new AdHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
                try {
                    JSONObject object = imagesArray.getJSONObject(position);
                    JSONObject user = object.getJSONObject(Const.IMAGE_USER);
                    JSONObject urls = object.getJSONObject(Const.IMAGE_URLS);
                    JSONObject profileImage = user.getJSONObject(Const.IMAGE_USER_IMAGES);

                    ((CuratedHolder) holder).firstName.setText(user.getString(Const.USER_FIRST_NAME));
                    ((CuratedHolder) holder).lastName.setText(" " + user.getString(Const.USER_LAST_NAME));
                    ((CuratedHolder) holder).authorImage.setImageURI(profileImage.getString(Const.USER_IMAGE_LARGE));
                    ((CuratedHolder) holder).image.setImageURI(urls.getString(Const.IMAGE_REGULAR));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ViewCompat.setTransitionName(((CuratedHolder) holder).authorImage, user.getString(Const.USERNAME));
                        ViewCompat.setTransitionName(((CuratedHolder) holder).firstName, user.getString(Const.USER_FIRST_NAME));
                        ViewCompat.setTransitionName(((CuratedHolder) holder).lastName, user.getString(Const.USER_LAST_NAME));
                        ViewCompat.setTransitionName(((CuratedHolder) holder).image, user.getString(Const.USERNAME));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case VIEW_TYPE_LOADING:
                ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
                break;
            case VIEW_TYPE_AD: //
            default:
                ((AdHolder) holder).mAdView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public int getItemCount() {
        return imagesArray.length();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 5) {
            //return VIEW_TYPE_AD;
        }
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
    private class CuratedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView authorImage;
        SimpleDraweeView image;
        TextView firstName;
        TextView lastName;
        LinearLayout authorLayout;

        public CuratedHolder(View itemView) {
            super(itemView);
            authorImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_curated_author_image);
            image = (SimpleDraweeView) itemView.findViewById(R.id.inflator_curated_drawee);
            firstName = (TextView) itemView.findViewById(R.id.inflator_curated_author_first_name);
            lastName = (TextView) itemView.findViewById(R.id.inflator_curated_author_last_name);
            authorLayout = (LinearLayout) itemView.findViewById(R.id.inflator_curated_author_layout);

            authorLayout.setOnClickListener(this);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            JSONObject object;
            try {
                object = imagesArray.getJSONObject(getAdapterPosition());
                JSONObject user = object.getJSONObject(Const.IMAGE_USER);

                switch (v.getId()) {
                    case R.id.inflator_curated_author_layout:
                        Intent intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra(Const.IMAGE_USER, user.toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_CURATED_TO_PROFILE, ViewCompat.getTransitionName(authorImage));
                            intent.putExtra(Const.TRANS_CURATED_TO_PROFILE_1, ViewCompat.getTransitionName(firstName));
                            intent.putExtra(Const.TRANS_CURATED_TO_PROFILE_2, ViewCompat.getTransitionName(lastName));

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

                    case R.id.inflator_curated_drawee:
                        Intent intent1 = new Intent(mContext, ImagePreviewActivity.class);
                        intent1.putExtra(Const.IS_DIRECT_OBJECT, "true");
                        intent1.putExtra(Const.IMAGE_OBJECT, object.toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent1.putExtra(Const.TRANS_CURATED_TO_PREVIEW, ViewCompat.getTransitionName(image));
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
