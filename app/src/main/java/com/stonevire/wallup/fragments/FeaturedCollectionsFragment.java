package com.stonevire.wallup.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.CollectionsAdapter;
import com.stonevire.wallup.interfaces.OnLoadMoreListener;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeaturedCollectionsFragment extends Fragment implements RequestResponse,SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_featured_collections_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_featured_collections_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    VolleyWrapper volleyWrapper;
    CollectionsAdapter mCollectionsAdapter;
    JSONArray collectionArray;
    Unbinder unbinder;

    int page;

    /**
     * Empty Default Constructor
     */
    public FeaturedCollectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_featured_collections, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        volleyWrapper = new VolleyWrapper(getActivity());
        volleyWrapper.setListener(this);
        volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS, Const.COLLECTIONS_FEATURED_CALLBACK);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_SHORT).show();

        mSwipeRefreshLayout.setRefreshing(false);

        if (callback == Const.COLLECTIONS_FEATURED_LOADING_CALLBACK)
            volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=" + page, Const.COLLECTIONS_FEATURED_LOADING_CALLBACK);
    }

    @Override
    public void onResponse(JSONObject response, int callback) {

    }

    @Override
    public void onResponse(JSONArray response, int callback) {
        if (callback == Const.COLLECTIONS_FEATURED_CALLBACK) {

            collectionArray = response;
            mCollectionsAdapter = new CollectionsAdapter(getActivity(), collectionArray, mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mCollectionsAdapter);
            mRecyclerView.setNestedScrollingEnabled(true);
            page = 2;

            mCollectionsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    collectionArray.put(null);
                    mCollectionsAdapter.notifyItemInserted(collectionArray.length());

                    volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=" + page, Const.COLLECTIONS_FEATURED_LOADING_CALLBACK);
                }
            });

            mSwipeRefreshLayout.setRefreshing(false);

        } else if (callback == Const.COLLECTIONS_FEATURED_LOADING_CALLBACK) {
            page++;
            collectionArray.remove(collectionArray.length() - 1);
            mCollectionsAdapter.notifyItemRemoved(collectionArray.length() - 1);

            for (int i = 0; i < response.length(); i++) {
                try {
                    collectionArray.put(response.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mCollectionsAdapter.notifyDataSetChanged();
            mCollectionsAdapter.setLoaded();
        }

    }

    @Override
    public void onResponse(String response, int callback) {

    }

    @Override
    public void onRefresh() {
        page = 1;
        volleyWrapper.getCallArray(Const.UNSPLASH_FEATURED_COLLECTIONS + "&page=1", Const.COLLECTIONS_FEATURED_CALLBACK);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
