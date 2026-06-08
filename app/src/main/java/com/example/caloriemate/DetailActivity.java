package com.example.caloriemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        foodName = getIntent().getStringExtra("food_name");
        foodBrand = getIntent().getStringExtra("food_brand");
        foodImage = getIntent().getStringExtra("food_image");
        foodCalories = getIntent().getDoubleExtra("food_calories", 0);
        foodProtein = getIntent().getDoubleExtra("food_protein", 0);
        foodFat = getIntent().getDoubleExtra("food_fat", 0);
        foodCarbs = getIntent().getDoubleExtra("food_carbs", 0);
        boolean fromLog = getIntent().getBooleanExtra("from_log", false);

        tvFoodName.setText(foodName != null ? foodName : "Unknown");
        tvBrand.setText(foodBrand != null && !foodBrand.isEmpty() ? foodBrand : "");
        tvCalories.setText((int) foodCalories + " kcal");
        tvProtein.setText((int) foodProtein + " g");
        tvFat.setText((int) foodFat + " g");
        tvCarbs.setText((int) foodCarbs + " g");

        // Sembunyikan tombol Add to Log kalau dibuka dari Log
        if (fromLog) {
            btnAddToLog.setVisibility(View.GONE);
        }

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
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("navigate_to", "home");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add food. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}