package com.stonevire.wallup.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stonevire.wallup.R;
import com.stonevire.wallup.adapters.CategoryListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryListFragment extends Fragment {


    @BindView(R.id.fragment_category_list_recycler)
    RecyclerView fragmentCategoryListRecycler;
    Unbinder unbinder;
    JSONArray categoryListArray;

    public CategoryListFragment() {
        String list         = "[\"Mountain\",\"Sea\",\"River\",\"Sunset\"]";
        try {
            categoryListArray   = new JSONArray(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_category_list, container, false);
        unbinder    = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentCategoryListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentCategoryListRecycler.setAdapter(new CategoryListAdapter(getActivity(),categoryListArray));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
