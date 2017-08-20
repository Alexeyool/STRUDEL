package com.strudel.alexeyool.strudel.viewpage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.strudel.alexeyool.strudel.BuildConfig;
import com.strudel.alexeyool.strudel.main.MainActivity;
import com.strudel.alexeyool.strudel.R;

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

public class SliderActivity extends AppCompatActivity {
    private static final String PDF_DIRECTORY_NAME = "pdf_directory";
    private static final String URL_PDF = "http://shtrudel.pro-depot.co.il/strudel_pdf/";
    private static final String SHARE_DIRECTORY = "share_directory";
    private static final String SHARE_FILE = "share_file.jpg";
    private static final String PROVIDER = "com.strudel.alexeyool.provider";
    Context mContext;

    EnableDisableViewPager viewPager;
    SliderAdapter viewPagerAdapter;

    ArrayList<Bitmap> imagesArray;
    DownloadImages downloadImages;
    PdfRenderer pdfRenderer;

    ImageButton imageButtonShare;
    ImageButton imageButtonPages;

    ProgressBar progressBar;

    String fileNamePdf;
    String fileNameJpg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        mContext = this;

        progressBarCreate();
        actionBarCreate();

        getFileNameFromExtra();

        createViewPager();
        downloadImagesToViewPager();

    }

    private void progressBarCreate() {
        progressBar = (ProgressBar) findViewById(R.id.AS_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getFileNameFromExtra() {
        Intent intent = getIntent();
        intent.getIntExtra(MainActivity.MONTH, 0);
        intent.getIntExtra(MainActivity.YEAR, 0);
        fileNamePdf = "" + intent.getIntExtra(MainActivity.MONTH, 0) + "_" + intent.getIntExtra(MainActivity.YEAR, 0) + ".pdf";
        fileNameJpg = "" + intent.getIntExtra(MainActivity.MONTH, 0) + "_" + intent.getIntExtra(MainActivity.YEAR, 0) + ".jpg";
    }

    private void downloadImagesToViewPager() {
        downloadImages = (DownloadImages) getLastNonConfigurationInstance();
        if (downloadImages == null) {
            downloadImages = new DownloadImages(mContext);
            downloadImages.execute("");
        }
        downloadImages.link(this);
    }

    private void createViewPager() {
        viewPager = (EnableDisableViewPager) findViewById(R.id.AS_viewPager);
    }

    private void actionBarCreate() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(false);

            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.action_bar_pageview, null);

            imageButtonShare = (ImageButton) mCustomView.findViewById(R.id.ABP_imageButton_share);
            imageButtonShare.setOnClickListener(onClickListener);
            imageButtonShare.setClickable(false);

            imageButtonPages = (ImageButton) mCustomView.findViewById(R.id.ABP_imageButton_pages);
            imageButtonPages.setClickable(false);

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
        inflater.inflate(R.menu.menu_pages, popup.getMenu());
        for(int i=0; i<pdfRenderer.getPageCount(); i++) {
            popup.getMenu().add(Menu.NONE, i, i, "страница" + (i+1));
        }
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        popup.show();
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            viewPager.setCurrentItem(item.getOrder());
            return true;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String fileName = SHARE_FILE;
            File dir = mContext.getFilesDir();
            File file = new File(dir, fileName );
            if(file.delete()){
                fileName = "a_" + SHARE_FILE;
                file = new File(dir, fileName);
            }
            else{
                fileName = "a_" + SHARE_FILE;
                file = new File(dir, fileName);
                file.delete();
                fileName = SHARE_FILE;
                file = new File(dir, fileName);
            }

            FileOutputStream fileOutputStream = null;

            try {

                PdfRenderer.Page currentPage = pdfRenderer.openPage(viewPager.getCurrentItem());
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);

                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                currentPage.close();
                bitmap = setWhiteBackgraundToBitmap(bitmap);

                fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);//new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            file.setReadable(true, false);
            Uri uri;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uri =FileProvider.getUriForFile(mContext.getApplicationContext(),
                        PROVIDER, file);
            }
            else {
                uri = Uri.fromFile(file);
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Set as:"));
        }
    };


    private Bitmap setWhiteBackgraundToBitmap(Bitmap _bitmap){
        int[] pixels = new int[_bitmap.getHeight() * _bitmap.getWidth()];
        _bitmap.getPixels(pixels, 0, _bitmap.getWidth(), 0, 0, _bitmap.getWidth(), _bitmap.getHeight());
        for (int i=0; i<pixels.length; i++) {
            if(pixels[i] == Color.TRANSPARENT) {
                pixels[i] = Color.WHITE;
            }
            else{
                int red = Color.red(pixels[i]);
                int green = Color.green(pixels[i]);
                int blue = Color.blue(pixels[i]);
                int alpha = Color.alpha(pixels[i]);//
                if(alpha > 0 && alpha < 255){
                    pixels[i] = Color.argb(255, calculateColor(red, alpha), calculateColor(green, alpha), calculateColor(blue, alpha));
                }
            }
        }
        _bitmap.setPixels(pixels, 0, _bitmap.getWidth(), 0, 0, _bitmap.getWidth(), _bitmap.getHeight());
        return _bitmap;
    }

    private int calculateColor(int _color, int alpha) {
        int t = (int) (255.0 - ((255.0 - (double)_color) / (255.0/(double)alpha)));
        return t;
    }

    private void stopDownloadImages(){
        downloadImages = (DownloadImages) getLastNonConfigurationInstance();
        if(downloadImages != null) {
            downloadImages.unLink();
            downloadImages.cancel(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        stopDownloadImages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownloadImages();
    }

    static class DownloadImages extends AsyncTask<String, Void, PdfRenderer> {
        Context mContext;
        SliderActivity activity;

        public DownloadImages(Context _mContext) {
            mContext = _mContext;
        }

        void link(SliderActivity act) {
            activity = act;
        }

        void unLink() {
            activity = null;
        }


        protected PdfRenderer doInBackground(String... params) {
            activity.imagesArray = new ArrayList<>();
            if(fileExistInURL(activity.fileNamePdf)) {
                if (fileExist(activity.fileNamePdf)) {
                    loadImageArrayFromInternalStorage(activity.fileNamePdf);
                } else {
                    saveFilePdfToInternalStorageFromUrl();
                    loadImageArrayFromInternalStorage(activity.fileNamePdf);
                }

                return activity.pdfRenderer;
            }
            else{
                if(fileExist(activity.fileNamePdf)){
                    deleteFile(activity.fileNamePdf);
                }
            }
            return null;
        }

        protected void onPostExecute(PdfRenderer result) {
            if(result != null) {
                activity.viewPagerAdapter = new SliderAdapter(activity.mContext, result);
                activity.viewPager.setAdapter(activity.viewPagerAdapter);
                activity.imageButtonShare.setClickable(true);
                activity.imageButtonPages.setClickable(true);
                activity.progressBar.setVisibility(View.INVISIBLE);
                }
            else activity.finish();
        }

        private boolean deleteFile(String _fileName){
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
            File file = new File(directory, _fileName);
            return file.delete();
        }

        private boolean fileExistInURL(String _fileName) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =
                        (HttpURLConnection) new URL(activity.URL_PDF + _fileName).openConnection();
                con.setRequestMethod("HEAD");
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private boolean fileExist(String _fileName){
            Log.e("myyS", "fileExist");
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
            File file = new File(directory, _fileName);
            return file.exists();
        }

        private void loadImageArrayFromInternalStorage(String _fileName) {
            Log.e("myyS", "loadImageArrayFromInternalStorage");
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
            try {
                File f=new File(directory, _fileName);
                ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
                activity.pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            public void saveFilePdfToInternalStorageFromUrl() {
                Log.e("myyS", "saveFilePdfToInternalStorageFromUrl");
                try {
                    ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
                    File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
                    int s = countFiles();
                    if(s > 2){
                        deleteOlderFile();
                    }
                    File file = new File(directory, activity.fileNamePdf);
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    URL url = new URL(URL_PDF + activity.fileNamePdf);
                    URLConnection ucon = url.openConnection();

                    InputStream in = ucon.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = in.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, len1);
                        Log.e("myyS", ""+len1);
                    }
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            private int countFiles(){
                ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
                File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
                String[] st = directory.list();
                int s = st.length;
                return s;
            }

            private void deleteOlderFile(){
                ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
                File directory = cw.getDir(PDF_DIRECTORY_NAME, Context.MODE_PRIVATE);
                String[] list = directory.list();
                File file = new File(directory, list[0]);
                long temp = file.lastModified();
                int j=0;
                for(int i=1; i<list.length; i++){
                    file = new File(directory, list[i]);
                    if(temp > file.lastModified()) {
                        temp = file.lastModified();
                        j=i;
                    }
                }
                file = new File(directory, list[j]);
                file.delete();
            }
    }
}
