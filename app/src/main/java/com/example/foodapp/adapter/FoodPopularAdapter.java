package com.example.foodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvFoodPopularBinding;
import com.example.foodapp.model.Food;

import java.util.List;

public class FoodPopularAdapter extends RecyclerView.Adapter<FoodPopularAdapter.MyViewHolder> {
    public interface IFoodPopular {
        void onClick(Food food);
    }

    private Context context;
    private List<Food> lists;
    private IFoodPopular iFoodPopular;

    public FoodPopularAdapter(Context context, List<Food> lists, IFoodPopular iFoodPopular) {
        this.context = context;
        this.lists = lists;
        this.iFoodPopular = iFoodPopular;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvFoodPopularBinding binding = ItemRcvFoodPopularBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Food food = lists.get(position);
        if(food == null){
            return;
        }

        Glide.with(context).load(food.getImagePath()).into(holder.binding.imgFood);
        holder.binding.txtTitle.setText(food.getTitle());
        holder.binding.txtPrice.setText(food.getPrice()+"$");
        holder.binding.txtStar.setText(food.getStar() + "/5");
        holder.binding.txtTimeValue.setText(food.getTimeValue() + "min");

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iFoodPopular.onClick(food);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemRcvFoodPopularBinding binding;

        public MyViewHolder(@NonNull ItemRcvFoodPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
