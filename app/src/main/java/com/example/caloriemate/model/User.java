package com.example.caloriemate.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private int calorieTarget;

    public User() {}

    public User(String name, String email, String password, int calorieTarget) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.calorieTarget = calorieTarget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(int calorieTarget) {
        this.calorieTarget = calorieTarget;
    }
}