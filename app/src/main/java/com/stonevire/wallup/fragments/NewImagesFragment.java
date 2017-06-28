package com.stonevire.wallup.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.NewImagesAdapter;
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
public class NewImagesFragment extends Fragment implements RequestResponse{

    VolleyWrapper volleyWrapper;
    Map<String,String> params;
    JSONArray imageArray;
    NewImagesAdapter mNewImagesAdapter;
    int pageNo = 0;

    Context mContext;
    RecyclerView mRecyclerView;

    public NewImagesFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        volleyWrapper    = new VolleyWrapper(getContext());
        params           = new HashMap<>();
        imageArray       = new JSONArray();

        return inflater.inflate(R.layout.fragment_new_images, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_new_images_recycler);

        //params.put(Const.NEW_IMAGES, String.valueOf(pageNo));
        volleyWrapper.getCall(Const.TAGGING_IMAGES,Const.NEW_IMAGES_CALLBACK);
        volleyWrapper.setListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response, int callback) {

        try {
            if (response.getString(Const.SUCCESS).equals("true"))
            {
                JSONArray imagesArray1 = response.getJSONArray(Const.IMAGES);
                if (!imagesArray1.toString().equals(imageArray.toString()))
                {
                    Log.d("Test",imagesArray1.toString());
                    imageArray = imagesArray1;
                    mNewImagesAdapter = new NewImagesAdapter(getContext(),imageArray,mRecyclerView);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                    mRecyclerView.setAdapter(mNewImagesAdapter);
                    mRecyclerView.setNestedScrollingEnabled(true);
                }else
                {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
