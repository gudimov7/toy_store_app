package com.example.toy_store_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;


import com.example.toy_store_app.adapters.StoreItemAdminDashAdapter;
import com.example.toy_store_app.firebase.FirebaseDB;
import com.example.toy_store_app.firebase.FirebaseST;
import com.example.toy_store_app.services.FF;
import com.example.toy_store_app.services.ItemDescription;
import com.example.toy_store_app.services.StoreItem;
import static com.example.toy_store_app.services.FF.*;

import java.io.File;
import java.util.ArrayList;

public class AdminItemsListActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ListView itemsLV;
    private Button addBtn;
    private ArrayList<StoreItem> storeItems;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_items_list);
        storeItems = new ArrayList<>();

        itemsLV = (ListView) findViewById(R.id.adminItemListActivity_lv_storeItemList);
        addBtn = (Button) findViewById(R.id.adminItemListActivity_btn_add);

        addBtn.setOnClickListener(v -> {
            dialog = new Dialog(AdminItemsListActivity.this);
            dialog.setContentView(R.layout.dialog_store_item_add_new);
            ImageButton itemPicIV = (ImageButton) dialog.findViewById(R.id.adminStoreItem_dialog_iv_itemPic);
            EditText itemNameET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemName);
            EditText itemAgeET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemAge);
            EditText itemColorET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemColor);
            EditText itemMaterialET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemMaterial);
            EditText itemMadeET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemMade);
            EditText itemPriceET = (EditText) dialog.findViewById(R.id.adminStoreItem_dialog_et_itemPrice);
            Button addBtn = (Button) dialog.findViewById(R.id.adminStoreItem_dialog_btn_add);
            Button clearBtn = (Button) dialog.findViewById(R.id.adminStoreItem_dialog_btn_clear);

            itemPicIV.setOnClickListener(iv -> {
                if (!isEditTextEmpty(itemNameET)) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent});

                }
            });
            clearBtn.setOnClickListener(cv -> {
                itemNameET.setText("");
                itemAgeET.setText("");
                itemColorET.setText("");
                itemMaterialET.setText("");
                itemMadeET.setText("");
                itemPriceET.setText("");
            });
            addBtn.setOnClickListener(av -> {
                if (
                        !isEditTextEmpty(itemNameET) &&
                        !isEditTextEmpty(itemAgeET) &&
                        !isEditTextEmpty(itemColorET) &&
                        !isEditTextEmpty(itemMaterialET) &&
                        !isEditTextEmpty(itemMadeET) &&
                        !isEditTextEmpty(itemPriceET)

                ) {
                    String itemName = itemNameET.getText().toString();
                    String itemAge = itemAgeET.getText().toString();
                    String itemColor = itemColorET.getText().toString();
                    String itemMaterial = itemMaterialET.getText().toString();
                    String itemMade = itemMadeET.getText().toString();
                    float itemPrice = Float.parseFloat(itemPriceET.getText().toString());

                    StoreItem item = new StoreItem(
                            itemName,
                            new ItemDescription(
                                    itemAge,
                                    itemColor,
                                    itemMaterial,
                                    itemMade
                            ),
                            itemPrice,
                            itemPicIV.getId()+""
                    );
                    storeItems.add(item);
                    FirebaseDB.getDataReference().child(FirebaseDB.TOYS_CHILD).child(itemName).setValue(item);
                    log(AdminItemsListActivity.class,"add item successfully");
                    logToFireBase(this,"add item successfully");
                    toast(this,"add item successfully");
                    refreshLV();
                    dialog.dismiss();
                }
            });


            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        });

    }

    void refreshLV() {
        StoreItemAdminDashAdapter SIADA = new StoreItemAdminDashAdapter(this,R.layout.layout_store_item_admin_dash,storeItems);
        itemsLV.setAdapter(SIADA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            toast(this,imageBitmap.toString());
//            FirebaseST.getStorageRef().child(FirebaseDB.TOYS_CHILD).putFile(data.getData());
        }
    }

}