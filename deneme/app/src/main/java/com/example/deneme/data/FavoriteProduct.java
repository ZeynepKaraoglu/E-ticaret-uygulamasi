package com.example.deneme.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "favorite_products")
public class FavoriteProduct {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int productId;
    private String productName;
    private String productImage;
    private double productPrice;

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getProductId() {return productId;}
    public void setProductId(int productId) {this.productId = productId;}

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public String getProductImage() {return productImage;}
    public void setProductImage(String productImage) {this.productImage = productImage;}

    public double getProductPrice() {return productPrice;}
    public void setProductPrice(double productPrice) {this.productPrice = productPrice;}
}
