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
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.deneme.R;
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
    private ImageButton back;

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


        back =findViewById(R.id.back_button);


        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
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

            if(isFavorite){
                saveProductToFavorites(product);
            }


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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    private void addToFavorites(Product product) {

        if (isFavorite) {
            addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp_border));
            addToFavoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.gray));
            Toast.makeText(this, "Ürün favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
            isFavorite = false;

        } else {
            addToFavoritesButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kalp));
            addToFavoritesButton.setColorFilter(ContextCompat.getColor(this, R.color.red));
            Toast.makeText(this, "Ürün favorilere eklendi", Toast.LENGTH_SHORT).show();
            isFavorite = true;

        }


        }

    private void saveProductToFavorites(Product product) {
        AppDatabase db = AppDatabase.getInstance(this);
        FavoriteProductDao favoriteProductDao = db.favoriteProductDao();

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProductId(product.getId());
        favoriteProduct.setProductName(product.getTitle());
        favoriteProduct.setProductImage(product.getThumbnail());
        favoriteProduct.setProductPrice(product.getPrice());

        // Veritabanına kaydetme işlemini asenkron yap
        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteProductDao.insert(favoriteProduct);
        });
        Executors.newSingleThreadExecutor().execute(() -> {
            favoriteProductDao.insert(favoriteProduct);
            // Kaydın başarılı olup olmadığını kontrol et
            List<FavoriteProduct> products = favoriteProductDao.getAllFavoriteProducts();
            Log.d("Database Check", "Favori Ürün Sayısı: " + products.size());
        });

    }


    private void addToCart(Product product, int quantity) {

    }

}



