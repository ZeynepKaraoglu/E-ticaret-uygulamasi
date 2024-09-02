package com.example.deneme.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deneme.R;
import com.example.deneme.data.FavoriteProduct;

import java.util.List;

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductViewHolder> {

    private Context context;
    private List<FavoriteProduct> favoriteProductList;
    private OnItemClickListener listener;

    public FavoriteProductAdapter(Context context, List<FavoriteProduct> favoriteProductList) {
        this.context = context;
        this.favoriteProductList = favoriteProductList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(FavoriteProduct product);
    }

    @NonNull
    @Override
    public FavoriteProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_product, parent, false);
        return new FavoriteProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductViewHolder holder, int position) {
        FavoriteProduct product = favoriteProductList.get(position);

        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText("$" + product.getProductPrice());

        Glide.with(context)
                .load(product.getProductImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.productImageView);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return favoriteProductList.size();
    }

    public static class FavoriteProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;

        public FavoriteProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
        }
    }
}
