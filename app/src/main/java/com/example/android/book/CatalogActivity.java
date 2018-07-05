package com.example.android.book;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.book.data.BookContract;
import com.example.android.book.data.BookContract.BookEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;

    /**
     * Adapter for the GridView
     */
    private BookCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        // Find the GridView which will be populated with the book data
        GridView bookGridView = findViewById(R.id.gridView);

        // Find and set empty view on the GridView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.emptyView);
        bookGridView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        //There is no book data yet (until the loader finishes), so pass in null for the Cursor.
        cursorAdapter = new BookCursorAdapter(this, null);

        // Attach the cursorAdapter to the GridView.
        bookGridView.setAdapter(cursorAdapter);

        //Kick off the loader.
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        bookGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                Log.i("CatalogActivity", "pressedItemUri: " + currentBookUri);
                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookContract.BookEntry._ID,
                BookEntry.COL_BOOK_NAME,
                BookContract.BookEntry.COL_BOOK_PRICE,
                BookContract.BookEntry.COL_BOOK_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        cursorAdapter.swapCursor(null);
    }
}
