package com.example.foodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvFoodPopularBinding;
import com.example.foodapp.databinding.ItemRcvListFoodBinding;
import com.example.foodapp.model.Food;

import java.util.List;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.MyViewHolder> {
    public interface IListFood {
        void onClick(Food food);
    }

    private Context context;
    private List<Food> lists;
    private IListFood iListFood;

    public ListFoodAdapter(Context context, List<Food> lists, IListFood iListFood) {
        this.context = context;
        this.lists = lists;
        this.iListFood  = iListFood;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListFoodBinding binding = ItemRcvListFoodBinding.inflate(inflater, parent, false);
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
                iListFood.onClick(food);
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
        private ItemRcvListFoodBinding binding;

        public MyViewHolder(@NonNull ItemRcvListFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
