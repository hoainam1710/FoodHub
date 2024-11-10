package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvListFoodOrderBinding;
import com.example.foodapp.model.Cart;

import java.util.List;

public class ListFoodOrderAdapter extends RecyclerView.Adapter<ListFoodOrderAdapter.MyViewHolder> {
    private final List<Cart> lists;
    private final Context context;

    public ListFoodOrderAdapter(Context context, List<Cart> lists) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListFoodOrderBinding binding = ItemRcvListFoodOrderBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cart cart = lists.get(position);
        if(cart == null){
            return;
        }
        Glide.with(context).load(cart.getFood().getImagePath()).into(holder.binding.imgFood);
        holder.binding.txtTitle.setText(cart.getFood().getTitle());
        holder.binding.txtPrice.setText("$"+ cart.getFood().getPrice());
        holder.binding.txtCount.setText("x"+ cart.getQuantity());

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListFoodOrderBinding binding;

        public MyViewHolder(@NonNull ItemRcvListFoodOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
