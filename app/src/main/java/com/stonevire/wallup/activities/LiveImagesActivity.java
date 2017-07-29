package com.stonevire.wallup.activities;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.stonevire.wallup.R;
import com.stonevire.wallup.services.LiveImagesService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LiveImagesActivity extends AppCompatActivity {

    @BindView(R.id.activity_live_images_chevron_right)
    AppCompatImageView activityLiveImagesChevronRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_images);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityLiveImagesChevronRight.setColorFilter(ContextCompat.getColor(this, R.color.black));
    }

    @OnClick(R.id.activity_live_images_set_wallpaper)
    public void onViewClicked() {
        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, LiveImagesService.class));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
