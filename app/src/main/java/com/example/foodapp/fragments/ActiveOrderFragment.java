package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.foodapp.R;
import com.example.foodapp.activities.OrderDetailActivity;
import com.example.foodapp.adapter.ListOrderAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentActiveOrderBinding;
import com.example.foodapp.model.Order;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ActiveOrderFragment extends BaseFragment implements ListOrderAdapter.IListOrder {
    private FragmentActiveOrderBinding binding;
    private ListOrderAdapter adapter;
    private List<Order> lists;
    private BottomSheetDialog bottomSheetDialogCancelSuccess;

    public static ActiveOrderFragment getInstance(){
        return new ActiveOrderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentActiveOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRcv();
    }

    private void initRcv() {
        lists = new ArrayList<>();
        adapter = new ListOrderAdapter(requireActivity(), lists, this);
        binding.rcvListActiveOrder.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListActiveOrder.setAdapter(adapter);
        loadRcvListOrders();

    }

    private void loadRcvListOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutNotifyEmpty.setVisibility(View.GONE);
                        lists.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
//                                Order order = item.getValue(Order.class);
//                                if(order == null){
//                                    continue;
//                                }
//                                if(order.getIdUser().equals(getUid()) && order.getStatus().equals("active")){
//                                    lists.add(order);
//                                }
                                try {
                                    Order order = item.getValue(Order.class);
                                    if (order == null) {
                                        continue;
                                    }
                                    if (order.getIdUser().equals(getUid()) && order.getStatus().equals("active")) {
                                        lists.add(order);
                                    }
                                } catch (DatabaseException e) {
                                    Log.e("DatabaseError", "Error deserializing Order: " + e.getMessage());
                                }
                            }
                            if(lists.isEmpty()){
                                binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void showPopupMenu(Order order, View view,  int type) {
        PopupMenu popupMenu = new PopupMenu(requireActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_order, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.menu_cancel_order).setVisible(type == ListOrderAdapter.ACTIVE_TYPE);
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.menu_cancel_order){
                showBottomSheetDialogCancelOder(order);
                return true;
            } else if(item.getItemId() == R.id.menu_detail_order){
                nextToOrderDetailActivity(order);
                return true;
            }
            return false;
        });
        popupMenu.show();

    }

    private void showBottomSheetDialogCancelOder(Order order){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_cancel_order);
        Button btnYes = bottomSheetDialog.findViewById(R.id.btn_yes);
        Button btnNo = bottomSheetDialog.findViewById(R.id.btn_no);
        if(btnNo == null || btnYes == null){
            return;
        }
        btnNo.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnYes.setOnClickListener(v -> {
            database.getReference("Orders").child(order.getId())
                    .child("status").setValue("canceled")
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            bottomSheetDialog.dismiss();
                            showNotifyCancelOrderSuccess();
                            new Handler().postDelayed(() -> bottomSheetDialogCancelSuccess.dismiss(), 3000);
                        }

                    });
        });

        bottomSheetDialog.show();

    }

    private void showNotifyCancelOrderSuccess(){
        bottomSheetDialogCancelSuccess = new BottomSheetDialog(requireActivity());
        bottomSheetDialogCancelSuccess.setContentView(R.layout.bottom_sheet_dialog_cancel_order_success);
        bottomSheetDialogCancelSuccess.show();
    }

    private void nextToOrderDetailActivity(Order order){
        Intent intent = new Intent(requireActivity(), OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClickItem(Order order) {
        Intent intent = new Intent(requireActivity(), OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void viewReview(Order order) {

    }

    @Override
    public void actionReview(Order order) {

    }
}