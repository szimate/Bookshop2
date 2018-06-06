package com.example.android.bookshop;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bookshop.Data.BookContract.BookEntry;
import com.example.android.bookshop.Data.BookDbHelper;

/**
 * "Displays" list of books that were stored in the app.
 */
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookDbHelper = new BookDbHelper(this);

        insertItem();
        displayData();
    }

    private Cursor queryData() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COL_PRODUCT_NAME,
                BookEntry.COL_Product_PRICE,
                BookEntry.COL_PRODUCT_QUANTITY,
                BookEntry.COL_SUPPLIER_NAME,
                BookEntry.COL_SUPPLIER_PHONE,
                BookEntry.COL_SUPPLIER_EMAIL
        };

        // Perform a query on the books table
        return db.query(BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }

    public void displayData() {

        Cursor cursor = queryData();

        try {
            // Count and log the books number.
            Log.i(LOG_TAG, "The books table contains " + cursor.getCount() + " books.\n\n");

            Log.i(LOG_TAG, BookEntry._ID + " \t " +
                    BookEntry.COL_PRODUCT_NAME + " \t " +
                    BookEntry.COL_Product_PRICE + " \t " +
                    BookEntry.COL_PRODUCT_QUANTITY + " \t " +
                    BookEntry.COL_PRODUCT_QUANTITY + " \t " +
                    BookEntry.COL_SUPPLIER_NAME + " \t " +
                    BookEntry.COL_SUPPLIER_PHONE + " \t " +
                    BookEntry.COL_SUPPLIER_EMAIL
            );

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry.COL_ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COL_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COL_Product_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COL_PRODUCT_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(BookEntry.COL_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COL_SUPPLIER_PHONE);
            int supEmailColumnIndex = cursor.getColumnIndex(BookEntry.COL_SUPPLIER_EMAIL);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupName = cursor.getString(supNameColumnIndex);
                String currentSupPhone = cursor.getString(supPhoneColumnIndex);
                String currentSupEmail = cursor.getString(supEmailColumnIndex);
                // Log the values from each column of the current row.
                Log.i(LOG_TAG, currentId + " \t "
                        + currentName + " \t "
                        + currentPrice + " \t "
                        + currentQuantity + " \t "
                        + currentSupName + " \t "
                        + currentSupPhone + " \t "
                        + currentSupEmail
                );
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertItem() {
        // Gets the database in write mode
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and the book attributes are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COL_PRODUCT_NAME, "Dictators in the EU - Orban Viktor");
        contentValues.put(BookEntry.COL_Product_PRICE, 3000);
        contentValues.put(BookEntry.COL_PRODUCT_QUANTITY, 2000);
        contentValues.put(BookEntry.COL_SUPPLIER_NAME, "Meszaros & Meszaros Nyrt.");
        contentValues.put(BookEntry.COL_SUPPLIER_PHONE, "06 10-78-666-999");
        contentValues.put(BookEntry.COL_SUPPLIER_EMAIL, "evil@hungary.hu");

        // Insert a new row for the book in the database, returning the ID of that new row.
        // The first argument for db.insert() is the books table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the book.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, contentValues);

        Log.v("MainActivity", "new roe ID" + newRowId);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "Error with saving product info");
        } else {
            Log.i(LOG_TAG, "Product saved with ID " + newRowId);
        }
    }
}
