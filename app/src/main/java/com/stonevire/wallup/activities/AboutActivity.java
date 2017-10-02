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

package com.stonevire.wallup.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.stonevire.wallup.R;
import com.stonevire.wallup.utils.DisplayCalculations;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.content_about_linear1)
    LinearLayout contentAboutLinear1;
    @BindView(R.id.content_about_linear2)
    LinearLayout contentAboutLinear2;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settingWidth();
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this,android.R.color.transparent));
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.white));

    }

    /**
     * Setting the width of linear layouts in Card 1
     */
    private void settingWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();       //-------------------- Getting width of screen
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        width -= DisplayCalculations.dpToPx(16,this);

        ViewGroup.LayoutParams lp1 = contentAboutLinear1.getLayoutParams();
        ViewGroup.LayoutParams lp2 = contentAboutLinear2.getLayoutParams();

        lp1.width = width / 2;
        lp1.height = width / 2;
        lp2.width = width / 2;
        lp2.height = width / 2;

        contentAboutLinear1.requestLayout();
        contentAboutLinear2.requestLayout();
    }
}
