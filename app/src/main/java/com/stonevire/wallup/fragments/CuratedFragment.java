package com.stonevire.wallup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eyalbira.loadingdots.LoadingDots;
import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.MainAdapter;
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

public class CuratedFragment extends Fragment implements RequestResponse, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_curated_recycler)
    RecyclerView fragmentCuratedRecycler;
    @BindView(R.id.fragment_curated_swipe)
    SwipeRefreshLayout fragmentCuratedSwipe;
    @BindView(R.id.fragment_curated_loading)
    LoadingDots fragmentCuratedLoading;
    Unbinder unbinder;

    JSONArray imagesArray;
    VolleyWrapper mVolleyWrapper;
    MainAdapter mCuratedAdapter;

    int page;

    public CuratedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imagesArray = new JSONArray();
        View view = inflater.inflate(R.layout.fragment_curated, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVolleyWrapper = new VolleyWrapper(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imagesArray.length() == 0) {
            page = 1;
            mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=1", Const.CURATED_CALLBACK);
            mVolleyWrapper.setListener(this);
        }

        fragmentCuratedSwipe.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        page = 1;
        mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=1", Const.CURATED_CALLBACK);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Log.d("Test", String.valueOf(volleyError));
        Toast.makeText(getActivity(), String.valueOf(volleyError), Toast.LENGTH_SHORT).show();

        if (callback == Const.CURATED_LOAD_MORE)
            mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=" + page, Const.CURATED_LOAD_MORE);
    }

    @Override
    public void onResponse(JSONObject response, int callback) {

    }

    @Override
    public void onResponse(JSONArray response, int callback) {
        fragmentCuratedLoading.setVisibility(View.GONE);

        if (callback == Const.CURATED_CALLBACK) {
            page++;
            imagesArray = response;
            mCuratedAdapter = new MainAdapter(getActivity(), imagesArray, fragmentCuratedRecycler);
            fragmentCuratedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentCuratedRecycler.setAdapter(mCuratedAdapter);
            fragmentCuratedRecycler.setNestedScrollingEnabled(true);

            mCuratedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    imagesArray.put(null);
                    mCuratedAdapter.notifyItemInserted(imagesArray.length());

                    mVolleyWrapper.getCallArray(Const.UNSPLASH_CURATED_IMAGES + "&page=" + page, Const.CURATED_LOAD_MORE);
                }
            });

            fragmentCuratedSwipe.setRefreshing(false);

        } else if (callback == Const.CURATED_LOAD_MORE) {
            page++;
            imagesArray.remove(imagesArray.length() - 1);
            mCuratedAdapter.notifyItemRemoved(imagesArray.length() - 1);

            for (int i = 0; i < response.length(); i++) {
                try {
                    imagesArray.put(response.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mCuratedAdapter.notifyDataSetChanged();
            mCuratedAdapter.setLoaded();
        }
    }

    @Override
    public void onResponse(String response, int callback) {

    }
}
