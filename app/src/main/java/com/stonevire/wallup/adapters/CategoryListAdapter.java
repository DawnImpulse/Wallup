package com.stonevire.wallup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.utils.Const;
import com.stonevire.wallup.utils.DisplayCalculations;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Saksham on 7/13/2017.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    Context mContext;
    JSONArray mJsonArray;

    public CategoryListAdapter(Context context, JSONArray jsonArray) {
        mContext    = context;
        mJsonArray  = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inflator_category_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WindowManager wm                = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics   = new DisplayMetrics();       //-------------------- Getting width of screen
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width                       = displaymetrics.widthPixels;
        int height                      = displaymetrics.widthPixels;

        ViewGroup.LayoutParams lp1 = holder.drawee1.getLayoutParams();
        ViewGroup.LayoutParams lp2 = holder.drawee2.getLayoutParams();
        ViewGroup.LayoutParams lp3 = holder.drawee3.getLayoutParams();
        ViewGroup.LayoutParams lp4 = holder.drawee4.getLayoutParams();
        ViewGroup.LayoutParams lp5 = holder.v.getLayoutParams();

        lp1.width   = width  / 2;
        lp1.height  = height / 2;
        lp2.width   = width  / 2 - DisplayCalculations.dpToPx(20,mContext);
        lp2.height  = height / 4;
        lp3.width   = width  / 4 - DisplayCalculations.dpToPx(12,mContext);
        lp3.height  = height / 4 - DisplayCalculations.dpToPx(4,mContext);
        lp4.width   = width  / 4 - DisplayCalculations.dpToPx(12,mContext);
        lp4.height  = height / 4 - DisplayCalculations.dpToPx(4,mContext);
        lp5.width   = width  / 4 - DisplayCalculations.dpToPx(12,mContext);
        lp5.height  = height / 4 - DisplayCalculations.dpToPx(4,mContext);

        holder.drawee1.requestLayout();
        holder.drawee2.requestLayout();
        holder.drawee3.requestLayout();
        holder.drawee4.requestLayout();

        try {
            holder.text.setText(mJsonArray.getString(position));
            holder.drawee1.setImageURI(Const.UNSPLASH_SOURCE + width/2 + "x240/?" + mJsonArray.getString(position));
            holder.drawee2.setImageURI(Const.UNSPLASH_SOURCE + width/2 + "x120/?" + mJsonArray.getString(position));
            holder.drawee3.setImageURI(Const.UNSPLASH_SOURCE + width/4 + "x120/?" + mJsonArray.getString(position));
            holder.drawee4.setImageURI(Const.UNSPLASH_SOURCE + width/4 + "x130/?" + mJsonArray.getString(position));

            Log.d("Test",Const.UNSPLASH_SOURCE + width/2 + "x240/?" + mJsonArray.getString(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mJsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView drawee1;
        SimpleDraweeView drawee2;
        SimpleDraweeView drawee3;
        SimpleDraweeView drawee4;
        TextView text;
        View v;

        public ViewHolder(View itemView) {
            super(itemView);
            drawee1 = (SimpleDraweeView) itemView.findViewById(R.id.inflator_category_list_drawee1);
            drawee2 = (SimpleDraweeView) itemView.findViewById(R.id.inflator_category_list_drawee2);
            drawee3 = (SimpleDraweeView) itemView.findViewById(R.id.inflator_category_list_drawee3);
            drawee4 = (SimpleDraweeView) itemView.findViewById(R.id.inflator_category_list_drawee4);
            text    = (TextView)         itemView.findViewById(R.id.inflator_category_list_category_name);
            v       =                    itemView.findViewById(R.id.inflator_category_list_view);
        }
    }
}
