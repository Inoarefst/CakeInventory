package com.example.android.cakeinventory.data;

import android.net.Uri;

import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the Cakes app.
 */
public final class CakeContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CakeContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.cakeinventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.cakes/cakes/ is a valid path for
     * looking at cake data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_CAKES = "cakes";

    /**
     * Inner class that defines constant values for the cakes database table.
     * Each entry in the table represents a single cake.
     */
    public static final class CakeEntry implements BaseColumns {

        /** The content URI to access the cake data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CAKES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAKES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single cake.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAKES;

        /** Name of database table for cakes */
        public final static String TABLE_NAME = "cakes";

        /**
         * Unique ID number for the cake (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the cake.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAKE_NAME ="name";

        /**
         * Shape of the cake.
         * The only possible values are {@link #SHAPE_UNKNOWN}, {@link #SHAPE_RING},
         *  {@link #SHAPE_ROUND} , or (@link #SHAPE_SQUARE) .
         * Type: INTEGER
         */
        public final static String COLUMN_CAKE_SHAPE = "shape";

        /**
         * price of the cake.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAKE_PRICE = "price";

        /**
         * Quantity of the cake.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAKE_QUANTITY = "quantity";

        /**
         * Name of the cake supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAKE_SUPPLIER ="supplier";

        /**
         * Phone number of the cake supplier.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CAKE_PHONE ="phone";

        /**
         * Description of the cake .
         *
         * Type: TEXT
         */
        public final static String COLUMN_CAKE_DESCRIPTION ="description";


        /**
         * Possible values for the shape of the cake.
         */
        public static final int SHAPE_UNKNOWN = 0;
        public static final int SHAPE_RING = 1;
        public static final int SHAPE_ROUND = 2;
        public static final int SHAPE_SQUARE = 3;

        /**
         * Returns whether or not the given shape is {@link #SHAPE_UNKNOWN}, {@link #SHAPE_RING},
         *  {@link #SHAPE_ROUND} or (@link #SHAPE_SQUARE).
         */
        public static boolean isValidShape(int shape) {
            if (shape == SHAPE_UNKNOWN || shape == SHAPE_RING || shape == SHAPE_ROUND || shape == SHAPE_SQUARE) {
                return true;
            }
            return false;
        }
    }
}