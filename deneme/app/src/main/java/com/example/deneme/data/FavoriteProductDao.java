package com.example.deneme.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteProductDao {


    @Insert
    void insert(FavoriteProduct favoriteProduct);

    @Delete
    void delete(FavoriteProduct favoriteProduct);

    @Update
    void update(FavoriteProduct favoriteProduct);

    @Query("SELECT * FROM favorite_products")
    List<FavoriteProduct> getAllFavoriteProducts();

    @Query("SELECT * FROM favorite_products WHERE productId = :productId LIMIT 1")
    FavoriteProduct getFavoriteProductById(int productId);

}
