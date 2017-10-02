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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.utils.Const;

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

    /**
     * On Create View Holder
     *
     * @param parent,viewType
     * @return
     */
    @Override
    public TagsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inflator_tags, parent, false);
        TagsHolder t = new TagsHolder(v);
        return t;
    }

    @Override
    public void onViewAttachedToWindow(final TagsHolder holder) {
        super.onViewAttachedToWindow(holder);
        try {
            holder.tagsButton.setText(tagsArray.getString(holder.getAdapterPosition()).toUpperCase());
            holder.tagsButton.requestLayout();
            holder.drawee.setImageURI(Const.UNSPLASH_SOURCE + "120x64/?" + tagsArray.getString(holder.getAdapterPosition()));

            ViewTreeObserver vto = holder.tagsButton.getViewTreeObserver();

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewGroup.LayoutParams lp1 = holder.drawee.getLayoutParams();
                    lp1.width = holder.tagsButton.getWidth();
                    lp1.height = holder.tagsButton.getHeight();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On Bind View Holder
     *
     * @param holder,position
     */


    @Override
    public void onBindViewHolder(final TagsHolder holder, int position) {
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
            tagsButton = (Button) itemView.findViewById(R.id.inflator_tags_button);
            drawee = (SimpleDraweeView) itemView.findViewById(R.id.inflator_tags_drawee);
        }
    }
}
