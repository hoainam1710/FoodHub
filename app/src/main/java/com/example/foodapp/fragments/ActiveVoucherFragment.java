package com.example.foodapp.fragments;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodapp.adapter.VoucherAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentActiveVoucherBinding;

import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Voucher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ActiveVoucherFragment extends BaseFragment implements VoucherAdapter.IVoucher {
    private FragmentActiveVoucherBinding binding;
    private VoucherAdapter adapter;
    private List<Voucher> lists;
    private float total = 0;
    private float discount = 0;
    private Voucher voucherSelected;
    private SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "voucher_selected3";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentActiveVoucherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtVoucherCount.setText("0 Phiếu giảm giá đã chọn");
        binding.txtDiscount.setText("$"+ discount);

        initSharedPreferences();
        getTotalFromRealtime();
        initRcv();
        initListener();
    }

    private void initSharedPreferences(){
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void saveSharedPreference(int idVoucher){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idVoucher", idVoucher);
        editor.apply();
    }
    public void clearSharedPreference(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initListener() {
        binding.imgDeleteNotify.setOnClickListener(v -> binding.layoutNotify.setVisibility(View.GONE));

        binding.btnNext.setOnClickListener(v -> {
            if(voucherSelected != null){
                saveSharedPreference(voucherSelected.getId());
//                adapter.notifyDataSetChanged();
            }
            Intent  intent = new Intent();

            intent.putExtra("voucher", voucherSelected);
            intent.putExtra("discount", discount);

            requireActivity().setResult(Activity.RESULT_OK, intent);
            requireActivity().finish();
        });

    }

    private void initRcv() {
        lists = new ArrayList<>();
        adapter = new VoucherAdapter(requireActivity(), lists, this);
        binding.rcvListActiveVoucher.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListActiveVoucher.setAdapter(adapter);
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
                                if(voucher.getMinOderAmount() <= total){
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onClick(Voucher voucher, boolean checked) {
        // post() là method của 1 view bất kì đc sủ dụng để đưa 1 hành động vào hàng dợi để thực
        // hiện các công việc ui chưa hoàn tất như tính toán bố cục, cuộn, ...
        // bug: java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
        binding.rcvListActiveVoucher.post(new Runnable() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void run() {
                // Gọi notifyDataSetChanged sau khi RecyclerView hoàn thành việc tính toán
                adapter.notifyDataSetChanged();
                if(checked){
                    voucherSelected = voucher;
                    binding.txtVoucherCount.setText("1 Phiếu giảm giá đã chọn");
                    discount = total * voucher.getDiscount();
                    DecimalFormat df = new DecimalFormat("#.##");
                    discount = Float.parseFloat(df.format(discount));
                    binding.txtDiscount.setText("$"+ discount);
                } else {
//                    clearSharedPreference();
                    voucherSelected = null;
                    discount = 0;
                    binding.txtVoucherCount.setText("0 Phiếu giảm giá đã chọn");
                    binding.txtDiscount.setText("$"+ discount);
                }

            }
        });
    }
}