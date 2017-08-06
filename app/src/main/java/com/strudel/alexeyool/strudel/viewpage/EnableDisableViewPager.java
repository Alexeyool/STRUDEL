package com.strudel.alexeyool.strudel.viewpage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Alexeyool on 8/1/2017.
 */

public class EnableDisableViewPager extends ViewPager {

    public EnableDisableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(ZoomableImageView.saveScale == 1f){
            return true && super.onInterceptTouchEvent(event);
        }
        else {
            return false && super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}