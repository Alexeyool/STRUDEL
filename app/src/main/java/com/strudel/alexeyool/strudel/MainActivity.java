package com.strudel.alexeyool.strudel;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Context mContext;

    ImageButton settingsActionBar;

    public static ListView list;
    public static ListAdapter listAdapter;
    public static ArrayList<Bitmap> coversArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        actionBarCreate();

        downloadCoversToList();
        createListView();


    }

    private void downloadCoversToList() {
        createListView();
        ArrayList<String> coversUrls = getCoversUrls();
        new DownloadCovers(mContext, coversUrls).execute("");
    }

    private void createListView() {
        list = (ListView) findViewById(R.id.AM_listview);
        list.setOnItemClickListener(onItemClickListener);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    private ArrayList<String> getCoversUrls() {
        ArrayList<String> conversUrlArray = new ArrayList<String>();
        return null;
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



