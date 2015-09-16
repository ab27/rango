package com.example.ab.rango.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ab.rango.NewsFeedFragment;
import com.example.ab.rango.R;
import com.example.ab.rango.util.Utils;
import com.example.ab.rango.widget.IndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ab on 12/30/14.
 */
public class RangoAdapter extends CursorAdapter {

    protected Context mContext;
    protected Activity mActivity;
    protected ArrayList<String> mDataList;
    private static final String LOG_TAG = "RangoAdapter";

    private static final int VIEW_TYPE_COUNT = 1;
    private static final int VIEW_TYPE_VIEWPAGER = 0;  // for the viewpager
    private static final int VIEW_TYPE_TEXT = 1;  // for the text under the viewpager

//    public RangoAdapter(Context context, ArrayList<String> list) {
//        //super(context, list);
//        this.mContext = context;
//        this.mDataList = list == null ? new ArrayList<String>() : new ArrayList<String>(list);
//        Log.d(TAG,mDataList.toString());
//    }

//    public RangoAdapter(Context context, ArrayList<String> list, int viewTypeCount) {
//        //super(context, list, viewTypeCount);
//        this(context, list);
//
//        //TYPE_COUNTER = viewTypeCount;
//
//    }

    public RangoAdapter(Activity context, Cursor c, int flags) {
        super(context, c, flags);
        mActivity = context;
        Log.d(LOG_TAG,"from constructor: ");
    }

//    public class ViewHolder {
//        private SparseArray<View> views = new SparseArray<View>();
//        private View convertView;
//
//        public ViewHolder(View convertView) {
//            this.convertView = convertView;
//        }
//
//        @SuppressWarnings({ "hiding", "unchecked" })
//        public <T extends View> T getView(int resId) {
//            View v = views.get(resId);
//            if (null == v) {
//                v = convertView.findViewById(resId);
//                views.put(resId, v);
//            }
//            return (T) v;
//        }
//    }

    /**
     * Cache of the children views for a forecast list item.
     * can be named what ever you want
     */
    public static class ViewHolder {
        public final TextView dateView;
        public final TextView priceView;
        public final TextView descriptionView;
        public final TextView itemNameView;
        public final ViewPager vpView;
        public final IndicatorView indicatorView;
        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.item_post_date);
            priceView = (TextView) view.findViewById(R.id.item_price);
            descriptionView = (TextView) view.findViewById(R.id.item_description);
            itemNameView = (TextView) view.findViewById(R.id.item_name);
            vpView = (ViewPager) view.findViewById(R.id.viewpager);
            indicatorView = (IndicatorView) view.findViewById(R.id.indicator);
        }
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.d(LOG_TAG,"from newView parent: "+R.id.newsFeedListView + " "+parent.getId());

        View view = LayoutInflater.from(context).inflate(R.layout.item_newsfeed, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        // the tag can be used to store any object on the view dont abuse it
        // because when you read it back you have to know what you`ve stored in there
        // to read from the tag
        // ViewHolder viewHolder = (ViewHolder) view.getTag();
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Log.d(LOG_TAG,"from bindView: "+cursor.toString());

        ViewPager viewPager = viewHolder.vpView;

        //Log.d(LOG_TAG,"string to array: "+ Utility.convertStringToArray(conv2str));

        //Activity context, int counter, ArrayList<String> postPictures
        String picStr = cursor.getString(NewsFeedFragment.COL_ITEM_PICTURES);
        List<String> picList = Utils.convertStringToArray(picStr);

        Log.d(LOG_TAG,"cursor picStr: "+ picStr);
        Log.d(LOG_TAG,"cursor picList: "+ picList);

        MyPagerAdapter pageAdapter = new MyPagerAdapter(mActivity,
                picList.size(),picList);
        viewPager.setAdapter(pageAdapter);

        IndicatorView indicatorView = (IndicatorView) viewHolder.indicatorView;
        //int size = cursor.getCount();

        indicatorView.setUpView(picList.size());
        indicatorView.setSelectIndex(0);


        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(
                new MyPagerAdapter.MyPagerChangeListener(indicatorView));

        // Read item description from cursor
        //String description = cursor.getString(NewsFeedFragment.COL_ITEM_DESCRIPTION);
        // Find TextView and set item description on it
        //viewHolder.descriptionView.setText(description);

        String name = cursor.getString(NewsFeedFragment.COL_ITEM_NAME);
        viewHolder.itemNameView.setText(name);

        String price = cursor.getString(NewsFeedFragment.COL_ITEM_PRICE);
        viewHolder.priceView.setText("$ "+price);

    }

//    // Get the data item associated with the specified position in the data set.
//    @Override
//    public Object getItem(int position) {
//        if (position >= mDataList.size())
//            return null;
//        return mDataList.get(position);
//    }

//    // Get the row id associated with the specified position in the list.
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    // How many items are in the data set represented by this Adapter.
//    @Override
//    public int getCount() {
//        return mDataList!=null ? mDataList.size() : 0;
//    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 2 == 0) {
            return VIEW_TYPE_VIEWPAGER;
        } else {
            return VIEW_TYPE_TEXT;
        }
    }

//    //@SuppressWarnings("unchecked")
//    // Get a View that displays the data at the specified position in the data set.
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        int type = getItemViewType(position);
//        ViewHolder holder0 = null;
//        ViewHolder holder1 = null;
//
//        switch (type) {
//            case VIEW_TYPE_VIEWPAGER: {
//
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(mContext)
//                            .inflate(getItemResourceId(type), parent, false);
//                    holder0 = new ViewHolder(convertView);
//                    convertView.setTag(holder0);
//                } else {
//                    holder0 = (ViewHolder) convertView.getTag();
//                }
//
//                return getItemView(position, convertView, holder0, type);
//            }
//            case VIEW_TYPE_TEXT:
//            default: {
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(mContext)
//                            .inflate(getItemResourceId(type), parent, false);
//                    holder1 = new ViewHolder(convertView);
//                    convertView.setTag(holder1);
//                } else {
//                    holder1 = (ViewHolder) convertView.getTag();
//                }
//
//                return getItemView(position, convertView, holder1, type);
//            }
//        }
//    }

//    public int getItemResourceId(int type) {
//        if (type == VIEW_TYPE_VIEWPAGER) {
//            return R.layout.item_newsfeed;
//        } else {
//            return R.layout.item_common;
//        }
//    }

//    // called by getView()
//    public View getItemView(int position, View convertView, ViewHolder holder, int type) {
//
//        String item = mDataList.get(position);
//
//        switch (type) {
//            case VIEW_TYPE_VIEWPAGER: {
//                ViewPager viewPager = (ViewPager) holder.getView(R.id.viewpager);
//                IndicatorView indicatorView = (IndicatorView) holder.getView(R.id.indicator);
//
//                int size = position + 1;
//                if (size > 10)
//                    size = 10;
//
//                indicatorView.setUpView(size);
//                indicatorView.setSelectIndex(0);
//
////                MyPagerAdapter pageAdaper = new MyPagerAdapter(mContext, size);
////                viewPager.setAdapter(pageAdaper);
////
////                viewPager.setCurrentItem(0);
////                viewPager.setOnPageChangeListener(
////                        new MyPagerAdapter.MyPagerChangeListener(indicatorView));
//                break;
//            }
//            case VIEW_TYPE_TEXT: {
//                TextView textView = (TextView) holder.getView(R.id.textView1);
//			    textView.setText(item);
//
//                break;
//            }
//        }
//
//        return convertView;
//    }


}
