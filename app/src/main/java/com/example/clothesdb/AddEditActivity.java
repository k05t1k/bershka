package com.example.clothesdb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class AddEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;
    private EditText editName, editDescription, editPrice;
    private ImageView imagePreview;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        Paper.init(this);

        editName = findViewById(R.id.edit_product_name);
        editDescription = findViewById(R.id.edit_product_description);
        editPrice = findViewById(R.id.edit_product_price);
        imagePreview = findViewById(R.id.image_preview);
        Button btnChooseImage = findViewById(R.id.btn_choose_image);
        Button btnSaveProduct = findViewById(R.id.btn_save_product);

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSaveProduct.setOnClickListener(v -> {
            if (validateInput()) {
                saveProduct();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        if (getIntent().hasExtra("productId")) {
            String productId = getIntent().getStringExtra("productId");
            loadProductData(productId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imagePath = selectedImageUri.toString();
                imagePreview.setImageURI(selectedImageUri);
            }
        }
    }

    private boolean validateInput() {
        if (editName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter name (brand bershka)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editPrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter price (like bershka)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imagePath == null) {
            Toast.makeText(this, "Choose photo (like bershka)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveProduct() {
        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        double price = Double.parseDouble(editPrice.getText().toString());

        List<Product> productList = Paper.book().read("products", new ArrayList<>());

        if (getIntent().hasExtra("productId")) {
            String productId = getIntent().getStringExtra("productId");

            for (Product product : productList) {
                if (product.getId().equals(productId)) {
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setImagePath(imagePath);
                    break;
                }
            }
        } else {
            String id = UUID.randomUUID().toString();
            Product product = new Product(id, name, description, price, imagePath);
            productList.add(product);
        }

        Paper.book().write("products", productList);
        Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
    }

    private void loadProductData(String productId) {
        List<Product> productList = Paper.book().read("products", new ArrayList<>());

        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                editName.setText(product.getName());
                editDescription.setText(product.getDescription());
                editPrice.setText(String.valueOf(product.getPrice()));

                imagePath = product.getImagePath();
                Glide.with(this).load(Uri.parse(imagePath)).into(imagePreview);
            }
        }
    }

}
