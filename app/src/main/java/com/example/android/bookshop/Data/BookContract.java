package com.example.android.bookshop.Data;

import android.provider.BaseColumns;

/**
 * API Contract for the Book shop app.
 */
public final class BookContract {

    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {
        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";
        /**
         * Unique ID number for the book.
         * <p>
         * Type: INTEGER
         */
        public final static String COL_ID = BaseColumns._ID;
        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COL_PRODUCT_NAME = "product_name";
        /**
         * Price of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COL_Product_PRICE = "product_price";
        /**
         * Quantity of rhe product.
         * <p>
         * Type: INTEGER
         */
        public final static String COL_PRODUCT_QUANTITY = "product_quantity";
        /**
         * Supplier name.
         * <p>
         * Type: TEXT
         */
        public final static String COL_SUPPLIER_NAME = "supplier_name";
        /**
         * Supplier phone number.
         * <p>
         * Type: TEXT
         */
        public final static String COL_SUPPLIER_PHONE = "supplier_phone";
        /**
         * Supplier email number.
         * <p>
         * Type: TEXT
         */
        public final static String COL_SUPPLIER_EMAIL = "supplier_email";
    }
}