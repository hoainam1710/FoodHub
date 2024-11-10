package com.example.foodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.FragmentFavoriteBinding;
import com.example.foodapp.databinding.ItemRcvListFavoriteBinding;
import com.example.foodapp.model.Food;

import java.net.PortUnreachableException;
import java.util.List;

public class ListFavoriteAdapter extends RecyclerView.Adapter<ListFavoriteAdapter.MyViewHolder> {
    public interface IFoodFavorite{
        void onClick(Food food);
        void deleteFood(Food food);
        void addToCart(Food food);
    }
    private List<Food> lists;
    private Context context;
    private IFoodFavorite iFoodFavorite;

    public ListFavoriteAdapter(Context context, List<Food> lists, IFoodFavorite iFoodFavorite) {
        this.context = context;
        this.lists = lists;
        this.iFoodFavorite = iFoodFavorite;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListFavoriteBinding binding = ItemRcvListFavoriteBinding.inflate(inflater, parent, false);
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
        holder.binding.txtPrice.setText("$"+ food.getPrice());

        holder.binding.imgDelete.setOnClickListener(v -> iFoodFavorite.deleteFood(food));
        holder.binding.imgAddToCart.setOnClickListener(v -> iFoodFavorite.addToCart(food));
        holder.binding.getRoot().setOnClickListener(v -> iFoodFavorite.onClick(food));
    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemRcvListFavoriteBinding binding;

        public MyViewHolder(@NonNull ItemRcvListFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
