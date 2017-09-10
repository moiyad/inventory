package com.abnd.maso.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abnd.maso.inventory.data.Contract.InventoryEntry;
import com.bumptech.glide.Glide;

import java.io.File;

import static com.abnd.maso.inventory.R.mipmap.ic_launcher;

/**
 * Created by mariosoberanis on 11/22/16.
 */

public class Editor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = Editor.class.getSimpleName();
    public static final int PHOTO_REQUEST = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;
    private static final int EXISTING_INVENTORY_LOADER = 0;
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

    private Uri mUrl;
    private ImageView photo;
    private EditText name;
    private EditText description;
    private EditText inventory;
    private EditText sold;
    private EditText price;
    private EditText supplier;
    private Button deleteAction;
    private Button increase;
    private Button decrease;
    private Button orderAction;
    private Button updateAction;
    private TextView saveUpdate;
    private Button soldItem;
    private TextView order;
    private TextView delete;
    private String currentPhoto = "no images";
    private String email;
    private String mProduct;
    private int quantity = 50;
    private boolean ProducChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            ProducChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        photo = (ImageView) findViewById(R.id.image_product_photo);
        name = (EditText) findViewById(R.id.inventory_item_name_edittext);
        description = (EditText) findViewById(R.id.inventory_item_description_edittext);
        inventory = (EditText) findViewById(R.id.inventory_item_current_quantity_edittext);
        sold = (EditText) findViewById(R.id.current_sales_edittext);
        price = (EditText) findViewById(R.id.inventory_item_price_edittext);
        supplier = (EditText) findViewById(R.id.suplier_edittext);
        photo.setOnTouchListener(mTouchListener);
        name.setOnTouchListener(mTouchListener);
        description.setOnTouchListener(mTouchListener);
        inventory.setOnTouchListener(mTouchListener);
        sold.setOnTouchListener(mTouchListener);
        price.setOnTouchListener(mTouchListener);
        soldItem = (Button) findViewById(R.id.buy_Item);
        supplier.setOnTouchListener(mTouchListener);
        deleteAction = (Button) findViewById(R.id.delete_product_button);
        orderAction = (Button) findViewById(R.id.order_supplier_button);
        updateAction = (Button) findViewById(R.id.save_product_button);
        increase = (Button) findViewById(R.id.increase);
        decrease = (Button) findViewById(R.id.decrease);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoUpdate(view);
            }
        });

        updateAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumOne(sold);

            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOne(sold);
            }
        });

        soldItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subtractOne(inventory);
                String previousValueString = inventory.getText().toString();
                int i = Integer.parseInt(previousValueString);
                if (i != 0) {
                    sumOne(sold);
                }
            }
        });


        Intent intent = getIntent();
        mUrl = intent.getData();

        if (mUrl == null) {
            setTitle(getString(R.string.add_product_title));
            orderAction.setVisibility(View.GONE);
            deleteAction.setVisibility(View.GONE);
            increase.setVisibility(View.VISIBLE);
            decrease.setVisibility(View.VISIBLE);
        } else {
            setTitle(getString(R.string.edit_product_title));

            orderAction.setVisibility(View.VISIBLE);
            increase.setVisibility(View.VISIBLE);
            decrease.setVisibility(View.VISIBLE);
            deleteAction.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        deleteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete();
            }
        });

        orderAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;

    }

    private void Delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProdeuct();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //Go back if we have no changes
        if (!ProducChanged) {
            super.onBackPressed();
            return;
        }
    }

    private void deleteProdeuct() {
        if (mUrl != null) {

            int rowsDeleted = getContentResolver().delete(mUrl, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                return true;

            case android.R.id.home:
                if (!ProducChanged) {
                    NavUtils.navigateUpFromSameTask(Editor.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(Editor.this);
                            }
                        };
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GetPhoto();
        } else {
            Toast.makeText(this, R.string.errExternal, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri mProductPhotoUri = data.getData();
                currentPhoto = mProductPhotoUri.toString();
                Log.d(TAG, "Selected images " + mProductPhotoUri);
                Glide.with(this).load(mProductPhotoUri).placeholder(ic_launcher).crossFade().fitCenter().into(photo);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mUrl,
                PRODUCT_COLS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int i_ID = 0;
            int i_COL_NAME = 1;
            int i_COL_QUANTITY = 2;
            int i_COL_PRICE = 3;
            int i_COL_DESCRIPTION = 4;
            int i_COL_ITEMS_SOLD = 5;
            int i_COL_SUPPLIER = 6;
            int i_COL_PICTURE = 7;

            String name = cursor.getString(i_COL_NAME);
            int quantity = cursor.getInt(i_COL_QUANTITY);
            float price = cursor.getFloat(i_COL_PRICE);
            String description = cursor.getString(i_COL_DESCRIPTION);
            int itemSold = cursor.getInt(i_COL_ITEMS_SOLD);
            String supplier = cursor.getString(i_COL_SUPPLIER);
            String photo = cursor.getString(i_COL_PICTURE);
            currentPhoto = cursor.getString(i_COL_PICTURE);
            email = "orders@" + supplier + ".com";
            mProduct = name;
            this.name.setText(name);
            this.price.setText(String.valueOf(price));
            inventory.setText(String.valueOf(quantity));
            this.description.setText(description);
            sold.setText(String.valueOf(itemSold));
            this.supplier.setText(supplier);

            Glide.with(this).load(currentPhoto).placeholder(ic_launcher).error(ic_launcher).crossFade().fitCenter().into(this.photo);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        price.setText("");
        inventory.setText("");
        description.setText("");
        sold.setText("");
        supplier.setText("");

    }

    private void save() {
        String nameString = name.getText().toString().trim();
        String descriptionString = description.getText().toString().trim();
        String inventoryString = inventory.getText().toString().toString();
        String salesString = sold.getText().toString().trim();
        String priceString = price.getText().toString().trim();
        String supplierString = supplier.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(descriptionString)
                || TextUtils.isEmpty(inventoryString) || TextUtils.isEmpty(salesString)
                || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(supplierString)) {

            Toast.makeText(this, R.string.err_missing_textfields, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COL_NAME, nameString);
        values.put(InventoryEntry.COL_DESCRIPTION, descriptionString);
        values.put(InventoryEntry.COL_QUANTITY, inventoryString);
        values.put(InventoryEntry.COL_ITEMS_SOLD, salesString);
        values.put(InventoryEntry.COL_PRICE, priceString);
        values.put(InventoryEntry.COL_SUPPLIER, supplierString);
        values.put(InventoryEntry.COL_PICTURE, currentPhoto);

        if (mUrl == null) {

            Uri insertedRow = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (insertedRow == null) {
                Toast.makeText(this, R.string.err_inserting_product, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.ok_updated, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            int rowUpdated = getContentResolver().update(mUrl, values, null, null);
            if (rowUpdated == 0) {
                Toast.makeText(this, R.string.err_inserting_product, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.ok_updated, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void order() {
        String[] TO = {email};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order " + mProduct);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please ship " + mProduct +
                " in quantities " + quantity);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void PhotoUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                GetPhoto();
            } else {
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            GetPhoto();
        }
    }

    private void GetPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, PHOTO_REQUEST);
    }

    public void sumOne(EditText view) {
        String previousValueString = view.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        view.setText(String.valueOf(previousValue + 1));
    }

    public void subtractOne(EditText view) {
        String previousValueString = view.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            view.setText(String.valueOf(previousValue - 1));
        }
    }
}

