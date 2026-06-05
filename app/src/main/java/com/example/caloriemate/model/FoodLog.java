package com.example.caloriemate.model;

public class FoodLog {
    private int id;
    private int userId;
    private String foodName;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private String dateLogged;
    private String timeLogged;

    public FoodLog() {}

    public FoodLog(int userId, String foodName, double calories, double protein, double fat, double carbs, String dateLogged, String timeLogged) {
        this.userId = userId;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.dateLogged = dateLogged;
        this.timeLogged = timeLogged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public String getDateLogged() {
        return dateLogged;
    }

    public void setDateLogged(String dateLogged) {
        this.dateLogged = dateLogged;
    }

    public String getTimeLogged() {
        return timeLogged;
    }

    public void setTimeLogged(String timeLogged) {
        this.timeLogged = timeLogged;
    }
}