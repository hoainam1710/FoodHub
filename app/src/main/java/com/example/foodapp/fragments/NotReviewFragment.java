package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodapp.activities.ReviewActivity;
import com.example.foodapp.adapter.ListOrderAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentNotReviewBinding;
import com.example.foodapp.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotReviewFragment extends BaseFragment implements ListOrderAdapter.IListOrder {
    private FragmentNotReviewBinding binding;
    private ListOrderAdapter adapter;
    private List<Order> lists;
    

    public static NotReviewFragment getInstance() {
        return new NotReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotReviewBinding.inflate(inflater, container, false);
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
        binding.rcvListNotReview.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListNotReview.setAdapter(adapter);
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
                                try {
                                    Order order = item.getValue(Order.class);
                                    if (order == null) {
                                        continue;
                                    }
                                    if (order.getIdUser().equals(getUid()) && order.getStatus().equals("completed")
                                            && !order.isCheckedReview()) {
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
    public void actionReview(Order order) {
        Intent intent = new Intent(requireActivity(), ReviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showPopupMenu(Order order, View view, int type) {
    }

    @Override
    public void onClickItem(Order order) {
    }

    @Override
    public void viewReview(Order order) {
    }


}