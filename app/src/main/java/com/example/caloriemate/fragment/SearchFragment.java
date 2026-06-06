package com.example.caloriemate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriemate.DetailActivity;
import com.example.caloriemate.R;
import com.example.caloriemate.adapter.FoodAdapter;
import com.example.caloriemate.model.FoodProduct;
import com.example.caloriemate.model.SearchResponse;
import com.example.caloriemate.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private TextInputEditText etSearch;
    private Button btnSearch, btnRetry;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvError;
    private RecyclerView rvFoodList;
    private FoodAdapter adapter;
    private List<FoodProduct> foodList = new ArrayList<>();
    private String lastQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnRetry = view.findViewById(R.id.btnRetry);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvError = view.findViewById(R.id.tvError);
        rvFoodList = view.findViewById(R.id.rvFoodList);

        adapter = new FoodAdapter(foodList, food -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("food_name", food.getProductName());
            intent.putExtra("food_brand", food.getBrands());
            intent.putExtra("food_image", food.getImageUrl());
            if (food.getNutriments() != null) {
                intent.putExtra("food_calories", food.getNutriments().getCalories());
                intent.putExtra("food_protein", food.getNutriments().getProteins());
                intent.putExtra("food_fat", food.getNutriments().getFat());
                intent.putExtra("food_carbs", food.getNutriments().getCarbohydrates());
            }
            startActivity(intent);
        });

        rvFoodList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoodList.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                lastQuery = query;
                searchFood(query);
            }
        });

        btnRetry.setOnClickListener(v -> {
            if (!lastQuery.isEmpty()) searchFood(lastQuery);
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    lastQuery = query;
                    searchFood(query);
                }
                return true;
            }
            return false;
        });
    }

    private void searchFood(String query) {
        showLoading();

        RetrofitClient.getInstance().getApiService()
                .searchFood(query, 1, "process", 1, 20)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            List<FoodProduct> products = response.body().getProducts();
                            if (products != null && !products.isEmpty()) {
                                foodList.clear();
                                foodList.addAll(products);
                                adapter.notifyDataSetChanged();
                                showResults();
                            } else {
                                showEmpty();
                            }
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        showError();
                    }
                });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvFoodList.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void showResults() {
        progressBar.setVisibility(View.GONE);
        rvFoodList.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        rvFoodList.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        rvFoodList.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
    }
}