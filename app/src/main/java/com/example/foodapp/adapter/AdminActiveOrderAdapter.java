package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListAdminActiveOrderBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminActiveOrderAdapter extends RecyclerView.Adapter<AdminActiveOrderAdapter.MyViewHolder> {
    public interface IAdminActiveOrder{
        void cancelOrder(Order order);
        void acceptOrder(Order order);
        void viewDetailOrder(Order order);
    }
    private final List<Order> lists;
    private final Context context;
    private final IAdminActiveOrder iAdminActiveOrder;
    private static final String TAG = "AdminActiveOrderAdapter";

    public AdminActiveOrderAdapter(Context context, List<Order> lists, IAdminActiveOrder iAdminActiveOrder) {
        this.context = context;
        this.lists = lists;
        this.iAdminActiveOrder = iAdminActiveOrder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListAdminActiveOrderBinding binding = ItemRcvListAdminActiveOrderBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = lists.get(position);
        if(order == null){
            return;
        }
        String idUser = order.getIdUser();
        getUserOrder(idUser, holder);
        holder.binding.txtNameUser.setText(order.getShippingAddress().getName());
        holder.binding.txtDate.setText(order.getDate());
//        holder.binding.txtOrderId.setText("Mã đơn: "+ order.getId());
        DecimalFormat format = new DecimalFormat("#.##");
        holder.binding.txtTotalOrder.setText("Thành tiền: $"+ format.format(order.getTotal()));

        // list food order
        holder.binding.rcvListFoodOrder.setLayoutManager(new LinearLayoutManager(context));
        List<Cart> carts = new ArrayList<>();
        AdminListCartOrderManagerAdapter adminListCartOrderManagerAdapter = new AdminListCartOrderManagerAdapter(order.getLists());
        holder.binding.rcvListFoodOrder.setAdapter(adminListCartOrderManagerAdapter);

        // event
        holder.binding.btnViewDetailOrder.setOnClickListener(v ->
                iAdminActiveOrder.viewDetailOrder(order));

        holder.binding.btnAcceptOrder.setOnClickListener(v ->
                iAdminActiveOrder.acceptOrder(order));

        holder.binding.btnCancelOrder.setOnClickListener(v ->
                iAdminActiveOrder.cancelOrder(order));
    }

    private void getUserOrder(String id, MyViewHolder holder){
        FirebaseDatabase.getInstance().getReference("Account").child(id)
                .child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if(user == null){
                                return;
                            }

                            Glide.with(context).load(user.getAvatar())
                                    .error(R.drawable.avatar_default)
                                    .placeholder(R.drawable.avatar_default)
                                    .into(holder.binding.imgAvatarOrderUser);

                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.binding.txtNameUser.setText("Người dùng mới");
                        Glide.with(context).load(R.drawable.avatar_default)
                                .into(holder.binding.imgAvatarOrderUser);
                        Log.d(TAG, "getUserCancelled: "+ error.getMessage());
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
        private final ItemRcvListAdminActiveOrderBinding binding;

        public MyViewHolder(@NonNull ItemRcvListAdminActiveOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
