package com.example.ab.rango.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.example.ab.rango.MainActivity;
import com.example.ab.rango.NewsFeedFragment;
import com.example.ab.rango.R;
import com.example.ab.rango.util.ImageCache;
import com.example.ab.rango.util.ImageResizer;
import com.example.ab.rango.util.ImageWorker;
import com.example.ab.rango.widget.IndicatorView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by ab on 12/30/14.
 */
public class MyPagerAdapter extends PagerAdapter {

    private static final String LOG_TAG = "MyPagerAdapter";

    private Context mContext;
    private Activity mActivity;
    private int mCounter = 4;
    Bitmap mPlaceHolderBitmap;



    private List<String> mPostPictures = null;

    public MyPagerAdapter() {
    }

    public MyPagerAdapter(Context context, int counter) {
        mContext = context;
        mCounter = counter;
    }

    public MyPagerAdapter(Activity context, int counter, List<String> postPictures) {
        mContext = context;
        mActivity = context;
        mCounter = counter;
        mPostPictures = postPictures;
        mPlaceHolderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_photo);

    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public int getCount() {
        return mCounter;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(View container, int position) {

        ImageView iv = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.viewpager_view, null);
        Log.d(LOG_TAG,"mPostPictures.get(0): "+mPostPictures.get(position));

        //final int index = position;

//        Button button1 = (Button)view.findViewById(R.id.button1);
//        button1.setOnClickListener( new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "position:" + index, Toast.LENGTH_SHORT).show();
//            }
//        });

        //int imageSize = IMAGES.length;

        //BitmapDrawable image = PictureUtils.getScaledDrawable(mActivity, mPostPictures.get(position));


        ((MainActivity) mContext).getImageFetcher().loadImage(mPostPictures.get(position), iv);

        //loadBitmap(mPostPictures.get(position), mActivity, iv);

        //iv.setImageDrawable(image);

        ((ViewPager) container).addView(iv);

        return iv;
    }

//    public void loadBitmap(String path, Activity a, ImageView iv) {
//        BitmapWorkerTask task = new BitmapWorkerTask(a, iv);
//        task.execute(path);
//    }

    // Before executing the BitmapWorkerTask, you create an AsyncDrawable
    // and bind it to the target ImageView
    //
    // BitmapWorkerTask.cancelPotentialWork()
    // checks if another running task is already associated with
    // the ImageView. If so, it attempts to cancel the previous
    // task by calling cancel(). In a small number of cases, the
    // new task data matches the existing task and nothing further
    // needs to happen.
    //
    // When loading a bitmap into an ImageView, the LruCache is
    // checked first. If an entry is found, it is used immediately
    // to update the ImageView, otherwise a background thread is
    // spawned to process the image:
//    public void loadBitmap(String path, Activity a, ImageView iv) {
//        final BitmapWorkerTask task = new BitmapWorkerTask(a, iv);
//        //final ImageCache ic = task.ic;
//        final Bitmap bitmap = ImageCache.getInstance(((MainActivity) mContext)
//                .getSupportFragmentManager()).getBitmapFromMemCache(path);
//
//        if (bitmap != null) {
//           Log.d(LOG_TAG,"from Cache: "+ path);
//           iv.setImageBitmap(bitmap);
//        } else if (BitmapWorkerTask.cancelPotentialWork(path, iv)) {
//
//            final BitmapWorkerTask.AsyncDrawable asyncDrawable =
//                    new BitmapWorkerTask.AsyncDrawable(a.getResources(), mPlaceHolderBitmap, task);
//            iv.setImageDrawable(asyncDrawable);
//            task.execute(path);
//        }
//    }



    /**
     * A OnPageChangeListener used to update the ShareActionProvider's share intent when a new item
     * is selected in the ViewPager.
     */
    public static class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        IndicatorView mIndicatorView = null;

        public MyPagerChangeListener( IndicatorView indicatorView) {
            mIndicatorView = indicatorView;
        }

        @Override
        public void onPageSelected(int position) {
            //setShareIntent(position);
            //Toast.makeText(context, "position=" + position, 1).show();
            Log.d("MyPagerChangeListener", "position=" + position);
            mIndicatorView.setSelectIndex(position);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // NO-OP
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // NO-OP
        }
    }
}
