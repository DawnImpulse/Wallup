package com.stonevire.wallup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stonevire.wallup.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashScreen extends AppCompatActivity {

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
    }

    /**
     * On Click
     */
    @OnClick(R.id.button)
    public void onViewClicked() {
        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(intent);
    }
}
