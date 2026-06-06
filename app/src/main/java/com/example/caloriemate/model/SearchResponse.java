package com.example.caloriemate.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {

    @SerializedName("products")
    private List<FoodProduct> products;

    public List<FoodProduct> getProducts() {
        return products;
    }
}