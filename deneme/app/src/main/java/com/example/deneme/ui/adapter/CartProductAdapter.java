package com.example.deneme.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deneme.R;
import com.example.deneme.data.CartItem;
import com.example.deneme.database.AppDatabase;

import java.util.List;
import java.util.concurrent.Executors;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartProductAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.productPriceTextView.setText("$" + cartItem.getProductPrice());
        holder.productQuantityTextView.setText("  " + cartItem.getQuantity());


        Glide.with(context)
                .load(cartItem.getProductImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.productImageView);


        holder.increaseButton.setOnClickListener(view -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            updateCartItem(cartItem);
        });


        holder.decreaseButton.setOnClickListener(view -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                updateCartItem(cartItem);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            deleteCartItem(cartItem);
        });
    }


    private void updateCartItem(CartItem cartItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.cartItemDao().update(cartItem);
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    notifyDataSetChanged();
                });
            }
        });
    }

    private void deleteCartItem(CartItem cartItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.cartItemDao().delete(cartItem);
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    cartItemList.remove(cartItem);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Ürün sepetten silindi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView increaseButton;
        ImageView decreaseButton;
        ImageView deleteButton;
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
            productQuantityTextView = itemView.findViewById(R.id.product_quantity);
            increaseButton = itemView.findViewById(R.id.button_increase);
            decreaseButton = itemView.findViewById(R.id.button_decrease);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
