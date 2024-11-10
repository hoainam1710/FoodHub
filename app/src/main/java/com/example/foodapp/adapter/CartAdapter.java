package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvCartBinding;
import com.example.foodapp.model.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    public interface ICart{
        void onClickItem(Cart cart);
        void deleteCart(Cart cart);
        void onClickChecked(Cart cart, boolean isChecked);
    }
    private final List<Cart> lists;
    private final Context context;
    private final ICart iCart;

    public CartAdapter(Context context, List<Cart> lists, ICart iCart) {
        this.context = context;
        this.lists = lists;
        this.iCart = iCart;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvCartBinding binding = ItemRcvCartBinding.inflate(inflater, parent, false);
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
        float price = cart.getFood().getPrice();
        holder.binding.txtPrice.setText("$"+ price);
        holder.binding.txtQuantity.setText(String.valueOf(cart.getQuantity()));

        holder.binding.cboFood.setChecked(cart.isChecked());

        // events
        holder.binding.getRoot().setOnClickListener(v -> iCart.onClickItem(cart));
        holder.binding.imgDelete.setOnClickListener(v -> iCart.deleteCart(cart));

        holder.binding.txtIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.binding.txtQuantity.getText().toString());
            if(quantity >= 1){
                quantity++;
                holder.binding.txtQuantity.setText(String.valueOf(quantity));
                cart.setQuantity(quantity);
                cart.setCartTotal(quantity * cart.getFood().getPrice());
                updateCartToRealtime(cart);

            }
        });

        holder.binding.txtReduce.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.binding.txtQuantity.getText().toString());
            if(quantity >= 2){
                quantity--;
                holder.binding.txtQuantity.setText(String.valueOf(quantity));
                cart.setQuantity(quantity);
                cart.setCartTotal(quantity * cart.getFood().getPrice());
                updateCartToRealtime(cart);

            }
        });

        holder.binding.cboFood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iCart.onClickChecked(cart, isChecked);
            cart.setChecked(isChecked);
            updateCartToRealtime(cart);
        });

    }

    private void updateCartToRealtime(Cart cart){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return;
        }
        FirebaseDatabase.getInstance().getReference("Account")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart")
                .child(String.valueOf(cart.getId()))
                .setValue(cart);
    }


    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvCartBinding binding;
        public MyViewHolder(@NonNull ItemRcvCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
