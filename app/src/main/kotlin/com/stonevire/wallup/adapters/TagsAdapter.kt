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

package com.stonevire.wallup.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button

import com.facebook.drawee.view.SimpleDraweeView
import com.stonevire.wallup.R
import com.stonevire.wallup.utils.Const

import org.json.JSONArray
import org.json.JSONException

/**
 * Created by Saksham on 7/12/2017.
 */

class TagsAdapter(internal var mContext: Context, internal var tagsArray: JSONArray) : RecyclerView.Adapter<TagsAdapter.TagsHolder>() {

    /**
     * On Create View Holder
     *
     * @param parent,viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.inflator_tags, parent, false)
        return TagsHolder(v)
    }

    override fun onViewAttachedToWindow(holder: TagsHolder?) {
        super.onViewAttachedToWindow(holder)
        try {
            holder!!.tagsButton.setText(tagsArray.getString(holder.adapterPosition).toUpperCase())
            holder.tagsButton.requestLayout()
            holder.drawee.setImageURI(Const.UNSPLASH_SOURCE + "120x64/?" + tagsArray.getString(holder.adapterPosition))

            val vto = holder.tagsButton.viewTreeObserver

            vto.addOnGlobalLayoutListener {
                val lp1 = holder.drawee.layoutParams
                lp1.width = holder.tagsButton.width
                lp1.height = holder.tagsButton.height
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * On Bind View Holder
     *
     * @param holder,position
     */


    override fun onBindViewHolder(holder: TagsHolder, position: Int) {}


    override fun getItemCount(): Int {
        return tagsArray.length()
    }

    inner class TagsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tagsButton: Button
        internal var drawee: SimpleDraweeView

        init {
            tagsButton = itemView.findViewById<View>(R.id.inflator_tags_button) as Button
            drawee = itemView.findViewById<View>(R.id.inflator_tags_drawee) as SimpleDraweeView
        }
    }
}
