package com.example.android.bookshop.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookshop.Data.BookContract.BookEntry;

/**
 * Database helper for Book shop app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    public final static String DATABASE_NAME = "inventory.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    public final static int DATABASE_VERSION = 1;

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the books table
        String CREATE_SQL_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry.COL_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COL_PRODUCT_NAME + TEXT_TYPE + " NOT NULL, "
                + BookEntry.COL_Product_PRICE + INTEGER_TYPE + " NOT NULL DEFAULT 0, "
                + BookEntry.COL_PRODUCT_QUANTITY + INTEGER_TYPE + " NOT NULL DEFAULT 0, "
                + BookEntry.COL_SUPPLIER_NAME + TEXT_TYPE + " NOT NULL, "
                + BookEntry.COL_SUPPLIER_PHONE + TEXT_TYPE + " NOT NULL, "
                + BookEntry.COL_SUPPLIER_EMAIL + TEXT_TYPE + " NOT NULL);";

        // Execute the SQL statement
        db.execSQL(CREATE_SQL_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}