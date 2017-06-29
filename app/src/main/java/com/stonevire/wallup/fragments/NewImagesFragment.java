package com.stonevire.wallup.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.NewImagesAdapter;
import com.stonevire.wallup.interfaces.LoadMoreListener;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewImagesFragment extends Fragment implements RequestResponse {

    VolleyWrapper volleyWrapper;
    Map<String, String> params;
    JSONArray imagesArrayTemp, imagesArray;
    NewImagesAdapter mNewImagesAdapter;
    int pageNo = 0;

    Context mContext;
    RecyclerView mRecyclerView;

    public NewImagesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        volleyWrapper = new VolleyWrapper(getContext());
        params = new HashMap<>();
        imagesArray = new JSONArray();
        imagesArrayTemp = new JSONArray();

        return inflater.inflate(R.layout.fragment_new_images, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_new_images_recycler);

        //params.put(Const.NEW_IMAGES, String.valueOf(pageNo));
        volleyWrapper.getCall(Const.TAGGING_IMAGES, Const.NEW_IMAGES_CALLBACK);
        volleyWrapper.setListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResponse(JSONObject response, int callback) {

        if (callback == Const.NEW_IMAGES_CALLBACK) {
            try {
                if (response.getString(Const.SUCCESS).equals("true")) {

                    imagesArray = response.getJSONArray(Const.IMAGES);
                    mNewImagesAdapter = new NewImagesAdapter(getContext(), imagesArray, mRecyclerView);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                    mRecyclerView.setAdapter(mNewImagesAdapter);
                    mRecyclerView.setNestedScrollingEnabled(true);

                    mNewImagesAdapter.setOnLoadMoreListener(new LoadMoreListener() {
                        @Override
                        public void onLoadMore() {

                            imagesArray.put(null);
                            mNewImagesAdapter.notifyItemInserted(imagesArray.length() - 1);
                            volleyWrapper.getCall(Const.TAGGING_IMAGES, Const.LOAD_MORE_IMAGES_CALLBACK);
                            volleyWrapper.setListener(NewImagesFragment.this);
                        }
                    });
                } else {
                    Toast.makeText(mContext, response.getString(Const.ERRORID), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (callback == Const.LOAD_MORE_IMAGES_CALLBACK) {
            try {
                if (response.getString(Const.SUCCESS).equals("true")) {

                    JSONArray tempArray = response.getJSONArray(Const.IMAGES);
                    imagesArray.remove(imagesArray.length()-1);
                    mNewImagesAdapter.notifyItemRemoved(imagesArray.length());
                    for(int i=0;i<tempArray.length();i++)
                    {
                        imagesArray.put(tempArray.get(i));
                    }
                    mNewImagesAdapter.notifyDataSetChanged();
                    mNewImagesAdapter.setLoaded();

                }else
                {
                    Toast.makeText(mContext, response.getString(Const.ERRORID), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
