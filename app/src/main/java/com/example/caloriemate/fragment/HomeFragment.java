package com.example.caloriemate.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.caloriemate.R;
import com.example.caloriemate.database.DatabaseHelper;
import com.example.caloriemate.model.FoodLog;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvCaloriesProgress;
    private TextView tvTotalCalories, tvTotalProtein, tvTotalFat, tvTotalCarbs;
    private CircularProgressIndicator progressCalories;
    private Button btnAddFood;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        prefs = requireActivity().getSharedPreferences("caloriemate_prefs", requireActivity().MODE_PRIVATE);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvCaloriesProgress = view.findViewById(R.id.tvCaloriesProgress);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvTotalProtein = view.findViewById(R.id.tvTotalProtein);
        tvTotalFat = view.findViewById(R.id.tvTotalFat);
        tvTotalCarbs = view.findViewById(R.id.tvTotalCarbs);
        progressCalories = view.findViewById(R.id.progressCalories);
        btnAddFood = view.findViewById(R.id.btnAddFood);

        String userName = prefs.getString("user_name", "User");
        tvGreeting.setText(getString(R.string.greeting, userName));

        btnAddFood.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.searchFragment));

        loadTodayData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodayData();
    }

    private void loadTodayData() {
        int userId = prefs.getInt("user_id", -1);
        int calorieTarget = prefs.getInt("calorie_target", 2000);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        executor.execute(() -> {
            List<FoodLog> logs = dbHelper.getFoodLogsByDate(userId, today);

            double totalCalories = 0, totalProtein = 0, totalFat = 0, totalCarbs = 0;
            for (FoodLog log : logs) {
                totalCalories += log.getCalories();
                totalProtein += log.getProtein();
                totalFat += log.getFat();
                totalCarbs += log.getCarbs();
            }

            double finalCalories = totalCalories;
            double finalProtein = totalProtein;
            double finalFat = totalFat;
            double finalCarbs = totalCarbs;

            handler.post(() -> {
                int progress = (int) ((finalCalories / calorieTarget) * 100);
                progress = Math.min(progress, 100);

                progressCalories.setMax(100);
                progressCalories.setProgress(progress);

                tvCaloriesProgress.setText((int) finalCalories + " / " + calorieTarget + " kcal");
                tvTotalCalories.setText(String.valueOf((int) finalCalories));
                tvTotalProtein.setText((int) finalProtein + "g");
                tvTotalFat.setText((int) finalFat + "g");
                tvTotalCarbs.setText((int) finalCarbs + "g");
            });
        });
    }
}