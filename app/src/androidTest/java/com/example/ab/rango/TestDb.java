package com.example.ab.rango;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.ab.rango.data.RangoContract.ItemsEntry;
import com.example.ab.rango.data.RangoDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by ab on 1/13/15.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // the test runner will execute every function in our class that
    // begins with test in the order that they are declared in the class
    //
    // each test should have a failure path that uses an assert
    public void testCreateDb() throws Throwable {
        // start by deleting the database before testing it
        mContext.deleteDatabase(RangoDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new RangoDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        RangoDbHelper dbHelper = new RangoDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createItemValues();

        long locationRowId;
        locationRowId = db.insert(ItemsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at
        // it and verify it made the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ItemsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        dbHelper.close();
    }

    static ContentValues createItemValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(ItemsEntry.COLUMN_ITEM_DESCRIPTION, "iphone 5s good condition");
        testValues.put(ItemsEntry.COLUMN_ITEM_NAME, "iphone");
        testValues.put(ItemsEntry.COLUMN_ITEM_PICTURES, "IMG.png");
        testValues.put(ItemsEntry.COLUMN_ITEM_PRICE, 150);
        testValues.put(ItemsEntry.COLUMN_POSTER_NAME, "ab");
        testValues.put(ItemsEntry.COLUMN_POST_DATE, "1/5/15");

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
            Log.d(LOG_TAG,columnName + ": " +valueCursor.getString(idx));
        }
        valueCursor.close();
    }

}
