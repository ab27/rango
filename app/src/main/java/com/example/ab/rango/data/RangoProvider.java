package com.example.ab.rango.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by ab on 1/13/15.
 */
public class RangoProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher rUriMatcher = buildUriMatcher();
    private RangoDbHelper mOpenHelper;

    // Uri types
    // each one of these uris are used for different type of queries
    private static final int ITEM = 300;
    private static final int ITEM_ID = 301;

    // Uri matcher
    // uses a simple expression syntax to help us match URI's for a
    // ContentProvider
    // Examples:
    // "path" - match path exactly
    // "path/#" - matches "path" followed by a number
    // "path/*" - matches "path" followed by any string
    // "path/*/other/#" - matches "path" followed by a string
    //    followed by "other" followed by a number
    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when
        // you can use regular expressions instead?  Because you're
        // not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the root URI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RangoContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding
        // code.
        matcher.addURI(authority, RangoContract.PATH_ITEMS, ITEM);
        matcher.addURI(authority, RangoContract.PATH_ITEMS + "/#", ITEM_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new RangoDbHelper(getContext());
        // return true to tell android that the content provider has been
        // created successfully
        return true;
    }

    // Parameters
    // uri	The URI to query. This will be the full URI sent by the client;
    //      if the client is requesting a specific record, the URI will end
    //      in a record number that the implementation should parse and add
    //      to a WHERE or HAVING clause, specifying that _id value.

    // projection	The list of columns to put into the cursor. If null all
    //              columns are included.

    // selection	A selection criteria to apply when filtering rows. If
    //              null then all rows are included.

    // selectionArgs	You may include ?s in selection, which will be
    //                  replaced by the values from selectionArgs, in order
    //                  that they appear in the selection. The values will
    //                  be bound as Strings.

    // sortOrder	How the rows in the cursor should be sorted. If null
    //              then the provider is free to define the sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine
        // what kind of request it is, and query the database accordingly.

        Cursor retCursor;
        switch (rUriMatcher.match(uri)) {
            // "item/*"
            case ITEM_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RangoContract.ItemsEntry.TABLE_NAME,
                        projection,
                        RangoContract.ItemsEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "item"
            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RangoContract.ItemsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    // Return the MIME type corresponding to a content URI.
    @Override
    public String getType(Uri uri) {
        final int match = rUriMatcher.match(uri);

        switch(match) {
            case ITEM:
                return RangoContract.ItemsEntry.CONTENT_TYPE; // a single item
            case ITEM_ID:
                return RangoContract.ItemsEntry.CONTENT_ITEM_TYPE; // a directory (list) of items
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ITEM: {
                long _id = db.insert(RangoContract.ItemsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = RangoContract.ItemsEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case ITEM: {
                rowsDeleted = db.delete(
                        RangoContract.ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ITEM: {
                rowsUpdated = db.update(
                        RangoContract.ItemsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Override this to handle requests to insert a set of new rows, or the
     * default implementation will iterate over the values and call
     * {@link #insert} on each of them.
     * As a courtesy, call ContentResolver#notifyChange(android.net.Uri,
     *   android.database.ContentObserver) notifyChange()
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = rUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RangoContract.ItemsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
