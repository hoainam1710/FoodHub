package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.activities.LoginActivity;
import com.example.foodapp.activities.MyOrderActivity;
import com.example.foodapp.activities.PaymentActivity;
import com.example.foodapp.activities.ReviewHistoryActivity;
import com.example.foodapp.activities.SettingProfileActivity;
import com.example.foodapp.activities.ShippingAddressActivity;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentAccountBinding;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends BaseFragment {
    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getInformationUserFromRealtime();
        getShippingAddressCountFromRealtime();
        getPaymentCountFromRealtime();
        initListener();
    }

    private void initListener() {
        binding.imgLogOut.setOnClickListener(v -> onClickLogOut());

        binding.layoutMyReview.setOnClickListener(v -> nextToReviewHistoryActivity());

        binding.layoutShippingAddress.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ShippingAddressActivity.class)));

        binding.layoutPayment.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PaymentActivity.class)));

        binding.layoutSetting.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), SettingProfileActivity.class)));

        binding.layoutMyOrder.setOnClickListener(v -> nextToMyOrderActivity());

    }

    private void onClickLogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Xác nhận đăng xuất tài khoản")
                .setCancelable(false)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finishAffinity();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void nextToReviewHistoryActivity(){
        Intent intent = new Intent(requireActivity(), ReviewHistoryActivity.class);
        intent.putExtra("from", "AccountFragment");
        startActivity(intent);
    }

    private void getInformationUserFromRealtime() {
        database.getReference("Account").child(getUid()).child("profile")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if(user == null){
                                return;
                            }
                            String strAvatar = user.getAvatar();
                            String name = user.getName();
                            String email = user.getEmail();

                            binding.txtEmail.setText(email);
                            if(strAvatar != null){
                                Uri uriAvatar = Uri.parse(strAvatar);
                                Glide.with(requireActivity())
                                        .load(uriAvatar)
                                        .placeholder(R.drawable.avatar_default)
                                        .error(R.drawable.avatar_default)
                                        .into(binding.imgAvatar);

                            } else Glide.with(requireActivity())
                                    .load(R.drawable.avatar_default)
                                    .into(binding.imgAvatar);

                            if(name == null || name.isEmpty()){
                                String id = user.getId();
                                String subId = id.substring(id.length()-6);
                                binding.txtName.setText("User_"+ subId);
                            } else binding.txtName.setText(name);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Glide.with(requireActivity())
                                .load(R.drawable.avatar_default)
                                .into(binding.imgAvatar);
                        binding.txtName.setText("Người dùng mới");
                        binding.txtEmail.setText("");
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getShippingAddressCountFromRealtime(){
        database.getReference("Account").child(getUid()).child("shippingAddress")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int shippingAddressCount = (int) snapshot.getChildrenCount();
                            binding.txtContentShippingAddress.setText(shippingAddressCount+" địa chỉ nhận hàng");
                        } else
                            binding.txtContentShippingAddress.setText("Chưa cài đặt địa chỉ nhận hàng");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.txtContentShippingAddress.setText("");
                    }
                });
    }

    private void getPaymentCountFromRealtime(){
        database.getReference("Account").child(getUid()).child("payment")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int paymentCount = (int) snapshot.getChildrenCount();
                            binding.txtContentPayment.setText(paymentCount+" tài khoản liên kết");
                        } else
                            binding.txtContentPayment.setText("Bạn chưa liên kết tài khoản ngân hàng");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.txtContentPayment.setText("");
                    }
                });
    }


    private void nextToMyOrderActivity(){
        Intent intent = new Intent(requireActivity(), MyOrderActivity.class);
        intent.putExtra("from", "AccountFragment");
        startActivity(intent);
    }

}