package com.example.caloriemate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caloriemate.R;
import com.example.caloriemate.model.FoodProduct;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodProduct> foodList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FoodProduct food);
    }

    public FoodAdapter(List<FoodProduct> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodProduct food = foodList.get(position);
        holder.tvFoodName.setText(food.getProductName());
        holder.tvBrand.setText(food.getBrands());

        double calories = food.getNutriments() != null ? food.getNutriments().getCalories() : 0;
        holder.tvCalories.setText((int) calories + " kcal / 100g");

        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.color.soft_green)
                    .into(holder.ivFoodImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(food));
    }

    @Override
    public int getItemCount() { return foodList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvBrand, tvCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }
    }
}