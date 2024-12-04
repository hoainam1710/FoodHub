package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListCustomerBinding;
import com.example.foodapp.model.User;

import java.util.List;

public class ListCustomerAdapter extends RecyclerView.Adapter<ListCustomerAdapter.MyViewHolder> {
    public interface IListCustomer{
        void showBottomSheetDialog(User user);
    }
    private final List<User> users;
    private final IListCustomer iListCustomer;

    public ListCustomerAdapter(List<User> users, IListCustomer iListCustomer) {
        this.users = users;
        this.iListCustomer = iListCustomer;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListCustomerBinding binding = ItemRcvListCustomerBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = users.get(position);
        if(user == null){
            return;
        }
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar())
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .into(holder.binding.imgAvatar);
        holder.binding.txtEmail.setText(user.getEmail());
        if(user.getName() == null || user.getName().isEmpty()){
            String id = user.getId();
            String subId = id.substring(id.length()-6);
            holder.binding.txtName.setText("User_"+ subId);
        } else holder.binding.txtName.setText(user.getName());

        holder.binding.imgMenu.setOnClickListener(v -> iListCustomer.showBottomSheetDialog(user));

    }

    @Override
    public int getItemCount() {
        if(users != null){
            return users.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListCustomerBinding binding;

        public MyViewHolder(@NonNull ItemRcvListCustomerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
