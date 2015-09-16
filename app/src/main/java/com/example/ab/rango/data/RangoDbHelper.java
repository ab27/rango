package com.example.ab.rango.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ab.rango.data.RangoContract.ItemsEntry;
/**
 * Created by ab on 1/13/15.
 */
public class RangoDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "rango.db";

    public RangoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemsEntry.TABLE_NAME + " (" +
                ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemsEntry.COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL, " +
                ItemsEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                ItemsEntry.COLUMN_POSTER_NAME + " TEXT NOT NULL, " +
                ItemsEntry.COLUMN_ITEM_PICTURES + " TEXT NOT NULL, " +
                ItemsEntry.COLUMN_ITEM_PRICE + " REAL NOT NULL, " +
                ItemsEntry.COLUMN_POST_DATE + " TEXT NOT NULL" + " );";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    // called if you changed the version of your database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade
        // policy is to simply to discard the data and start over Note
        // that this only fires if you change the version number for your
        // database. It does NOT depend on the version number for your
        // application. If you want to update the schema without wiping
        // data, commenting out the next line should be your top priority
        // before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + ItemsEntry.TABLE_NAME);
        onCreate(db);
    }
}
