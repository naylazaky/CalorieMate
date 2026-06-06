package com.example.caloriemate.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.caloriemate.LoginActivity;
import com.example.caloriemate.R;
import com.example.caloriemate.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfileCalorieTarget;
    private SwitchCompat switchDarkMode;
    private Button btnEditTarget, btnLogout;

    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity().getSharedPreferences("caloriemate_prefs", requireActivity().MODE_PRIVATE);
        dbHelper = new DatabaseHelper(requireContext());

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileCalorieTarget = view.findViewById(R.id.tvProfileCalorieTarget);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnEditTarget = view.findViewById(R.id.btnEditTarget);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadProfileData();
        setupDarkModeSwitch();

        btnEditTarget.setOnClickListener(v -> showEditTargetDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadProfileData() {
        String name = prefs.getString("user_name", "");
        String email = prefs.getString("user_email", "");
        int target = prefs.getInt("calorie_target", 2000);

        tvProfileName.setText(name);
        tvProfileEmail.setText(email);
        tvProfileCalorieTarget.setText("Daily Target: " + target + " kcal");
    }

    private void setupDarkModeSwitch() {
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switchDarkMode.setChecked(themeMode == AppCompatDelegate.MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            prefs.edit().putInt("theme_mode", mode).apply();
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }

    private void showEditTargetDialog() {
        TextInputLayout inputLayout = new TextInputLayout(requireContext());
        TextInputEditText etNewTarget = new TextInputEditText(requireContext());
        etNewTarget.setHint(getString(R.string.hint_new_target));
        etNewTarget.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputLayout.setPadding(48, 16, 48, 0);
        inputLayout.addView(etNewTarget);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.btn_edit_target))
                .setView(inputLayout)
                .setPositiveButton(getString(R.string.btn_save), (dialog, which) -> {
                    String input = etNewTarget.getText().toString().trim();
                    if (!input.isEmpty()) {
                        int newTarget = Integer.parseInt(input);
                        int userId = prefs.getInt("user_id", -1);

                        executor.execute(() -> {
                            dbHelper.updateCalorieTarget(userId, newTarget);
                            handler.post(() -> {
                                prefs.edit().putInt("calorie_target", newTarget).apply();
                                tvProfileCalorieTarget.setText("Daily Target: " + newTarget + " kcal");
                                Toast.makeText(requireContext(), "Calorie target updated!", Toast.LENGTH_SHORT).show();
                            });
                        });
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}