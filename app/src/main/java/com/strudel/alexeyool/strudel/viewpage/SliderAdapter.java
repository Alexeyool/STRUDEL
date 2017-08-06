package com.strudel.alexeyool.strudel.viewpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.strudel.alexeyool.strudel.R;

/**
 * Created by Alexeyool on 7/28/2017.
 */

public class SliderAdapter extends PagerAdapter {

    Context mContext;
    PdfRenderer pdfRenderer;
    Bitmap bitmap;

    SliderAdapter(Context _context, PdfRenderer _pdfRenderer){
        mContext = _context;
        pdfRenderer = _pdfRenderer;
    }

    @Override
    public int getCount() {
        return pdfRenderer.getPageCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }



    public Bitmap getImage(){
        return bitmap;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpage_item, container, false);

        PdfRenderer.Page currentPage = pdfRenderer.openPage(position);
        bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        ZoomableImageView touch = (ZoomableImageView) itemView.findViewById(R.id.VI_imageView);
        touch.setImageBitmap(bitmap);

        currentPage.close();

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
