package com.stonevire.wallup.fragments;

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
import com.stonevire.wallup.adapters.FeedAdapter;
import com.stonevire.wallup.network.volley.RequestResponse;
import com.stonevire.wallup.network.volley.VolleyWrapper;
import com.stonevire.wallup.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements RequestResponse {

    @BindView(R.id.fragment_feed_recycler)
    RecyclerView fragmentFeedRecycler;
    Unbinder unbinder;

    VolleyWrapper mVolleyWrapper;
    JSONArray imagesArray;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view       = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder        = ButterKnife.bind(this, view);
        mVolleyWrapper  = new VolleyWrapper(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVolleyWrapper.getCallArray(Const.UNSPLASH_LATEST_IMAGES, Const.FEED_CALLBACK);
        mVolleyWrapper.setListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError, int callback) {
        Log.d("Test", String.valueOf(volleyError));
        Toast.makeText(getActivity(), String.valueOf(volleyError), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response, int callback) {
    }

    @Override
    public void onResponse(JSONArray response, int callback) {
        fragmentFeedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentFeedRecycler.setAdapter(new FeedAdapter(getActivity(),response,fragmentFeedRecycler));
    }

    @Override
    public void onResponse(String response, int callback) {

    }
}
