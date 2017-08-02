package com.strudel.alexeyool.strudel;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.strudel.alexeyool.strudel.viewpage.SliderActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


public class MainActivity extends AppCompatActivity {
    private static final String COVERS_DIRECTORY = "coversDir";
    public static final String MONTH = "month";
    public static final String YEAR = "year";

    Context mContext;

    ImageButton settingsActionBar;

    ListView list;
    ListAdapter listAdapter;
    ArrayList<Cover> coversArrayList;
    ProgressBar progressBar;

    DownloadCovers downloadCovers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        actionBarCreate();
        createProgressBar();
        createListView();
        downloadCoversToList();

    }

    private void downloadCoversToList() {
        downloadCovers = (DownloadCovers) getLastNonConfigurationInstance();
        if (downloadCovers == null) {
            downloadCovers = new DownloadCovers(mContext);
            downloadCovers.execute("");
        }
        downloadCovers.link(this);
    }

    private void createProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.AM_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void createListView() {
        list = (ListView) findViewById(R.id.AM_listview);
        list.setOnItemClickListener(onItemClickListener);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, SliderActivity.class);
            intent.putExtra(MONTH, coversArrayList.get(position).getMonth());
            intent.putExtra(YEAR, coversArrayList.get(position).getYear());
            startActivity(intent);
        }
    };

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


    private static class DownloadCovers extends AsyncTask<String, Void, ArrayList<Cover>> {
        Context mContext;
        MainActivity activity;

        public DownloadCovers(Context _mContext) {
            mContext = _mContext;
        }

        void link(MainActivity act) {
            activity = act;
        }

        void unLink() {
            activity = null;
        }


        protected ArrayList<Cover> doInBackground(String... params) {
            activity.coversArrayList = new ArrayList<>();
            updateCoversFilesInInternalStorageFromUrls();
            getImagesFromInternalStorageToArrayList();
            return upend(activity.coversArrayList);
        }

        protected void onPostExecute(ArrayList<Cover> result) {
            activity.listAdapter = new ListAdapter(mContext, result);
            activity.list.setAdapter(activity.listAdapter);
            activity.progressBar.setVisibility(View.INVISIBLE);
        }


        private void updateCoversFilesInInternalStorageFromUrls() {
            Log.e("myy", "updateCoversFilesInInternalStorageFromUrls");
            int i = 4;
            int j = 2017;
            while (i != 0) {
                Cover cover = new Cover(i , j);
                if(fileExistInURL(cover)) {
                    if (fileExist(cover.fileName)) {
                        if (i < 12) {
                            i++;
                        } else {
                            i = 1;
                            j++;
                        }
                        activity.coversArrayList.add(cover);
                    } else {
                        try {
                            InputStream inputStream = new java.net.URL(cover.getUrl()).openStream();

                            Bitmap temp = BitmapFactory.decodeStream(inputStream);
                            if (temp != null) {
                                activity.coversArrayList.add(cover);
                                saveToInternalStorage(cover, temp);
                                if (i < 12) {
                                    i++;
                                } else {
                                    i = 1;
                                    j++;
                                }
                            } else {
                                i = 0;
                            }
                        } catch (Exception e) {
                            Log.e("myy", e.getMessage());
                            i = 0;
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    if(fileExist(cover.fileName)){
                        deleteFile(cover.fileName);
                    }
                    i=0;
                }
            }
        }

        private boolean deleteFile(String _fileName){
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(COVERS_DIRECTORY, Context.MODE_PRIVATE);
            File file = new File(directory, _fileName);
            return file.delete();
        }

        private boolean fileExistInURL(Cover _cover) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =
                        (HttpURLConnection) new URL(_cover.getUrl()).openConnection();
                con.setRequestMethod("HEAD");
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private boolean fileExist(String fname){
            Log.e("myy", "fileExist");
        //    "/data/user/0/com.strudel.alexeyool.strudel/app_coversDir"
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(COVERS_DIRECTORY, Context.MODE_PRIVATE);
            File file = new File(directory, fname);
            return file.exists();
        }

        private String saveToInternalStorage(Cover cover, Bitmap bitmapImage){
            Log.e("myy", "saveToInternalStorage");
//            File sdPath = Environment.get;
//            sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
//            sdPath.mkdirs();

            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(COVERS_DIRECTORY, Context.MODE_PRIVATE);
            File mypath=new File(directory, cover.fileName);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(mypath);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return directory.getAbsolutePath();
        }

        private void getImagesFromInternalStorageToArrayList() {
            Log.e("myy", "getImagesFromInternalStorageToArrayList");
            int size = activity.coversArrayList.size();
            for(int i=0; i<size; i++){
                loadImageFromInternalStorage(activity.coversArrayList.get(i), i);
            }
        }

        private void loadImageFromInternalStorage(Cover cover, int position) {
            Log.e("myy", "loadImageFromInternalStorage" + position);
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(COVERS_DIRECTORY, Context.MODE_PRIVATE);
            try {
      //          "/data/user/0/com.strudel.alexeyool.strudel/app_coversDir"
                File file = new File(directory, cover.fileName);
                if(file.exists()) {
                    activity.coversArrayList.get(position).addImage(BitmapFactory.decodeStream(new FileInputStream(file)));
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        private ArrayList<Cover> upend(ArrayList<Cover> _temp){
            activity.coversArrayList = new ArrayList<>();
            for(int i=_temp.size()-1; i>-1; i--){
                activity.coversArrayList.add(_temp.get(i));
            }
            return activity.coversArrayList;
        }
    }
}


