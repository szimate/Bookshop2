package com.example.android.book;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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

public class AddActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText priceEdit;
    private EditText availableQuantityEdit;
    private EditText supplierNameEdit;
    private EditText supplierEmailEdit;
    private EditText supplierPhoneNumberEdit;

    private boolean bookHasChanged = false;
    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the bookHasChanged boolean to true.
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

        // Find all relevant views that we will need to read user input from
        nameEdit = findViewById(R.id.edit_book_name);
        priceEdit = findViewById(R.id.edit_price);
        availableQuantityEdit = findViewById(R.id.editquantity);
        supplierNameEdit = findViewById(R.id.edit_supplier_name);
        supplierEmailEdit = findViewById(R.id.edit_supplier_email);
        supplierPhoneNumberEdit = findViewById(R.id.edit_supplier_phone_number);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEdit.setOnTouchListener(touchListener);
        priceEdit.setOnTouchListener(touchListener);
        availableQuantityEdit.setOnTouchListener(touchListener);
        supplierNameEdit.setOnTouchListener(touchListener);
        supplierEmailEdit.setOnTouchListener(touchListener);
        supplierPhoneNumberEdit.setOnTouchListener(touchListener);

    }

    //Calls when the Save acton button is pressed to insert the book into the database.
    private void insertBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEdit.getText().toString().trim();
        String priceString = priceEdit.getText().toString().trim();
        String availableQuantityString = availableQuantityEdit.getText().toString().trim();
        String supplierNameString = supplierNameEdit.getText().toString().trim();
        String supplierEmailString = supplierEmailEdit.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhoneNumberEdit.getText().toString().trim();

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
            values.put(BookContract.BookEntry.COL_BOOK_PRICE, priceString);
            values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, availableQuantityString);
            values.put(BookContract.BookEntry.COL_BOOK_SUPPLIER_NAME, supplierNameString);
            values.put(BookContract.BookEntry.COL_BOOK_SUPPLIER_EMAIL, supplierEmailString);
            values.put(BookContract.BookEntry.COL_BOOK_SUPPLIER_PHONE, supplierPhoneNumberString);

            // Insert a new book into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.msg_add_save_error, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.msg_add_save_success, Toast.LENGTH_SHORT).show();
            }
            // Exit activity.
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option.
            case R.id.action_add_save:
                // Save book to database.
                insertBook();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar.
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
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
                                NavUtils.navigateUpFromSameTask(AddActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
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
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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
}