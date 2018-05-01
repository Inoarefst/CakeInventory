package com.example.android.cakeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.cakeinventory.data.CakeContract.CakeEntry;
/**
 * {@link ContentProvider} for cake inventory app.
 */
public class CakeProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = CakeProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the cakes table */
    private static final int CAKES = 100;

    /** URI matcher code for the content URI for a single cake in the cakes table */
    private static final int CAKE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.cakes/cakes" will map to the
        // integer code {@link #CAKES}. This URI is used to provide access to MULTIPLE rows
        // of the cakes table.
        sUriMatcher.addURI(CakeContract.CONTENT_AUTHORITY, CakeContract.PATH_CAKES, CAKES);

        // The content URI of the form "content://com.example.android.cakes/cakes/#" will map to the
        // integer code {@link #CAKE_ID}. This URI is used to provide access to ONE single row
        // of the cakes table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.cakes/cakes/3" matches, but
        // "content://com.example.android.cakes/cakes" (without a number at the end) doesn't match.
        sUriMatcher.addURI(CakeContract.CONTENT_AUTHORITY, CakeContract.PATH_CAKES + "/#", CAKE_ID);
    }

    /** Database helper object */
    private CakeDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new CakeDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CAKES:
                // For the CAKES code, query the cakes table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the cakes table.
                cursor = database.query(CakeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CAKE_ID:
                // For the CAKE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.cakes/cakes/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = CakeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the cakes table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(CakeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CAKES:
                return insertCake(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a cake into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCake(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(CakeEntry.COLUMN_CAKE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("cake requires a name");
        }

        // Check that the shape is valid
        Integer shape = values.getAsInteger(CakeEntry.COLUMN_CAKE_SHAPE);
        if (shape == null || !CakeEntry.isValidShape(shape)) {
            throw new IllegalArgumentException("Cake requires valid shape");
        }

        // If the price is provided, check that it's greater than  0
        Integer price = values.getAsInteger(CakeEntry.COLUMN_CAKE_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Cake requires valid price");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(CakeEntry.COLUMN_CAKE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Cake requires valid price");
        }

        // No need to check the cake supplier's name, any value is valid (including null).

        // No need to check the cake supplier's phone number, any value is valid (including null).

        // Check that the description is not null
        String description = values.getAsString(CakeEntry.COLUMN_CAKE_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("cake requires a description");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new cake with the given values
        long id = database.insert(CakeEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the cake content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CAKES:
                return updateCake(uri, contentValues, selection, selectionArgs);
            case CAKE_ID:
                // For the CAKE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CakeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCake(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update cakes in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more cakes).
     * Return the number of rows that were successfully updated.
     */
    private int updateCake(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link CakeEntry#COLUMN_CAKE_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(CakeEntry.COLUMN_CAKE_NAME)) {
            String name = values.getAsString(CakeEntry.COLUMN_CAKE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Cake requires a name");
            }
        }

        // If the {@link CakeEntry#COLUMN_CAKE_SHAPE} key is present,
        // check that the shape value is valid.
        if (values.containsKey(CakeEntry.COLUMN_CAKE_SHAPE)) {
            Integer shape = values.getAsInteger(CakeEntry.COLUMN_CAKE_SHAPE);
            if (shape == null || !CakeEntry.isValidShape(shape)) {
                throw new IllegalArgumentException("Cake requires valid shape");
            }
        }

        // If the {@link CakeEntry#COLUMN_CAKE_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(CakeEntry.COLUMN_CAKE_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer price = values.getAsInteger(CakeEntry.COLUMN_CAKE_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Cake requires valid price");
            }
        }

        // If the {@link CakeEntry#COLUMN_CAKE_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(CakeEntry.COLUMN_CAKE_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(CakeEntry.COLUMN_CAKE_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Cake requires valid quantity");
            }
        }

        // No need to check the cake supplier's name, any value is valid (including null).

        // No need to check the cake supplier's phone number, any value is valid (including null).

        // If the {@link CakeEntry#COLUMN_CAKE_DESCRIPTION} key is present,
        // check that the description value is not null.
        if (values.containsKey(CakeEntry.COLUMN_CAKE_DESCRIPTION)) {
            String description = values.getAsString(CakeEntry.COLUMN_CAKE_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Cake requires a description");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CakeEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CAKES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CakeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CAKE_ID:
                // Delete a single row given by the ID in the URI
                selection = CakeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CakeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CAKES:
                return CakeEntry.CONTENT_LIST_TYPE;
            case CAKE_ID:
                return CakeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}