package com.example.deneme.data;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private int id;
    private String title;
    private String description;
    private String thumbnail;
    private double price;
    private String category;
    private double rating;
    private List<String> tags ;
    private List<String> images;


    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getThumbnail() { return thumbnail; }

    public double getPrice() { return price; }

    public String getCategory() { return category; }

    public List<String> getTags() { return tags; }

    public double getRating() {return rating; }

    public List<String> getImages() {return images;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

