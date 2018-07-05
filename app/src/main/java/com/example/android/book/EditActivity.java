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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.book.data.BookContract;
import com.example.android.book.data.BookContract.BookEntry;

/**
 * Allows user to edit an existing one.
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText availableQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierEmailEditText;
    private EditText supplierPhoneNumberEditText;

    /** Identifier for the book data loader */
    private static final int UPDATE_BOOK_LOADER = 1;

    /** Content URI for the existing BOOK */
    private Uri currentBookUri;

    /** Boolean flag that keeps track of whether the book has been edited (true) or not (false) */
    private boolean bookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editors);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're editing an existing one.
        currentBookUri = getIntent().getData();
        //Kick off the loader.
        getLoaderManager().initLoader(UPDATE_BOOK_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        nameEditText = findViewById(R.id.edit_book_name);
        priceEditText = findViewById(R.id.edit_price);
        availableQuantityEditText = findViewById(R.id.editquantity);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierEmailEditText = findViewById(R.id.edit_supplier_email);
        supplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        availableQuantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierEmailEditText.setOnTouchListener(touchListener);
        supplierPhoneNumberEditText.setOnTouchListener(touchListener);
    }

    //Calls when the edit acton button is pressed to update a specified book.
    private void updateBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String availableQuantityString = availableQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierEmailString = supplierEmailEditText.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhoneNumberEditText.getText().toString().trim();

        // Check if all the fields in the editor are blank
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(availableQuantityString) || TextUtils.isEmpty(supplierNameString)
                || TextUtils.isEmpty(supplierEmailString) || TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, R.string.fill_the_fields,
                    Toast.LENGTH_LONG).show();

        } else {

            // Create a ContentValues object where column names are the keys,
            // and book attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COL_BOOK_NAME, nameString);
            values.put(BookEntry.COL_BOOK_PRICE, priceString);
            values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, availableQuantityString);
            values.put(BookEntry.COL_BOOK_SUPPLIER_NAME, supplierNameString);
            values.put(BookContract.BookEntry.COL_BOOK_SUPPLIER_EMAIL, supplierEmailString);
            values.put(BookEntry.COL_BOOK_SUPPLIER_PHONE, supplierPhoneNumberString);

            // This is an EXISTING book, so update the book with content URI: currentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this,R.string.update_book_error , Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.update_book, Toast.LENGTH_SHORT).show();
            }
            // Exit activity.
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option.
            case R.id.action_edit_save:
                // Update the books.
                updateBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_edit_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar.
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link DetailsActivity}.
                if (!bookHasChanged) {
                    Intent intent = NavUtils.getParentActivityIntent(this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    NavUtils.navigateUpTo(this, intent);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                Intent intent = NavUtils.getParentActivityIntent(EditActivity.this);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                NavUtils.navigateUpTo(EditActivity.this, intent);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press.
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookContract.BookEntry.COL_BOOK_NAME,
                BookEntry.COL_BOOK_PRICE,
                BookContract.BookEntry.COL_BOOK_QUANTITY,
                BookContract.BookEntry.COL_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COL_BOOK_SUPPLIER_EMAIL,
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
            // Find the columns of pet attributes that we're interested in
            String name = data.getString(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_NAME));
            String price = String.valueOf(data.getInt(data.getColumnIndex(BookEntry.COL_BOOK_PRICE)));
            String availableQuantity = String.valueOf(data.getInt(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_QUANTITY)));
            String supplierName = data.getString(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_SUPPLIER_NAME));
            String supplierEmail = data.getString(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_SUPPLIER_EMAIL));
            String supplierPhoneNumber = data.getString(data.getColumnIndex(BookContract.BookEntry.COL_BOOK_SUPPLIER_PHONE));

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            priceEditText.setText(price);
            availableQuantityEditText.setText(availableQuantity);
            supplierNameEditText.setText(supplierName);
            supplierEmailEditText.setText(supplierEmail);
            supplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.getText().clear();
        priceEditText.getText().clear();
        availableQuantityEditText.getText().clear();
        supplierNameEditText.getText().clear();
        supplierEmailEditText.getText().clear();
        supplierPhoneNumberEditText.getText().clear();
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
            Toast.makeText(this, R.string.delete_book_error,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_book,
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
        Intent intent = new Intent(EditActivity.this, CatalogActivity.class);
        startActivity(intent);
    }
}
