package com.example.android.cakeinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cakeinventory.data.CakeContract.CakeEntry;

/**
 * {@link CakeCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of cake data as its data source. This adapter knows
 * how to create list items for each row of cake data in the {@link Cursor}.
 */
public class CakeCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = CakeCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link CakeCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public CakeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.tv_cake_name);
        TextView priceTextView = view.findViewById(R.id.tv_price);
        TextView quantityTextView = view.findViewById(R.id.tv_quantity);

        // Find the columns of cake attributes that we're interested in
        int cakeNameColumnIndex = cursor.getColumnIndex(CakeEntry.COLUMN_CAKE_NAME);
        int priceColumnIndex = cursor.getColumnIndex(CakeEntry.COLUMN_CAKE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(CakeEntry.COLUMN_CAKE_QUANTITY);

        // Read the pet attributes from the Cursor for the current pet
        String cakeName = cursor.getString(cakeNameColumnIndex);
        String cakePrice = cursor.getString(priceColumnIndex);
        final String cakeQuantity = cursor.getString(quantityColumnIndex);

        // If the cake price is empty string or null, then use some default text
        // that says "Ask for a price", so the TextView isn't blank.
        if (TextUtils.isEmpty(cakePrice)) {
            cakePrice = context.getString(R.string.ask_price);
        }

        final int quantityTextView_int = cursor.getInt(quantityColumnIndex);
        final Uri uri = ContentUris.withAppendedId(CakeEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndexOrThrow(CakeEntry._ID)));


        // Update the TextViews with the attributes for the current cake
        nameTextView.setText(cakeName);
        priceTextView.setText(cakePrice);
        quantityTextView.setText(cakeQuantity);

        //Reduce 1 in product quantity if > 0, update in the database of the current row
        //Update the TextViews with the new value of the atribute
        Button saleButton = view.findViewById(R.id.button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityTextView_int > 0) {
                    int newQuantity = quantityTextView_int - 1;

                    ContentValues values = new ContentValues();
                    values.put(CakeEntry.COLUMN_CAKE_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, values, null, null);
                    Log.d(LOG_TAG, "URI for update: " + uri);
                } else {
                    Toast.makeText(context, context.getString(R.string.sold_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}