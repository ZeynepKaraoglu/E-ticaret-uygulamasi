package com.example.deneme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deneme.R;
import com.example.deneme.data.Category;
import com.example.deneme.data.Product;
import com.example.deneme.data.ProductResponse;
import com.example.deneme.service.ApiService;
import com.example.deneme.service.RetrofitClient;
import com.example.deneme.ui.adapter.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Parcelable recyclerViewState;
    private RecyclerView recyclerView;
    private LinearLayout categoryLinearLayout;

    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredProductList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private TextView noResultsText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noResultsText = findViewById(R.id.no_results_text);
        SearchView searchView = findViewById(R.id.search_view);
        categoryLinearLayout = findViewById(R.id.categoryLinearLayout);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_urun) {
                    scrollToTop();
                    return true;
                } else if (itemId == R.id.navigation_favori) {
                    Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_sepet) {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });



        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadProducts();
        loadCategories();

        productAdapter = new ProductAdapter(this, filteredProductList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(productAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    filteredProductList.clear();
                    filteredProductList.addAll(productList);
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultsText.setVisibility(View.GONE);
                } else {
                    filterProductsByTag(newText);
                }
                productAdapter.notifyDataSetChanged();
                return true;
            }
        });



        if (recyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }

    private void loadProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ProductResponse> call = apiService.getProducts(1000, 0);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body().getProducts();
                    filteredProductList.addAll(productList);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Veriler alınamadı!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Veriler yüklenemedi. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<List<Category>> call = apiService.getCategories(500, 0);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryList.clear();
                    for (Category category : categories) {
                        addCategoryButton(category);
                    }
                    Log.d("Categories", "Kategoriler Yüklendi: " + categoryList);
                } else {
                    Log.d("Categories", "Yanıt Başarısız: " + response.errorBody().toString());
                    Toast.makeText(MainActivity.this, "Kategoriler alınamadı!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d("Categories", "İstek Başarısız: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Kategoriler yüklenemedi. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategoryButton(Category category) {
        Button categoryButton = new Button(this);
        categoryButton.setText(category.getName().toLowerCase());
        categoryButton.setBackgroundResource(R.drawable.category_button_background);
        categoryButton.setTextColor(getResources().getColor(R.color.black));
        categoryButton.setPadding(8, 4, 8, 8);
        categoryButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        categoryButton.setTextSize(15);
        categoryButton.setAllCaps(false);
        categoryButton.setOnClickListener(v -> {
            filterProductsByCategory(category.getSlug());
        });

        categoryLinearLayout.addView(categoryButton);
    }

    private void filterProductsByCategory(String key) {
        filteredProductList.clear();

        for (Product product : productList) {
            if (product.getCategory().equalsIgnoreCase(key)) {
                filteredProductList.add(product);
            }
        }

        if (filteredProductList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        }

        productAdapter.notifyDataSetChanged();
    }


    private void filterProductsByTag(String query) {
        filteredProductList.clear();

        for (Product product : productList) {
            if (product.getTags() != null) {
                for (String tag : product.getTags()) {
                    if (tag.toLowerCase().contains(query.toLowerCase())) {
                        filteredProductList.add(product);
                        break;
                    }
                }
            }
        }

        if (filteredProductList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        }
    }

    private void scrollToTop(){
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0); // RecyclerView'in pozisyonunu sıfırlayın
        }
    }
}
