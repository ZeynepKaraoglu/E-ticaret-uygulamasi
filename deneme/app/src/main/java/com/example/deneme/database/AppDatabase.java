package com.example.deneme.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.deneme.data.FavoriteProduct;
import com.example.deneme.data.FavoriteProductDao;

@Database(entities = {FavoriteProduct.class}, version = 1 , exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract FavoriteProductDao favoriteProductDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "favorite_products_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
