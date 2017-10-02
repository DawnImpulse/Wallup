package com.stonevire.wallup.fragments;

import android.os.Build;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LatestFragment extends Fragment implements RequestResponse, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_latest_recycler)
    RecyclerView fragmentLatestRecycler;
    @BindView(R.id.fragment_latest_swipe)
    SwipeRefreshLayout fragmentLatestSwipe;
    @BindView(R.id.fragment_latest_loading)
    LoadingDots fragmentLatestLoading;
    Unbinder unbinder;

    VolleyWrapper mVolleyWrapper;
    JSONArray imagesArray;
    MainAdapter mFeedAdapter;

    int page;

    public LatestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVolleyWrapper = new VolleyWrapper(getActivity());
        imagesArray = new JSONArray();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imagesArray.length() == 0) {
            page = 1;
            mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=1", Const.LATEST_CALLBACK);
            mVolleyWrapper.setListener(this);
        }

        fragmentLatestSwipe.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {

        fragmentLatestSwipe.setRefreshing(false);
        Log.d("Test", String.valueOf(volleyError));
        Toast.makeText(getActivity(), String.valueOf(volleyError), Toast.LENGTH_SHORT).show();

        if (callback == Const.LATEST_LOAD_MORE)
            mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=" + page, Const.LATEST_LOAD_MORE);
    }

    @Override
    public void onResponse(JSONObject response, int callback) {
    }

    @Override
    public void onResponse(JSONArray response, int callback) {
        fragmentLatestLoading.setVisibility(View.GONE);
        if (callback == Const.LATEST_CALLBACK) {
            page++;
            imagesArray = response;
            mFeedAdapter = new MainAdapter(getActivity(), imagesArray, fragmentLatestRecycler);
            fragmentLatestRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentLatestRecycler.setAdapter(mFeedAdapter);
            fragmentLatestRecycler.setNestedScrollingEnabled(true);

            mFeedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    imagesArray.put(null);
                    mFeedAdapter.notifyItemInserted(imagesArray.length());

                    mVolleyWrapper.getCallArray(Const.UNSPLASH_TRENDING_IMAGES + "&page=" + page, Const.LATEST_LOAD_MORE);
                }
            });

            fragmentLatestSwipe.setRefreshing(false);
        } else if (callback == Const.LATEST_LOAD_MORE) {
            try {
                page++;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    imagesArray.remove(imagesArray.length() - 1);
                    mFeedAdapter.notifyItemRemoved(imagesArray.length());
                }
                for (int i = 0; i < response.length(); i++) {
                    imagesArray.put(response.getJSONObject(i));
                }
                mFeedAdapter.notifyDataSetChanged();
                mFeedAdapter.setLoaded();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onResponse(String response, int callback) {

    }

    @Override
    public void onRefresh() {
        page = 1;
        mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES + "&page=1", Const.LATEST_CALLBACK);
    }
}
