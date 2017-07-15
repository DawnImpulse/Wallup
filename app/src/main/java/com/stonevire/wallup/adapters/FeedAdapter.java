package com.stonevire.wallup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.DateModifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saksham on 7/15/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    JSONArray imagesArray;
    Context mContext;
    RecyclerView mRecyclerView;

    public FeedAdapter(Context context, JSONArray array, RecyclerView recyclerView) {
        mContext = context;
        imagesArray = array;
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inflator_feed, parent, false);
        return new FeedHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedHolder) {
            try {
                JSONObject object       = imagesArray.getJSONObject(position);
                JSONObject user         = object.getJSONObject(Const.IMAGE_USER);
                JSONObject urls         = object.getJSONObject(Const.IMAGE_URLS);
                JSONObject profileImage = user.getJSONObject(Const.IMAGE_USER_IMAGES);

                ((FeedHolder) holder).date.setText(DateModifier.toDateFullMonthYear(object.getString(Const.IMAGE_CREATED)));
                ((FeedHolder) holder).firstName.setText(user.getString(Const.USER_FIRST_NAME));
                ((FeedHolder) holder).lastName.setText(" "+user.getString(Const.USER_LAST_NAME));
                ((FeedHolder) holder).authorImage.setImageURI(profileImage.getString(Const.USER_IMAGE_LARGE));
                ((FeedHolder) holder).image.setImageURI(urls.getString(Const.IMAGE_REGULAR));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return imagesArray.length();
    }

   /* @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }*/

    /**
     * Loading View Holder
     */
    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
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
            authorImage = (SimpleDraweeView) itemView.findViewById(R.id.inflator_feed_author_image);
            image = (SimpleDraweeView) itemView.findViewById(R.id.inflator_feed_drawee);
            date = (TextView) itemView.findViewById(R.id.inflator_feed_date);
            firstName = (TextView) itemView.findViewById(R.id.inflator_feed_author_first_name);
            lastName = (TextView) itemView.findViewById(R.id.inflator_feed_author_last_name);
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
