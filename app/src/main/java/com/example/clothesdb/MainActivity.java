package com.example.clothesdb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private static final int ADD_EDIT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        Button btnAddProduct = findViewById(R.id.btn_add_product);
        recyclerView = findViewById(R.id.recycler_view_products);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = Paper.book().read("products", new ArrayList<>());
        adapter = new ProductAdapter(this, productList, this::deleteProduct, this::editProduct);
        recyclerView.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            productList = Paper.book().read("products", new ArrayList<>());
            adapter.updateProducts(productList);
        }
    }

    private void deleteProduct(String productId) {
        productList.removeIf(product -> product.getId().equals(productId));
        Paper.book().write("products", productList);
        adapter.updateProducts(productList);
    }

    private void editProduct(Product product) {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);

        intent.putExtra("productId", product.getId());
        intent.putExtra("productName", product.getName());
        intent.putExtra("productDescription", product.getDescription());
        intent.putExtra("productPrice", product.getPrice());
        intent.putExtra("productImagePath", product.getImagePath());

        startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
    }
}
