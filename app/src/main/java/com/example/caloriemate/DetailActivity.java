package com.example.caloriemate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.caloriemate.database.DatabaseHelper;
import com.example.caloriemate.model.FoodLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivFoodDetail;
    private TextView tvFoodName, tvBrand, tvCalories, tvProtein, tvFat, tvCarbs;
    private Button btnAddToLog;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private String foodName, foodBrand, foodImage;
    private double foodCalories, foodProtein, foodFat, foodCarbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("caloriemate_prefs", MODE_PRIVATE);

        ivFoodDetail = findViewById(R.id.ivFoodDetail);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvBrand = findViewById(R.id.tvBrand);
        tvCalories = findViewById(R.id.tvCalories);
        tvProtein = findViewById(R.id.tvProtein);
        tvFat = findViewById(R.id.tvFat);
        tvCarbs = findViewById(R.id.tvCarbs);
        btnAddToLog = findViewById(R.id.btnAddToLog);

        // Get data from Intent
        foodName = getIntent().getStringExtra("food_name");
        foodBrand = getIntent().getStringExtra("food_brand");
        foodImage = getIntent().getStringExtra("food_image");
        foodCalories = getIntent().getDoubleExtra("food_calories", 0);
        foodProtein = getIntent().getDoubleExtra("food_protein", 0);
        foodFat = getIntent().getDoubleExtra("food_fat", 0);
        foodCarbs = getIntent().getDoubleExtra("food_carbs", 0);

        // Set data to views
        tvFoodName.setText(foodName != null ? foodName : "Unknown");
        tvBrand.setText(foodBrand != null && !foodBrand.isEmpty() ? foodBrand : "");
        tvCalories.setText((int) foodCalories + " kcal");
        tvProtein.setText((int) foodProtein + " g");
        tvFat.setText((int) foodFat + " g");
        tvCarbs.setText((int) foodCarbs + " g");

        if (foodImage != null && !foodImage.isEmpty()) {
            Glide.with(this)
                    .load(foodImage)
                    .placeholder(R.color.soft_green)
                    .into(ivFoodDetail);
        }

        btnAddToLog.setOnClickListener(v -> addToLog());
    }

    private void addToLog() {
        int userId = prefs.getInt("user_id", -1);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        FoodLog log = new FoodLog(userId, foodName, foodCalories, foodProtein, foodFat, foodCarbs, today, time);

        btnAddToLog.setEnabled(false);

        executor.execute(() -> {
            boolean success = dbHelper.addFoodLog(log);
            handler.post(() -> {
                btnAddToLog.setEnabled(true);
                if (success) {
                    Toast.makeText(this, foodName + " added to today's log!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add food. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}