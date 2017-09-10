package com.abnd.maso.inventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static android.R.attr.id;
import static com.abnd.maso.inventory.R.mipmap.ic_launcher;
import static com.abnd.maso.inventory.data.Contract.InventoryEntry;

/**
 * Created by mariosoberanis on 11/22/16.
 */

public class CursorAdapter extends android.widget.CursorAdapter {

    Context context;
    private Editor editor;
    int i=0;
    int z=0;
    private static final String TAG = CursorAdapter.class.getSimpleName();


    protected CursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }


    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView product_name = (TextView) view.findViewById(R.id.inventory_item_name_text);
        TextView buyItem = (TextView) view.findViewById(R.id.getItem);
        final TextView product_quantity = (TextView) view.findViewById(R.id.inventory_item_current_quantity_text);
        TextView product_price = (TextView) view.findViewById(R.id.inventory_item_price_text);
        final TextView product_sold = (TextView) view.findViewById(R.id.inventory_item_current_sold_text);
        ImageView product_thumbnail = (ImageView) view.findViewById(R.id.product_thumbnail);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRICE);
        int thumbnailColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PICTURE);
        final int salesColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_ITEMS_SOLD);

        int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final String productName = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int products_sold = cursor.getInt(salesColumnIndex);
        String productPrice = "Price RS" + cursor.getString(priceColumnIndex);
        Uri thumbUri = Uri.parse(cursor.getString(thumbnailColumnIndex));

        buyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity+z!=0){
                    Toast.makeText(context,"You bought one item from "+ productName,Toast.LENGTH_SHORT).show();
                    i++;
                    z--;
                    String productQuantity = String.valueOf(quantity+z) + " Inventory";
                    String productSold = String.valueOf(products_sold+i) + " Sold";
                    product_sold.setText(productSold);
                    product_quantity.setText(productQuantity);
                }else
                    Toast.makeText(context,"this product has finished",Toast.LENGTH_SHORT).show();



            }
        });

        String productQuantity = String.valueOf(quantity) + " Inventory";
        String productSold = String.valueOf(products_sold) + " Sold";
        product_name.setText(productName);
        product_quantity.setText(productQuantity);
        product_price.setText(productPrice);
        product_sold.setText(productSold);
        Glide.with(context).load(thumbUri).placeholder(ic_launcher).error(ic_launcher).crossFade().centerCrop().into(product_thumbnail);
    }
}

