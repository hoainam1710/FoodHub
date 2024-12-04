package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.activities.AdminOrderDetailActivity;
import com.example.foodapp.adapter.AdminActiveOrderAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentAdminActiveOrderBinding;
import com.example.foodapp.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActiveOrderFragment extends BaseFragment implements AdminActiveOrderAdapter.IAdminActiveOrder {
    private FragmentAdminActiveOrderBinding binding;
    private  List<Order> orders;
    private static final String TAG = "AdminActiveOrderFragment";
    private ProgressDialogFragment progressDialogFragment;
    private BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment;


    public static AdminActiveOrderFragment getInstance() {
        return new AdminActiveOrderFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentAdminActiveOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRcv();
    }

    private void showNotifyBottomSheetDialog(String message){
        bottomSheetNotifySuccessFragment = (BottomSheetNotifySuccessFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("bottomSheetDialogFragment");
        if(bottomSheetNotifySuccessFragment == null){
            bottomSheetNotifySuccessFragment = new BottomSheetNotifySuccessFragment(message);
        }
        bottomSheetNotifySuccessFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheetDialogFragment");
        new Handler().postDelayed(() -> bottomSheetNotifySuccessFragment.dismiss(), 3000);
    }

    private void showProgressDialog() {
        progressDialogFragment = (ProgressDialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("progressDialogFragment");
        if(progressDialogFragment == null){
            progressDialogFragment = new ProgressDialogFragment();
        }
        progressDialogFragment.show(requireActivity().getSupportFragmentManager(), "progressDialogFragment");

    }

    private void initRcv() {
        orders = new ArrayList<>();
        binding.rcvListActiveOrder.setLayoutManager(new LinearLayoutManager(requireActivity()));
        AdminActiveOrderAdapter adminActiveOrderAdapter = new AdminActiveOrderAdapter(requireActivity(), orders, this);
        binding.rcvListActiveOrder.setAdapter(adminActiveOrderAdapter);
        loadData(adminActiveOrderAdapter);
    }

    private void loadData(AdminActiveOrderAdapter adminActiveOrderAdapter) {
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
                                if(order != null && order.getStatus().equals("active")){
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
                        Log.d(TAG, "getAdminOrderActiveCancelled: "+ error.getMessage());
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onClickCancelOrder(Order order) {
        showProgressDialog();
        database.getReference("Orders").child(order.getId())
                .child("status").setValue("canceled")
                .addOnCompleteListener(task -> {
                    progressDialogFragment.dismiss();
                    if(task.isSuccessful()){
                        showNotifyBottomSheetDialog("Đã hủy đơn hàng thành công!");
                    } else
                        Toast.makeText(requireActivity(), "Hủy đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
                });
    }

    private void onClickAcceptOrder(Order order){
        showProgressDialog();
        database.getReference("Orders").child(order.getId())
                .child("status").setValue("completed")
                .addOnCompleteListener(task -> {
                    progressDialogFragment.dismiss();
                    if(task.isSuccessful()){
                        showNotifyBottomSheetDialog("Xác nhận đơn hàng thành công!");
                    } else
                        Toast.makeText(requireActivity(), "Xác nhận đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
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
    public void cancelOrder(Order order) {
        onClickCancelOrder(order);
    }


    @Override
    public void acceptOrder(Order order) {
        onClickAcceptOrder(order);
    }

    @Override
    public void viewDetailOrder(Order order) {
        onClickViewDetailOrder(order);
    }
}