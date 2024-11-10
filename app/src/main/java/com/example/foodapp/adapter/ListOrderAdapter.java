package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.databinding.ItemRcvListAvitveOrderBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.MyViewHolder> {
    public interface IListOrder{
        void showPopupMenu(Order order, View view, int type);
        void onClickItem(Order order);
        void viewReview(Order order);
        void actionReview(Order order);
    }

    private final List<Order> listActiveOrders;
    private final Context context;
    private final IListOrder iListOrder;
    public static final int ACTIVE_TYPE = 0;
    public static final int COMPLETED_TYPE = 1;
    public static final int CANCELED_TYPE = 2;


    public ListOrderAdapter(Context context, List<Order> listActiveOrders, IListOrder iListOrder) {
        this.context =  context;
        this.listActiveOrders = listActiveOrders;
        this.iListOrder = iListOrder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        com.example.foodapp.databinding.ItemRcvListAvitveOrderBinding binding = com.example.foodapp.databinding.ItemRcvListAvitveOrderBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order =  listActiveOrders.get(position);
        if(order == null){
            return;
        }

        holder.binding.txtTimeStamp.setText(order.getDate());
        DecimalFormat format = new DecimalFormat("#.##");
        double sumTotal = Double.parseDouble(format.format(order.getTotal()));
        holder.binding.txtSumTotal.setText("Tổng thanh toán: $"+ sumTotal);

//        rcv_list_cart in order
        List<Cart> listCartOrder = new ArrayList<>();
        holder.binding.rcvListActiveOrder.setLayoutManager(new LinearLayoutManager(context));
        ListFoodOrderAdapter listOrderAdapter = new ListFoodOrderAdapter(context, listCartOrder);
        holder.binding.rcvListActiveOrder.setAdapter(listOrderAdapter);
        loadListCartOrder(order, listCartOrder, listOrderAdapter);

        // event
        holder.binding.getRoot().setOnClickListener(v -> iListOrder.onClickItem(order));

        holder.binding.imgMenuPopup.setOnClickListener(v ->
                iListOrder.showPopupMenu(order, holder.binding.imgMenuPopup, holder.getItemViewType()));

        if(holder.getItemViewType() == COMPLETED_TYPE){
            holder.binding.btnReview.setVisibility(View.VISIBLE);
            if(order.isCheckedReview()){
                holder.binding.btnReview.setText("Xem đánh giá");
            } else  {
                holder.binding.btnReview.setText("Đánh giá");
            }

        } else holder.binding.btnReview.setVisibility(View.GONE);

        holder.binding.btnReview.setOnClickListener(v -> {
            if(order.isCheckedReview()){
                iListOrder.viewReview(order);
            } else iListOrder.actionReview(order);
        });



    }

    private void loadListCartOrder(Order order, List<Cart> listCartOrder, ListFoodOrderAdapter listFoodOrderAdapter){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        FirebaseDatabase.getInstance().getReference("Orders").child(order.getId()).child("lists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listCartOrder.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                listCartOrder.add(cart);
                            }
                        }
                        listFoodOrderAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemViewType(int position) {
        Order order = listActiveOrders.get(position);
        if(order.getStatus().equals("active")){
            return ACTIVE_TYPE;
        } else if(order.getStatus().equals("completed")){
            return COMPLETED_TYPE;
        } else return CANCELED_TYPE;
    }

    @Override
    public int getItemCount() {
        if(listActiveOrders != null){
            return listActiveOrders.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListAvitveOrderBinding binding;

        public MyViewHolder(ItemRcvListAvitveOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
