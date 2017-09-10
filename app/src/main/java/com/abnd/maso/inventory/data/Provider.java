package com.abnd.maso.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.abnd.maso.inventory.data.Contract.InventoryEntry;

/**
 * Created by mariosoberanis on 11/22/16.
 */

public class Provider extends ContentProvider {

    public static final String TAG = Provider.class.getSimpleName();

    private static final int INVENTORY = 100;

    private static final int INVENTORY_ID = 101;

    private static final UriMatcher UrlMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        UrlMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY, INVENTORY);

        UrlMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private DBHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        int match = UrlMatcher.match(uri);
        int rowsUpdated;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot update empty values");
        }
        switch (match) {
            case INVENTORY:
                rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case INVENTORY_ID:
                rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues, InventoryEntry._ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return rowsUpdated;
    }

    public Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(InventoryEntry.COL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("The name is ematy");
        }

        Integer quantity = values.getAsInteger(InventoryEntry.COL_QUANTITY);

        Float price = values.getAsFloat(InventoryEntry.COL_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("unvalid price");
        }
        SQLiteDatabase database = dbhelper.getWritableDatabase();


        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor;

        int match = UrlMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = db.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INVENTORY_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = UrlMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = UrlMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException();
        }


    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        int match = UrlMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delition is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

}

