package com.example.deneme.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deneme.R;
import com.example.deneme.data.FavoriteProduct;
import com.example.deneme.data.FavoriteProductDao;
import com.example.deneme.database.AppDatabase;
import com.example.deneme.ui.adapter.FavoriteProductAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteProductAdapter adapter;
    private FavoriteProductDao favoriteProductDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Veritabanı bağlantısını oluştur ve ExecutorService başlat
        favoriteProductDao = AppDatabase.getInstance(this).favoriteProductDao();
        executorService = Executors.newSingleThreadExecutor();

        // Favori ürünleri yükle
        loadFavoriteProducts();

    }

    private void loadFavoriteProducts() {
        executorService.execute(() -> {
            List<FavoriteProduct> favoriteProducts = favoriteProductDao.getAllFavoriteProducts();

            // Eğer liste boş değilse, favori ürünleri göster
            runOnUiThread(() -> {
                if (favoriteProducts != null && !favoriteProducts.isEmpty()) {
                    adapter = new FavoriteProductAdapter(this, favoriteProducts);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Favori ürün bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ExecutorService'i kapat
        executorService.shutdown();
    }
}
