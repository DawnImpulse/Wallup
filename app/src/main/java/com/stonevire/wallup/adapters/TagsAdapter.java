package com.stonevire.wallup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Saksham on 7/12/2017.
 */

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagsHolder> {

    Context mContext;
    JSONArray tagsArray;

    public TagsAdapter(Context context, JSONArray jsonArray) {
        mContext = context;
        tagsArray = jsonArray;
    }

    @Override
    public TagsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inflator_tags, parent, false);
        TagsHolder t = new TagsHolder(v);
        return t;
    }

    @Override
    public void onBindViewHolder(final TagsHolder holder, int position) {
        try {
            holder.tagsButton.setText(tagsArray.getString(position).toUpperCase());
            holder.tagsButton.requestLayout();
            holder.drawee.setImageURI("https://source.unsplash.com/featured/120x64/?"+tagsArray.getString(position));

            ViewTreeObserver vto = holder.tagsButton.getViewTreeObserver();

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewGroup.LayoutParams lp1 = holder.drawee.getLayoutParams();
                    lp1.width   = holder.tagsButton.getWidth();
                    lp1.height  = holder.tagsButton.getHeight();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return tagsArray.length();
    }

    public class TagsHolder extends RecyclerView.ViewHolder {
        Button tagsButton;
        SimpleDraweeView drawee;

        public TagsHolder(View itemView) {
            super(itemView);
            tagsButton  = (Button) itemView.findViewById(R.id.inflator_tags_button);
            drawee      = (SimpleDraweeView) itemView.findViewById(R.id.inflator_tags_drawee);
        }
    }
}
