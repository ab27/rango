package com.example.ab.rango;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.ab.rango.data.RangoContract.ItemsEntry;
import com.example.ab.rango.data.RangoDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by ab on 1/13/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // the test runner will execute every function in our class that
    // begins with test in the order that they are declared in the class
    //
    // each test should have a failure path that uses an assert

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createItemValues();

        // ItemsEntry.CONTENT_URI:  content://com.example.ab.rango/items
        Uri locationUri = mContext.getContentResolver().insert(ItemsEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at
        // it and verify it made the round trip.

        Log.d(LOG_TAG, "ItemsEntry.CONTENT_URI: " + ItemsEntry.CONTENT_URI);
        // ItemsEntry.CONTENT_URI:  content://com.example.ab.rango/items
        Cursor cursor = mContext.getContentResolver().query(
                ItemsEntry.CONTENT_URI,
                null,   // leaving "columns" null just returns all the columns.
                null,   // cols for "where" clause
                null,   // values for "where" clause
                null);  // sort order

        TestDb.validateCursor(cursor, testValues);

        Log.d(LOG_TAG,"buildLocationUri: "+ItemsEntry.buildLocationUri(locationRowId));
        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                // // content://com.example.ab.rango/items/1
                ItemsEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);
    }

    public void testGetType() {
        String type;

        // content://com.example.ab.rango/item/
        type = mContext.getContentResolver().getType(ItemsEntry.CONTENT_URI);
        Log.d(LOG_TAG,type);
        Log.d(LOG_TAG,ItemsEntry.CONTENT_TYPE);
        // vnd.android.cursor.dir/com.example.ab.rango/location
        assertEquals(ItemsEntry.CONTENT_TYPE, type);

        // content://com.example.ab.rango/item/1
        type = mContext.getContentResolver().getType(ItemsEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.ab.rango/location
        assertEquals(ItemsEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdateItem() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createItemValues();

        Uri itemUri = mContext.getContentResolver().
                insert(ItemsEntry.CONTENT_URI, values);
        long itemRowId = ContentUris.parseId(itemUri);

        // Verify we got a row back.
        assertTrue(itemRowId != -1);
        Log.d(LOG_TAG, "New row id: " + itemRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(ItemsEntry._ID, itemRowId);
        updatedValues.put(ItemsEntry.COLUMN_ITEM_NAME, "android");

        int count = mContext.getContentResolver().update(
                ItemsEntry.CONTENT_URI, updatedValues, ItemsEntry._ID + "= ?",
                new String[] { Long.toString(itemRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ItemsEntry.buildLocationUri(itemRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);

    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecords() {
        deleteAllRecords();
    }

    // brings our database to an empty state
    // called by setup
    public void deleteAllRecords() {
        // delete the records
        mContext.getContentResolver().delete(
                ItemsEntry.CONTENT_URI,
                null,
                null
        );

        // check if deleted
        Cursor cursor = mContext.getContentResolver().query(
                ItemsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
}
