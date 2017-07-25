package com.strudel.alexeyool.strudel;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity {
    Context mContext;

    ImageButton settingsActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        actionBarCreate();

    }

    private void actionBarCreate() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(false);

            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.action_bar_main, null);
            settingsActionBar = (ImageButton) mCustomView.findViewById(R.id.ABM_imageButton_menu);

            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(ContextCompat.getColor(mContext, R.color.white));
            mActionBar.setBackgroundDrawable(colorDrawable);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        popup.show();
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.MM_about:
                    Intent intentAbout = new Intent();
                    intentAbout.setClass(MainActivity.this, AboutActivity.class);
                    startActivity(intentAbout);
                    return true;
                case R.id.MM_Privacy_Policy:
                    Intent intentPrivacy = new Intent();
                    intentPrivacy.setClass(MainActivity.this, PrivacyActivity.class);
                    startActivity(intentPrivacy);
                    return true;
                default:
                    return false;
            }
        }
    };

}