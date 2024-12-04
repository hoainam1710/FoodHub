package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.activities.AdminOrderDetailActivity;
import com.example.foodapp.adapter.AdminCompletedOrCanceledOrderAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentAdminCompletedOrderBinding;
import com.example.foodapp.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCompletedOrderFragment extends BaseFragment implements AdminCompletedOrCanceledOrderAdapter.IAdminCompletedOrCanceledOrder {
    private FragmentAdminCompletedOrderBinding binding;
    private List<Order> orders;
    private static final String TAG = "AdminCompletedOrderFragment";

    public static AdminCompletedOrderFragment getInstance() {
        return new AdminCompletedOrderFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding =  FragmentAdminCompletedOrderBinding.inflate(inflater, container, false);
         return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRcv();
    }

    private void initRcv() {
        orders = new ArrayList<>();
        binding.rcvListCompletedOrder.setLayoutManager(new LinearLayoutManager(requireActivity()));
        AdminCompletedOrCanceledOrderAdapter adminActiveOrderAdapter = new AdminCompletedOrCanceledOrderAdapter(requireActivity(), orders, this);
        binding.rcvListCompletedOrder.setAdapter(adminActiveOrderAdapter);
        loadData(adminActiveOrderAdapter);
    }

    private void loadData(AdminCompletedOrCanceledOrderAdapter adminActiveOrderAdapter) {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        orders.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Order order = item.getValue(Order.class);
                                if(order != null && order.getStatus().equals("completed")){
                                    orders.add(order);
                                }
                            }
                            if(orders.isEmpty()){
                                binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                            } else binding.layoutNotifyEmpty.setVisibility(View.GONE);
                        } else binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                        adminActiveOrderAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getAdminOrderCompletedError: "+ error.getMessage());
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void onClickViewDetailOrder(Order order){
        Intent intent = new Intent(requireActivity(), AdminOrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void viewDetailOrder(Order order) {
        onClickViewDetailOrder(order);
    }
}