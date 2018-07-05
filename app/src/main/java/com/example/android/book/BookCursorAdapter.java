package com.example.android.book;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.book.data.BookContract.BookEntry;


public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.view_book_name);
        TextView priceTextView = view.findViewById(R.id.view_book_price);
        TextView quantityTextView = view.findViewById(R.id.view_book_quantity);
        ImageButton addToCartImageButton = view.findViewById(R.id.button_book_addTocCart);

        final int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(BookEntry.COL_BOOK_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(BookEntry.COL_BOOK_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COL_BOOK_QUANTITY));

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        addToCartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                addToCart(context, currentBookUri, quantity);
            }
        });
    }

    private void addToCart(Context context, Uri uri, int quantity) {
        if (quantity > 0) {
            int newAvailableQuantityValue = quantity - 1;

            ContentValues values = new ContentValues();
            values.put(BookEntry.COL_BOOK_QUANTITY, newAvailableQuantityValue);


            int rowsAffected = context.getContentResolver().update(uri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(context, R.string.book_added_error, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(context, R.string.book, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
