package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodapp.adapter.InactiveVoucherAdapter;
import com.example.foodapp.base.BaseFragment;

import com.example.foodapp.databinding.FragmentInactiveVoucherBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Voucher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InactiveVoucherFragment extends BaseFragment {
    private FragmentInactiveVoucherBinding binding;
    private InactiveVoucherAdapter adapter;
    private List<Voucher> lists;
    private double total = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentInactiveVoucherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTotalFromRealtime();
        initRcv();

    }

    private void initRcv() {
        lists = new ArrayList<>();
        adapter = new InactiveVoucherAdapter(lists);
        binding.rcvListInactiveVoucher.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListInactiveVoucher.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Voucher")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.txtEmpty.setVisibility(View.GONE);
                        lists.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Voucher voucher = item.getValue(Voucher.class);
                                if(voucher == null){
                                    continue;
                                }
                                if(voucher.getMinOderAmount() > total){
                                    lists.add(voucher);
                                }
                            }
                            if(lists.isEmpty()){
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }

                        } else binding.txtEmpty.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getTotalFromRealtime(){
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        total = 0;
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    total += cart.getCartTotal();
                                }
                            }
                            if(total >= 0){
                                DecimalFormat df = new DecimalFormat("#.##");
                                total = Double.parseDouble(df.format(total));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}