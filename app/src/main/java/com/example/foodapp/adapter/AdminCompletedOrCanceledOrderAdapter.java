package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListAdminCompletedOrderBinding;
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

public class AdminCompletedOrCanceledOrderAdapter extends RecyclerView.Adapter<AdminCompletedOrCanceledOrderAdapter.MyViewHolder> {
    public interface IAdminCompletedOrCanceledOrder {
        void viewDetailOrder(Order order);
    }
    private final List<Order> lists;
    private final Context context;
    private final IAdminCompletedOrCanceledOrder iAdminCompletedOrder;
    private static final String TAG = "AdminCompletedOrderAdapter";

    public AdminCompletedOrCanceledOrderAdapter(Context context, List<Order> lists, IAdminCompletedOrCanceledOrder iAdminCompletedOrder) {
        this.context = context;
        this.lists = lists;
        this.iAdminCompletedOrder = iAdminCompletedOrder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListAdminCompletedOrderBinding binding = ItemRcvListAdminCompletedOrderBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = lists.get(position);
        if(order == null){
            return;
        }
        if(order.getStatus().equals("completed")){
            holder.binding.txtStatus.setText("Trạng thái: Đã hoàn thành");
            holder.binding.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green));
        } else {
            holder.binding.txtStatus.setText("Trạng thái: Đã hủy");
            holder.binding.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
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
                iAdminCompletedOrder.viewDetailOrder(order));

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
        private final ItemRcvListAdminCompletedOrderBinding binding;

        public MyViewHolder(@NonNull ItemRcvListAdminCompletedOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
