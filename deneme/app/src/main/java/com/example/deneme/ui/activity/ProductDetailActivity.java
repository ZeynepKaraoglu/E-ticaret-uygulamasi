package com.example.deneme.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.deneme.R;
import com.example.deneme.data.CartItem;
import com.example.deneme.data.CartItemDao;
import com.example.deneme.data.FavoriteProduct;
import com.example.deneme.data.FavoriteProductDao;
import com.example.deneme.data.Product;
import com.example.deneme.database.AppDatabase;
import com.example.deneme.ui.adapter.ImagePagerAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    //private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productCategoryTextView;
    private TextView productDescriptionTextView;
    private RatingBar productRatingBar;
    private EditText quantityEditText;
    private ImageButton addToCartButton;
    private ImageButton addToFavoritesButton;
    private TextView productPrice;

    private Product product;
    private boolean isFavorite = false;

    private ViewPager2 viewPager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

      //  productImageView = findViewById(R.id.imageView);
        viewPager = findViewById(R.id.imageView);
        productNameTextView = findViewById(R.id.product_name);
        productCategoryTextView = findViewById(R.id.category_name);
        productDescriptionTextView = findViewById(R.id.description);
        productRatingBar = findViewById(R.id.ratingBar);
        quantityEditText = findViewById(R.id.adet);
        addToCartButton = findViewById(R.id.sepet);
        addToFavoritesButton = findViewById(R.id.favori);
        productPrice = findViewById(R.id.product_price);


        Toolbar toolbar = findViewById(R.id.back_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        product = (Product) getIntent().getSerializableExtra("product");


        if (product != null) {
            checkFavoriteStatus();

            productNameTextView.setText(product.getTitle());
            productCategoryTextView.setText(product.getCategory());
            productDescriptionTextView.setText(product.getDescription());
            productPrice.setText(String.format("%.2f $", product.getPrice()));
            productRatingBar.setRating((float) product.getRating());
            Log.d("Product Rating", "Rating: " + product.getRating());


            List<String> images = product.getImages();
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
            viewPager.setAdapter(adapter);


            addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToFavorites(product);

                }
            });


            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateQuantity()) {
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
                           addToCart(product, quantity);
                    }
                }
            });


        }

      /*  back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
    }


    public void onBackPressed(){
        super.onBackPressed();
    }

    private boolean validateQuantity() {
        String quantityText = quantityEditText.getText().toString().trim();
        if (quantityText.isEmpty()) {
            quantityEditText.setError("Lütfen adet giriniz");
            return false;
        }

        int quantity = Integer.parseInt(quantityText);
        if (quantity <= 0) {
            quantityEditText.setError("Adet 0'dan büyük olmalıdır");
            return false;
        }

        return true;
    }
/*
    private void addToFavorites(Product product) {

        if (isFavorite) {
            addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp_border));
            addToFavoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.gray));
            Toast.makeText(this, "Ürün favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
            isFavorite = false;

        } else {
            addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp));
            Toast.makeText(this, "Ürün favorilere eklendi", Toast.LENGTH_SHORT).show();
            isFavorite = true;

        }


        }

    private void saveProductToFavorites(Product product) {
        AppDatabase db = AppDatabase.getInstance(this);
        Log.d("DatabaseCheck", "Veritabanı başarıyla oluşturuldu veya mevcut.");
        FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProductId(product.getId());
        favoriteProduct.setProductName(product.getTitle());
        favoriteProduct.setProductImage(product.getThumbnail());
        favoriteProduct.setProductPrice(product.getPrice());

        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteProductDao.insert(favoriteProduct);
            // Kaydın başarılı olup olmadığını kontrol et
            List<FavoriteProduct> products = favoriteProductDao.getAllFavoriteProducts();
            Log.d("Database Check", "Favori Ürün Sayısı: " + products.size());
        });

    }*/

    private void checkFavoriteStatus() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            FavoriteProductDao favoriteProductDao = db.favoriteProductDao();
            FavoriteProduct favoriteProduct = favoriteProductDao.getFavoriteProductById(product.getId());

            runOnUiThread(() -> {
                if (favoriteProduct != null) {
                    isFavorite = true;
                    addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp));
                } else {
                    isFavorite = false;
                    addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp_border));
                }
            });
        });
    }

    private void addToFavorites(Product product) {
        AppDatabase db = AppDatabase.getInstance(this);
        FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            FavoriteProduct existingFavorite = favoriteProductDao.getFavoriteProductById(product.getId());
            runOnUiThread(() -> {
                if (existingFavorite != null) {
                    removeFromFavorites(existingFavorite);
                } else {
                    saveProductToFavorites(product);
                }
            });
        });
    }

    private void removeFromFavorites(FavoriteProduct favoriteProduct) {
        AppDatabase db = AppDatabase.getInstance(this);
        FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteProductDao.delete(favoriteProduct);
            runOnUiThread(() -> {
                addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp_border));
                addToFavoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.gray));
                Toast.makeText(this, "Ürün favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                isFavorite = false;
            });
        });
    }

    private void saveProductToFavorites(Product product) {
        AppDatabase db = AppDatabase.getInstance(this);
        FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProductId(product.getId());
        favoriteProduct.setProductName(product.getTitle());
        favoriteProduct.setProductImage(product.getThumbnail());
        favoriteProduct.setProductPrice(product.getPrice());
        // favoriteProduct.setRating(product.getRating());

        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteProductDao.insert(favoriteProduct);
            runOnUiThread(() -> {
                addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp));
                addToFavoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.red));
                Toast.makeText(this, "Ürün favorilere eklendi", Toast.LENGTH_SHORT).show();
                isFavorite = true;
            });
        });
    }


    private void addToCart(Product product, int quantity) {
        AppDatabase db = AppDatabase.getInstance(this);
        CartItemDao cartItemDao = db.cartItemDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<CartItem> cartItemList = cartItemDao.getAllCartItems();

            CartItem existingCartItem = null;
            for (CartItem item : cartItemList) {
                if (item.getProductId() == product.getId()) {
                    existingCartItem = item;
                    break;
                }
            }

            if (existingCartItem != null) {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
                cartItemDao.update(existingCartItem);
                runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Sepette güncellendi", Toast.LENGTH_SHORT).show());
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setProductId(product.getId());
                cartItem.setProductName(product.getTitle());
                cartItem.setProductImage(product.getThumbnail());
                cartItem.setProductPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItemDao.insert(cartItem);
                runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Sepete eklendi", Toast.LENGTH_SHORT).show());
            }
        });
    }

}



