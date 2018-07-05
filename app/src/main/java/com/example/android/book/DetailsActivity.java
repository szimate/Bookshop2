package com.example.android.book;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.book.data.BookContract;
import com.example.android.book.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIEW_BOOK_LOADER = 2;
    private Uri currentBookUri;

    private TextView nameView;
    private TextView quantityView;
    private Button decreaseQuantityButton;
    private Button increaseQuantityButton;
    private TextView totalView;
    private TextView supplierNameView;
    private TextView supplierEmailView;
    private TextView supplierPhoneNumberView;
    private ImageButton callSupplierButton;


    private String bookName;
    private int quantity;
    private int pricePerBook;
    private String supplierName;
    private String supplierEmail;
    private String supplierPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        currentBookUri = getIntent().getData();
        //Kick off the loader.
        getLoaderManager().initLoader(VIEW_BOOK_LOADER, null, this);

        nameView = findViewById(R.id.details_name);
        quantityView = findViewById(R.id.details_quantity);
        decreaseQuantityButton = findViewById(R.id.details_decrease);
        increaseQuantityButton = findViewById(R.id.details_increase);
        totalView = findViewById(R.id.details_total);
        supplierNameView = findViewById(R.id.details_supplier_name);
        supplierEmailView = findViewById(R.id.details_email);
        supplierPhoneNumberView = findViewById(R.id.details_phone);
        callSupplierButton = findViewById(R.id.image_phone);

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(DetailsActivity.this, R.string.updated_quantity_error, Toast.LENGTH_LONG).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast (for MORE than ONE book).
                        Toast.makeText(DetailsActivity.this, R.string.updated_quantity, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity >= 0) {
                    int newQuantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(DetailsActivity.this, R.string.updated_quantity_error, Toast.LENGTH_LONG).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast (for MORE than ONE book).
                        Toast.makeText(DetailsActivity.this, R.string.updated_quantity, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookContract.BookEntry._ID,
                BookEntry.COL_BOOK_NAME,
                BookContract.BookEntry.COL_BOOK_PRICE,
                BookContract.BookEntry.COL_BOOK_QUANTITY,
                BookEntry.COL_BOOK_SUPPLIER_NAME,
                BookEntry.COL_BOOK_SUPPLIER_EMAIL,
                BookEntry.COL_BOOK_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,
                currentBookUri,      // Query the content URI for the *current* book.
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            bookName = data.getString(data.getColumnIndex(BookEntry.COL_BOOK_NAME));
            pricePerBook = data.getInt(data.getColumnIndex(BookEntry.COL_BOOK_PRICE));
            quantity = data.getInt(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_QUANTITY));
            supplierName = data.getString(data.getColumnIndex(BookEntry.COL_BOOK_SUPPLIER_NAME));
            supplierEmail = data.getString(data.getColumnIndex(BookEntry.COL_BOOK_SUPPLIER_EMAIL));
            supplierPhoneNumber = data.getString(data.getColumnIndex(BookEntry.COL_BOOK_SUPPLIER_PHONE));

            // Update the views on the screen with the values from the database
            nameView.setText(bookName);
            quantityView.setText(String.valueOf(quantity));
            totalView.setText(String.valueOf(calculateTotalPrice()));
            supplierNameView.setText(supplierName);
            supplierEmailView.setText(supplierEmail);
            supplierPhoneNumberView.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameView.setText("");
        quantityView.setText("");
        totalView.setText(String.valueOf(0));
        supplierNameView.setText("");
        supplierEmailView.setText("");
        supplierPhoneNumberView.setText("");
    }

    private int calculateTotalPrice() {
        return quantity * pricePerBook;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option.
            case R.id.action_details_edit:
                // Start the EditActivity to edit the book.
                Log.i("CatalogActivity", "pressedItemUri: " + currentBookUri);
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.setData(currentBookUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_details_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {

        // Call the ContentResolver to delete the book at the given content URI.
        // Pass in null for the selection and selection args because the currentBookUri
        // content URI already identifies the book that we want.
        int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, R.string.delete_book_error, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_book, Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
    }
}
