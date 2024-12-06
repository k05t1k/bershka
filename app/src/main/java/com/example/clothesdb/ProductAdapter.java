package com.example.clothesdb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private final OnDeleteClickListener deleteClickListener;
    private final OnItemClickListener itemClickListener;

    public ProductAdapter(Context context, List<Product> productList,
                          OnDeleteClickListener deleteClickListener, OnItemClickListener itemClickListener) {
        this.context = context;
        this.productList = productList;
        this.deleteClickListener = deleteClickListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public interface OnDeleteClickListener {
        void onDelete(String productId);
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;
        ImageButton deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            deleteButton = itemView.findViewById(R.id.btn_delete_product);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(String.format("Price: $%.2f", product.getPrice()));
            Glide.with(context).load(Uri.parse(product.getImagePath())).into(productImage);

            deleteButton.setOnClickListener(v -> deleteClickListener.onDelete(product.getId()));
            itemView.setOnClickListener(v -> itemClickListener.onItemClick(product));

        }
    }
}



