package com.stonevire.wallup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.activities.ImagePreviewActivity;
import com.stonevire.wallup.activities.UserProfileActivity;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.utils.ColorModifier;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 6/28/2017.
 */

public class NewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_AD = 2;

    private OnLoadMoreListener mLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private JSONArray imagesArray;
    private RecyclerView mRecyclerView;

    /**
     * Constructor
     *
     * @param context,imagesArray,recyclerView
     */
    public NewImagesAdapter(Context context, JSONArray imagesArray, RecyclerView recyclerView) {
        this.imagesArray = imagesArray;
        this.mContext = context;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_new_image, parent, false);
            return new NewImagesHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_loading_view, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_AD) {
            view = LayoutInflater.from(mContext).inflate(R.layout.inflator_native_ad, parent, false);
            return new AdViewHolder(view);
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
        if (holder instanceof NewImagesHolder) {
            try {

                JSONObject jsonObject = imagesArray.getJSONObject(position);
                String details = jsonObject.getString(Const.DETAILS);
                String new_details = details.replace("\\", "");
                final JSONObject detailsObject = new JSONObject(new_details);
                final JSONObject urls = detailsObject.getJSONObject(Const.IMAGE_URLS);
                JSONObject author = detailsObject.getJSONObject(Const.IMAGE_USER);
                JSONObject author_image = author.getJSONObject(Const.IMAGE_USER_IMAGES);

                if (detailsObject.has(Const.LOCATION_OBJECT)) {
                    JSONObject locationObject = detailsObject.getJSONObject(Const.LOCATION_OBJECT);
                    ((NewImagesHolder) holder).location.setText(locationObject.getString(Const.LOCATION_TITLE));
                }

                // Adding Background Color as View Hierarchy
                Drawable b = new Drawable() {
                    @Override
                    public void draw(@NonNull Canvas canvas) {
                        try {
                            canvas.drawColor(Color.parseColor(detailsObject.getString(Const.IMAGE_COLOR)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

                    }

                    @Override
                    public void setColorFilter(@Nullable ColorFilter colorFilter) {

                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.UNKNOWN;
                    }
                };

                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(mContext.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setFadeDuration(300)
                        .setBackground(b)
                        .build();

                //Adding Information
                ((NewImagesHolder) holder).draweeView.setHierarchy(hierarchy);
                ((NewImagesHolder) holder).bottomBar.setBackgroundColor(Color.parseColor(detailsObject.getString(Const.IMAGE_COLOR)));
                ((NewImagesHolder) holder).draweeView.setImageURI(urls.getString(Const.IMAGE_REGULAR));
                ((NewImagesHolder) holder).authorImage.setImageURI(author_image.getString(Const.USER_IMAGE_LARGE));
                ((NewImagesHolder) holder).authorFirstName.setText(author.getString(Const.USER_FIRST_NAME));
                ((NewImagesHolder) holder).authorLastName.setText(" " + author.getString(Const.USER_LAST_NAME));
                ((NewImagesHolder) holder).favourite.setText(detailsObject.getString(Const.IMAGE_LIKES));

                // Adding Dynamic Color
                ((NewImagesHolder) holder).authorFirstName.setTextColor(
                        ColorModifier.getBlackOrWhite(detailsObject.getString(Const.IMAGE_COLOR), mContext));
                ((NewImagesHolder) holder).authorLastName.setTextColor(
                        ColorModifier.getBlackOrWhite(detailsObject.getString(Const.IMAGE_COLOR), mContext));
                ((NewImagesHolder) holder).favourite.setTextColor(
                        ColorModifier.getBlackOrWhite(detailsObject.getString(Const.IMAGE_COLOR), mContext));
                ((NewImagesHolder) holder).location.setTextColor(
                        ColorModifier.getBlackOrWhite(detailsObject.getString(Const.IMAGE_COLOR), mContext));
                ((NewImagesHolder) holder).heart.setColorFilter(
                        ColorModifier.getBlackOrWhite(detailsObject.getString(Const.IMAGE_COLOR), mContext));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewCompat.setTransitionName(((NewImagesHolder) holder).authorImage, author.getString(Const.USERNAME));
                    ViewCompat.setTransitionName(((NewImagesHolder) holder).authorFirstName,
                            author.getString(Const.USER_FIRST_NAME) + Const.USERNAME);
                    ViewCompat.setTransitionName(((NewImagesHolder) holder).authorLastName,
                            author.getString(Const.USER_LAST_NAME) + Const.USERNAME);
                    ViewCompat.setTransitionName(((NewImagesHolder) holder).draweeView, author.getString(Const.USERNAME));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (holder instanceof LoadingViewHolder) {

            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

        } else if (holder instanceof AdViewHolder) {

            AdRequest mAdRequest = new AdRequest.Builder().build();
            ((AdViewHolder) holder).mAdView.loadAd(mAdRequest);
        }
    }

    /**
     * Get Item View Count
     *
     * @return Count of Items
     */
    @Override
    public int getItemCount() {
        return imagesArray == null ? 0 : imagesArray.length();
    }

    /**
     * What type of item does the current view should show
     *
     * @param position
     * @return Item Type (int)
     */
    @Override
    public int getItemViewType(int position) {
        try {
            if (position == 5) {
                //return VIEW_TYPE_AD;
            }
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
     * New Images View Holder
     */
    private class NewImagesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView draweeView;
        SimpleDraweeView authorImage;
        TextView authorFirstName;
        TextView authorLastName;
        TextView favourite;
        TextView location;
        LinearLayout authorLayout;
        RelativeLayout bottomBar;
        ImageView heart;

        public NewImagesHolder(View itemView) {
            super(itemView);

            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.inflator_new_image_drawee);
            authorImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_new_image_user_image);
            authorFirstName = (TextView) itemView.findViewById(R.id.inflator_new_image_user_first_name);
            authorLastName = (TextView) itemView.findViewById(R.id.inflator_new_image_user_last_name);
            favourite = (TextView) itemView.findViewById(R.id.inflator_new_image_favourite);
            location = (TextView) itemView.findViewById(R.id.inflator_new_image_location);
            authorLayout = (LinearLayout) itemView.findViewById(R.id.inflator_new_image_author_layout);
            bottomBar = (RelativeLayout) itemView.findViewById(R.id.inflator_new_image_bottom_bar);
            heart = (ImageView) itemView.findViewById(R.id.inflator_new_image_heart);

            authorLayout.setOnClickListener(this);
            draweeView.setOnClickListener(this);
        }

        /**
         * On Click View Holder
         *
         * @param v - View
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {

            JSONObject jsonObject = null;
            try {

                jsonObject = imagesArray.getJSONObject(getAdapterPosition());
                String details = jsonObject.getString(Const.DETAILS);
                String new_details = details.replace("\\", "");
                JSONObject detailsObject = new JSONObject(new_details);
                JSONObject author = detailsObject.getJSONObject(Const.IMAGE_USER);

                switch (v.getId()) {

                    case R.id.inflator_new_image_author_layout:
                        Intent intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra(Const.IMAGE_USER, String.valueOf(author));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.putExtra(Const.TRANS_NEW_TO_PROFILE, ViewCompat.getTransitionName(authorImage));
                            intent.putExtra(Const.TRANS_NEW_TO_PROFILE_1, ViewCompat.getTransitionName(authorFirstName));
                            intent.putExtra(Const.TRANS_NEW_TO_PROFILE_2, ViewCompat.getTransitionName(authorLastName));

                            Pair<View, String> pairImage = Pair.create((View) authorImage, ViewCompat.getTransitionName(authorImage));
                            Pair<View, String> pairFirstName = Pair.create((View) authorFirstName, ViewCompat.getTransitionName(authorFirstName));
                            Pair<View, String> pairLastName = Pair.create((View) authorLastName, ViewCompat.getTransitionName(authorLastName));

                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) mContext, pairImage, pairFirstName, pairLastName);
                            mContext.startActivity(intent, options.toBundle());
                        }else
                        {
                            mContext.startActivity(intent);
                        }
                        break;

                    case R.id.inflator_new_image_drawee:
                        Intent intent1 = new Intent(mContext, ImagePreviewActivity.class);
                        intent1.putExtra(Const.IMAGE_OBJECT, jsonObject.toString()); //sending cleaned image string object
                        intent1.putExtra(Const.TRANS_NEW_TO_PREVIEW_3, ViewCompat.getTransitionName(draweeView));

                        ActivityOptionsCompat options1 = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext, draweeView, ViewCompat.getTransitionName(draweeView));
                        mContext.startActivity(intent1, options1.toBundle());
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
