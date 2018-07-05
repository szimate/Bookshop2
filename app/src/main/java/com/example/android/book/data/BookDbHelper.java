package com.example.android.book.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.book.data.BookContract.BookEntry;

/**
 * Database helper for Book app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "BookShop.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_PRODUCT_INFO_TABLE =
                "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                        BookEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT ," +
                        BookEntry.COL_BOOK_NAME + TEXT_TYPE + " NOT NULL, " +
                        BookContract.BookEntry.COL_BOOK_PRICE + INTEGER_TYPE + " NOT NULL, " +
                        BookEntry.COL_BOOK_QUANTITY + INTEGER_TYPE + " NOT NULL DEFAULT 0, " +
                        BookContract.BookEntry.COL_BOOK_SUPPLIER_NAME + TEXT_TYPE + " NOT NULL, " +
                        BookEntry.COL_BOOK_SUPPLIER_EMAIL + TEXT_TYPE + " NOT NULL , " +
                        BookEntry.COL_BOOK_SUPPLIER_PHONE + TEXT_TYPE + " );";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCT_INFO_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}