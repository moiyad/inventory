package com.abnd.maso.inventory.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {


    public static final String CONTENT_AUTHORITY = "com.abnd.maso.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";


    public Contract() {
    }

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_INVENTORY;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_INVENTORY;


        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;
        public final static String COL_NAME = "name";
        public final static String COL_QUANTITY = "quantity";
        public final static String COL_PRICE = "price";
        public final static String COL_DESCRIPTION = "description";
        public final static String COL_ITEMS_SOLD = "sales";
        public final static String COL_SUPPLIER = "supplier";
        public final static String COL_PICTURE = "picture";


    }

}
