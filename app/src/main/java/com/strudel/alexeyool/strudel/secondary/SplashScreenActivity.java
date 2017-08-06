package com.strudel.alexeyool.strudel.secondary;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.strudel.alexeyool.strudel.R;
import com.strudel.alexeyool.strudel.main.MainActivity;


public class SplashScreenActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splash_screen);

        hideActionBar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },DELAY_MILLIS);
//        Intent intent = new Intent();
//                intent.setClass(SplashScreenActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();

    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}
