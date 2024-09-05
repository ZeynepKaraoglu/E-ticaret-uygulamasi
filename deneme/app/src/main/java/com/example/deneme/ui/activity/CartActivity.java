package com.example.deneme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deneme.R;
import com.example.deneme.data.CartItem;
import com.example.deneme.data.CartItemDao;
import com.example.deneme.database.AppDatabase;
import com.example.deneme.ui.adapter.CartProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartProductAdapter cartProductAdapter;
    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.back_button);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        loadCartItems();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_urun) {
                    Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(mainIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_favori) {
                    Intent intent = new Intent(CartActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_sepet) {
                    return true;
                } else {
                    return false;
                }
            }
            });
    }

    private void loadCartItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(CartActivity.this);
            CartItemDao cartItemDao = db.cartItemDao();

            cartItemList = cartItemDao.getAllCartItems();

            runOnUiThread(() -> {
                if (cartItemList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Sepetiniz boş", Toast.LENGTH_SHORT).show();
                } else {
                    cartProductAdapter = new CartProductAdapter(CartActivity.this, cartItemList);
                    recyclerView.setAdapter(cartProductAdapter);
                }
            });
        });
    }


}
