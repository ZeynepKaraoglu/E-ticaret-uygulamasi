package com.example.deneme.service;

import com.example.deneme.data.Category;
import com.example.deneme.data.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("products")
    Call<ProductResponse> getProducts(@Query("limit") int limit,
                                      @Query("offset") int offset );
    @GET("products/categories")
    Call<List<Category>> getCategories(@Query("limit") int limit,
                                       @Query("offset") int offset );
}
