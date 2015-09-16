package com.example.ab.rango.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by ab on 12/30/14.
 */
public class ItemViewPager extends ViewPager {

    private String LOG_TAG = "ItemViewPager";

    private float mDownX;
    private float mDownY;

    public ItemViewPager(Context context) {
        super(context);
    }

    public ItemViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Pass the touch screen motion event down to the target view, or
    // this view if it is the target.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(LOG_TAG, "dispatchTouchEvent: " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDownX) > Math.abs(ev.getY() - mDownY)) {

                    // Allowed to intercept events, deal with their own
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


}
