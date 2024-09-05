package com.example.deneme.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deneme.R;
import com.example.deneme.data.FavoriteProduct;
import com.example.deneme.data.FavoriteProductDao;
import com.example.deneme.database.AppDatabase;
import com.example.deneme.ui.adapter.FavoriteProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteProductAdapter favoriteProductAdapter;
    private ExecutorService executorService;
    private Drawable deleteIcon;
    private ColorDrawable background;
    private TextView noFavoritesTextView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noFavoritesTextView = findViewById(R.id.no_favorites_text);

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



      BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_urun) {
                    Intent mainIntent = new Intent(FavoritesActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(mainIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_favori) {
                    Intent intent = new Intent(FavoritesActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_sepet) {
                    Intent mainIntent = new Intent(FavoritesActivity.this, CartActivity.class);
                    startActivity(mainIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        executorService = Executors.newSingleThreadExecutor();

        loadFavoriteProducts();

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }



   private void loadFavoriteProducts() {
       Executors.newSingleThreadExecutor().execute(() -> {
           AppDatabase db = AppDatabase.getInstance(FavoritesActivity.this);
           FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

           List<FavoriteProduct> favoriteProductList = favoriteProductDao.getAllFavoriteProducts();

           runOnUiThread(() -> {
               if (favoriteProductList.isEmpty()) {
                  // Toast.makeText(FavoritesActivity.this, "Hiç favori ürün yok", Toast.LENGTH_SHORT).show();
                   showNoFavoritesMessage();
               } else {
                   favoriteProductAdapter = new FavoriteProductAdapter(FavoritesActivity.this, favoriteProductList, new FavoriteProductAdapter.OnItemClickListener() {
                       @Override
                       public void onItemClick(FavoriteProduct favoriteProduct) {
                           Intent intent = new Intent(FavoritesActivity.this, ProductDetailActivity.class);
                           intent.putExtra("product", (CharSequence) favoriteProduct);
                           startActivity(intent);
                       }
                   });
                   recyclerView.setAdapter(favoriteProductAdapter);

               }
           });
       });
   }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(FavoritesActivity.this);
                FavoriteProductDao favoriteProductDao = db.favoriteProductDao();
                List<FavoriteProduct> favoriteProductList = favoriteProductDao.getAllFavoriteProducts();

                if (favoriteProductList != null && position >= 0 && position<favoriteProductList.size()) {
                    FavoriteProduct favoriteProduct = favoriteProductList.get(position);
                    favoriteProductDao.delete(favoriteProduct);

                        runOnUiThread(() -> {
                            favoriteProductList.remove(position);
                            favoriteProductAdapter.notifyItemRemoved(position);
                            loadFavoriteProducts();

                            if (favoriteProductList.isEmpty()) {
                                showNoFavoritesMessage();
                            }
                        });

                } else {
                    favoriteProductAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

            if (dX < 0) {
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else {
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            deleteIcon.draw(c);
        }
    };

    private void showNoFavoritesMessage() {
        recyclerView.setVisibility(View.GONE);
        noFavoritesTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
