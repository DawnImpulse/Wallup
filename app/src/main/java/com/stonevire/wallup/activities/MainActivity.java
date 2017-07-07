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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.stonevire.wallup.R;
import com.stonevire.wallup.fragments.NewImagesFragment;
import com.stonevire.wallup.network.volley.VolleyWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity{
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

    private SectionsPagerAdapter mSectionsPagerAdapter;
    VolleyWrapper mVolleyWrapper;
    Map<String, String> params;
    boolean searchTextBoxEmpty = true; // to check if edit text is currently empty or not

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Fresco.initialize(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVolleyWrapper = new VolleyWrapper(this);
        params = new HashMap<>();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        activityMainVoiceSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        /*searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(this);*/

    }

    /**
     * Menu Options Create
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
     * @param item
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        if (id == R.id.search) {

            int cx = activityMainSearchOverflow.getWidth();
            int cy = activityMainSearchOverflow.getHeight() / 2;
            float initialRadius = (float) Math.hypot(cx, cy);
            Animator anim = ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, 0, initialRadius);

            activityMainSearchOverflow.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            anim.start();
            includeSearchLayoutExternal.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.INVISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On Activity Result
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
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.activity_main_search_close, R.id.activity_main_voice_search})
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
        }
    }

    /**
     * On Text Change
     * @param text
     */
    @OnTextChanged(R.id.activity_main_search_text)
    protected void onTextChanged(CharSequence text) {
        if (text.toString().length()!=0)
        {
            activityMainVoiceSearch.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.vector_drawable_close));
            searchTextBoxEmpty = false;
        }else
        {
            activityMainVoiceSearch.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.vector_drawable_microphone));
            activityMainVoiceSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
        }
    }
    // Dummy PlaceHolder
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("hello");
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * Setting Up ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewImagesFragment(), "IMAGES");
        adapter.addFragment(PlaceholderFragment.newInstance(1), "CURRENT");
        adapter.addFragment(PlaceholderFragment.newInstance(2), "FUTURE");

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
     * Support Navigation Up
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
     * Closing Search Bar
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeSearchBar() {
        int cx = activityMainSearchOverflow.getWidth();
        int cy = activityMainSearchOverflow.getHeight() / 2;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim =
                ViewAnimationUtils.createCircularReveal(activityMainSearchOverflow, cx, cy, initialRadius, 0);
        toolbar.setVisibility(View.VISIBLE);
        anim.start();
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
        tabLayout.setVisibility(View.VISIBLE);
        includeSearchLayoutExternal.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }

    /**
     * Displaying Speech Recognizer
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, 1);
    }
}
