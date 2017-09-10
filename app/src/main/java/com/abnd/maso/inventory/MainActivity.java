package com.abnd.maso.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.abnd.maso.inventory.data.Contract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER = 0;
    public final String[] PRODUCT_COLS = {
            InventoryEntry._ID,
            InventoryEntry.COL_NAME,
            InventoryEntry.COL_QUANTITY,
            InventoryEntry.COL_PRICE,
            InventoryEntry.COL_DESCRIPTION,
            InventoryEntry.COL_ITEMS_SOLD,
            InventoryEntry.COL_SUPPLIER,
            InventoryEntry.COL_PICTURE
    };

    private CursorAdapter CursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Editor.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        CursorAdapter = new CursorAdapter(this, null);
        listView.setAdapter(CursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Editor.class);

                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(LOADER, null, this);

    }

    private void dummyData() {

        String productName = "NewProduct Random";
        int quantity = 5;
        float price = 10;

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COL_NAME, productName);
        values.put(InventoryEntry.COL_QUANTITY, quantity);
        values.put(InventoryEntry.COL_PRICE, price);

        Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                PRODUCT_COLS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        CursorAdapter.swapCursor(data);

    }

    private void delete() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        CursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummy_data:
                dummyData();
                return true;
            case R.id.delete:
                delete();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}
