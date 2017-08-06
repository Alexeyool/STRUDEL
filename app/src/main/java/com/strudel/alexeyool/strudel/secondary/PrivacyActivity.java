package com.strudel.alexeyool.strudel.secondary;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.strudel.alexeyool.strudel.R;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        actionBarCreate();
    }

    private void actionBarCreate() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.action_bar_main, null);
            ImageButton settingsActionBar = (ImageButton) mCustomView.findViewById(R.id.ABM_imageButton_menu);
            settingsActionBar.setVisibility(View.INVISIBLE);

            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(ContextCompat.getColor(this, R.color.white));
            mActionBar.setBackgroundDrawable(colorDrawable);

            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

}
