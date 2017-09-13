package com.stonevire.wallup.activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stonevire.wallup.R;
import com.stonevire.wallup.fragments.CuratedFragment;
import com.stonevire.wallup.fragments.LatestFragment;
import com.stonevire.wallup.fragments.TrendingFragment;
import com.stonevire.wallup.network.volley.VolleyWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.include_search_layout_powered_text)
    LinearLayout includeSearchLayoutPoweredText;
    @BindView(R.id.include_search_layout_recycler)
    RecyclerView includeSearchLayoutRecycler;
    @BindView(R.id.include_search_layout_external)
    LinearLayout includeSearchLayoutExternal;
    @BindView(R.id.activity_main_search_overflow)
    RelativeLayout activityMainSearchOverflow;
    @BindView(R.id.activity_main_search_close)
    AppCompatImageView activityMainSearchClose;
    @BindView(R.id.activity_main_voice_search)
    AppCompatImageView activityMainVoiceSearch;
    @BindView(R.id.activity_main_search_text)
    EditText activityMainSearchText;
    @BindView(R.id.nav_drawer_user_image)
    SimpleDraweeView navDrawerUserImage;
    @BindView(R.id.nav_drawer_user_full_name)
    TextView navDrawerUserFullName;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    VolleyWrapper mVolleyWrapper;
    Map<String, String> params;
    boolean searchTextBoxEmpty = true; // to check if edit text is currently empty or not

    /**
     * On Create
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVolleyWrapper = new VolleyWrapper(this);
        params = new HashMap<>();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        activityMainVoiceSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        toolbar.setOnTouchListener(this);
        activityMainSearchOverflow.setOnTouchListener(this);

        initNavigationDrawer();
    }

    /**
     * Menu Options Create
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Menu Options Click
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = activityMainSearchOverflow.getWidth();
                int cy = activityMainSearchOverflow.getHeight() / 2;
                float radius = (float) Math.hypot(cx, cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, 0, radius);

                activityMainSearchOverflow.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);
                anim.start();
                includeSearchLayoutExternal.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.INVISIBLE);
            } else {
                activityMainSearchOverflow.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);
                includeSearchLayoutExternal.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.INVISIBLE);
            }
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        //Intent to Live Images Activity
        if (id == R.id.action_live_images) {
            Intent intent = new Intent(MainActivity.this, LiveImagesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On Activity Result
     *
     * @param requestCode,resultCode,data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            activityMainSearchText.setText(spokenText);
            activityMainVoiceSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_close));
            searchTextBoxEmpty = false;
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * On Click Listener
     *
     * @param view
     */
    @OnClick({R.id.activity_main_search_close, R.id.activity_main_voice_search, R.id.nav_drawer_profile,
              R.id.nav_drawer_collections, R.id.nav_drawer_live_images, R.id.nav_drawer_settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.activity_main_search_close:
                closeSearchBar();
                break;

            case R.id.activity_main_voice_search:
                if (searchTextBoxEmpty)
                    displaySpeechRecognizer();
                else
                    activityMainSearchText.setText(null);
                break;

            case R.id.nav_drawer_profile :
                break;

            case R.id.nav_drawer_collections :
                break;

            case R.id.nav_drawer_live_images :
                break;

            case R.id.nav_drawer_settings :
                break;
        }
    }

    /**
     * On Text Change
     *
     * @param text
     */
    @OnTextChanged(R.id.activity_main_search_text)
    protected void onTextChanged(CharSequence text) {
        if (text.toString().length() != 0) {
            activityMainVoiceSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_close));
            searchTextBoxEmpty = false;
        } else {
            activityMainVoiceSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_drawable_microphone));
            activityMainVoiceSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
        }
    }

    /**
     * On Touch Listener - Use to make sure only the visible layout is touchable not the other one
     *
     * @param v,event
     * @return true/false
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.activity_main_search_overflow:
                if (activityMainSearchOverflow.getVisibility() == View.VISIBLE) {
                    return false;
                } else {
                    return true;
                }
            case R.id.toolbar:
                if (toolbar.getVisibility() == View.VISIBLE) {
                    return false;
                } else {
                    return true;
                }
        }
        return false;
    }

    /**
     * Support Navigation Up
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * Back Pressed
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (activityMainSearchOverflow.getVisibility() == View.VISIBLE) {
            if (searchTextBoxEmpty)
                closeSearchBar();
            else
                activityMainSearchText.setText(null); //empty text
        } else {
            finish();
        }
    }

    /**
     * Setting Up ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LatestFragment(), "LATEST");
        adapter.addFragment(new TrendingFragment(), "TRENDING");
        adapter.addFragment(new CuratedFragment(), "CURATED");

        viewPager.setAdapter(adapter);
    }

    /**
     * Fragments Adapter
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

    /**
     * Closing Search Bar
     */
    private void closeSearchBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = activityMainSearchOverflow.getWidth();
            int cy = activityMainSearchOverflow.getHeight() / 2;
            float initialRadius = (float) Math.hypot(cx, cy);
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, initialRadius, 0);

            toolbar.setVisibility(View.VISIBLE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    activityMainSearchOverflow.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();
            tabLayout.setVisibility(View.VISIBLE);
            includeSearchLayoutExternal.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            includeSearchLayoutExternal.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Displaying Speech Recognizer
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 1);
    }

    /**
     * Navigation Drawer Initialize
     */
    public void initNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_content);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

}
