package com.example.ab.rango;

/**
 * Created by ab on 12/28/14.
 */

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ab.rango.adapter.RangoAdapter;
import com.example.ab.rango.data.RangoContract;
import com.example.ab.rango.util.ImageCache;
import com.example.ab.rango.util.ImageFetcher;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {



    private RangoAdapter mRangoAdapter;
    private String mItem;
    // each loader has an id, it allows a fragment to have multiple
    // loaders active at once.
    private static final int ITEM_LOADER = 0;

    // Specify the columns we need.
    private static final String[] ITEM_COLUMNS = {
            //RangoContract.ItemsEntry.TABLE_NAME,
            RangoContract.ItemsEntry._ID,
            RangoContract.ItemsEntry.COLUMN_POST_DATE,
            RangoContract.ItemsEntry.COLUMN_POSTER_NAME,
            RangoContract.ItemsEntry.COLUMN_ITEM_DESCRIPTION,
            RangoContract.ItemsEntry.COLUMN_ITEM_PRICE,
            RangoContract.ItemsEntry.COLUMN_ITEM_NAME,
            RangoContract.ItemsEntry.COLUMN_ITEM_PICTURES
    };

    // These indices are tied to ITEM_COLUMNS above
    public static final int COL_ITEM_ID = 0;
    public static final int COL_POST_DATE = 1;
    public static final int COL_POSTER_NAME = 2;
    public static final int COL_ITEM_DESCRIPTION = 3;
    public static final int COL_ITEM_PRICE = 4;
    public static final int COL_ITEM_NAME = 5;
    public static final int COL_ITEM_PICTURES = 6;

    public NewsFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        // create our own custom adapter. unlike with the simple cursor adapter,
        // there is no need to define which database column it will be mapping or
        // accessing. that`s all handled in the adapters implementation
        mRangoAdapter = new RangoAdapter(getActivity(), null, 0);

        ListView mListView = (ListView) view.findViewById(R.id.newsFeedListView);
        mListView.setAdapter(mRangoAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loaders are initialized in onActivityCreated because their life
        // cycle is actually bound to the activity. Not the fragment.
        getLoaderManager().initLoader(ITEM_LOADER, null, this);


    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Sort order:  Ascending, by date.
        String sortOrder = RangoContract.ItemsEntry._ID + " DESC";

        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        // CursorLoader(Context context, Uri uri, String[] projection,
        //   String selection, String[] selectionArgs, String sortOrder)
        // Creates a fully-specified CursorLoader.
        return new CursorLoader(
                getActivity(),
                RangoContract.ItemsEntry.CONTENT_URI,
                ITEM_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See  FragmentManager#beginTransaction()
     * FragmentManager.openTransaction() for further discussion on this.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link android.database.Cursor}
     * and you place it in a {@link android.widget.CursorAdapter}, use
     * the {@link android.widget.CursorAdapter#CursorAdapter(android.content.Context,
     * android.database.Cursor, int)} constructor <em>without</em> passing
     * in either {@link android.widget.CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link android.database.Cursor} from a {@link android.content.CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link android.widget.CursorAdapter}, you should use the
     * {@link android.widget.CursorAdapter#swapCursor(android.database.Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRangoAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRangoAdapter.swapCursor(null);
    }
}
