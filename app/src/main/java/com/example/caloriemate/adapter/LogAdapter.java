package com.example.caloriemate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriemate.R;
import com.example.caloriemate.model.FoodLog;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private List<FoodLog> logList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(FoodLog log, int position);
    }

    public LogAdapter(List<FoodLog> logList, OnDeleteClickListener listener) {
        this.logList = logList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodLog log = logList.get(position);
        holder.tvLogFoodName.setText(log.getFoodName());
        holder.tvLogCalories.setText((int) log.getCalories() + " kcal");
        holder.tvLogTime.setText(log.getTimeLogged());

        holder.btnDeleteLog.setOnClickListener(v ->
                listener.onDeleteClick(log, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return logList.size(); }

    public void removeItem(int position) {
        logList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogFoodName, tvLogCalories, tvLogTime;
        Button btnDeleteLog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogFoodName = itemView.findViewById(R.id.tvLogFoodName);
            tvLogCalories = itemView.findViewById(R.id.tvLogCalories);
            tvLogTime = itemView.findViewById(R.id.tvLogTime);
            btnDeleteLog = itemView.findViewById(R.id.btnDeleteLog);
        }
    }
}