package com.example.caloriemate.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriemate.DetailActivity;
import com.example.caloriemate.R;
import com.example.caloriemate.adapter.LogAdapter;
import com.example.caloriemate.database.DatabaseHelper;
import com.example.caloriemate.model.FoodLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogFragment extends Fragment {

    private RecyclerView rvLogList;
    private TextView tvEmptyLog;
    private LogAdapter adapter;
    private List<FoodLog> logList = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        prefs = requireActivity().getSharedPreferences("caloriemate_prefs", requireActivity().MODE_PRIVATE);

        rvLogList = view.findViewById(R.id.rvLogList);
        tvEmptyLog = view.findViewById(R.id.tvEmptyLog);

        adapter = new LogAdapter(logList,
                (log, position) -> deleteLog(log, position),
                log -> openDetail(log));

        rvLogList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLogList.setAdapter(adapter);

        loadLogs();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs();
    }

    private void loadLogs() {
        int userId = prefs.getInt("user_id", -1);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        executor.execute(() -> {
            List<FoodLog> logs = dbHelper.getFoodLogsByDate(userId, today);
            handler.post(() -> {
                logList.clear();
                logList.addAll(logs);
                adapter.notifyDataSetChanged();

                if (logList.isEmpty()) {
                    tvEmptyLog.setVisibility(View.VISIBLE);
                    rvLogList.setVisibility(View.GONE);
                } else {
                    tvEmptyLog.setVisibility(View.GONE);
                    rvLogList.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void deleteLog(FoodLog log, int position) {
        executor.execute(() -> {
            dbHelper.deleteFoodLog(log.getId());
            handler.post(() -> {
                adapter.removeItem(position);
                if (logList.isEmpty()) {
                    tvEmptyLog.setVisibility(View.VISIBLE);
                    rvLogList.setVisibility(View.GONE);
                }
            });
        });
    }

    private void openDetail(FoodLog log) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("food_name", log.getFoodName());
        intent.putExtra("food_brand", "");
        intent.putExtra("food_image", "");
        intent.putExtra("food_calories", log.getCalories());
        intent.putExtra("food_protein", log.getProtein());
        intent.putExtra("food_fat", log.getFat());
        intent.putExtra("food_carbs", log.getCarbs());
        intent.putExtra("from_log", true);
        startActivity(intent);
    }
}