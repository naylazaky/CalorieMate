package com.example.caloriemate.model;

import com.google.gson.annotations.SerializedName;

public class FoodProduct {

    @SerializedName("product_name")
    private String productName;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("brands")
    private String brands;

    @SerializedName("nutriments")
    private Nutriments nutriments;

    public String getProductName() {
        return productName != null ? productName : "Unknown";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBrands() {
        return brands != null ? brands : "";
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public static class Nutriments {
        @SerializedName("energy-kcal_100g")
        private double calories;

        @SerializedName("proteins_100g")
        private double proteins;

        @SerializedName("fat_100g")
        private double fat;

        @SerializedName("carbohydrates_100g")
        private double carbohydrates;

        public double getCalories() {
            return calories;
        }

        public double getProteins() {
            return proteins;
        }

        public double getFat() {
            return fat;
        }

        public double getCarbohydrates() {
            return carbohydrates;
        }
    }
}