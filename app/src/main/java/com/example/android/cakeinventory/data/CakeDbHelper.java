package com.example.android.cakeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.cakeinventory.data.CakeContract.CakeEntry;

/**
 * Database helper for cakeinventory app. Manages database creation and version management.
 */
public class CakeDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CakeDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "cakeinventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link CakeDbHelper}.
     *
     * @param context of the app
     */
    public CakeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the cakes table
        String SQL_CREATE_CAKES_TABLE = "CREATE TABLE " + CakeEntry.TABLE_NAME + " ("
                + CakeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CakeEntry.COLUMN_CAKE_NAME + " TEXT NOT NULL, "
                + CakeEntry.COLUMN_CAKE_SHAPE + " INTEGER NOT NULL, "
                + CakeEntry.COLUMN_CAKE_PRICE + " INTEGER NOT NULL, "
                + CakeEntry.COLUMN_CAKE_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + CakeEntry.COLUMN_CAKE_SUPPLIER + " TEXT , "
                + CakeEntry.COLUMN_CAKE_PHONE + " INTEGER , "
                + CakeEntry.COLUMN_CAKE_DESCRIPTION + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CAKES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}