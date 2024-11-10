package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodapp.adapter.ListFoodOrderAdapter;
import com.example.foodapp.databinding.FragmentItemOrderDialogBinding;
import com.example.foodapp.model.Cart;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class ItemOrderDialogFragment extends BottomSheetDialogFragment {

    public interface IItemOrderDialogFragment{
         void onClick();
    }
    private FragmentItemOrderDialogBinding binding;
    private List<Cart> lists;
    private ListFoodOrderAdapter adapter;
    private IItemOrderDialogFragment iItemOrderDialogFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iItemOrderDialogFragment = (IItemOrderDialogFragment) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentItemOrderDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRcv();
        binding.imgHide.setOnClickListener(v -> {
            iItemOrderDialogFragment.onClick();
        });
    }

    private void initRcv() {
        lists = new ArrayList<>();
        binding.rcvListOrder.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ListFoodOrderAdapter(requireContext(), lists);
        binding.rcvListOrder.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        binding.rcvListOrder.addItemDecoration(itemDecoration);
        loadData();
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String id = user.getUid();
        FirebaseDatabase.getInstance().getReference("Account").child(id).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lists.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    lists.add(cart);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

