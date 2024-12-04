package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.databinding.ItemRcvListCartInOrderManagerBinding;
import com.example.foodapp.model.Cart;

import java.util.List;

public class AdminListCartOrderManagerAdapter extends RecyclerView.Adapter<AdminListCartOrderManagerAdapter.MyViewHolder> {
    private final List<Cart> carts;

    public AdminListCartOrderManagerAdapter(List<Cart> carts) {
        this.carts = carts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListCartInOrderManagerBinding binding = ItemRcvListCartInOrderManagerBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cart cart = carts.get(position);
        if(cart == null){
            return;
        }
        holder.binding.txtNameFood.setText(cart.getFood().getTitle());
        holder.binding.txtQuantity.setText("SL: "+ cart.getQuantity());
        holder.binding.txtPrice.setText("$"+ cart.getCartTotal());
    }

    @Override
    public int getItemCount() {
        if(carts != null){
            return carts.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListCartInOrderManagerBinding binding;

        public MyViewHolder(@NonNull ItemRcvListCartInOrderManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
