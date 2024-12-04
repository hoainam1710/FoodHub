package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.adapter.AdminListCartOrderManagerAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAdminOrderDetailBinding;
import com.example.foodapp.fragments.BottomSheetNotifySuccessFragment;
import com.example.foodapp.fragments.ProgressDialogFragment;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.Payment;
import com.example.foodapp.model.ShippingAddress;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminOrderDetailActivity extends BaseActivity {
    private ActivityAdminOrderDetailBinding binding;
    private Order orderSelected = null;
    private ProgressDialogFragment progressDialogFragment;
    private BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment;
    private static final String TAG = "AdminOrderDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOrderByIntent();
        getUserOrder();
        getPaymentOrder();
        initListener();
        initRcv();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnAcceptOrder.setOnClickListener(v -> acceptOrder(orderSelected));
        binding.btnCancelOrder.setOnClickListener(v -> cancelOrder(orderSelected));
    }

    @SuppressLint("SetTextI18n")
    private void getOrderByIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        orderSelected = (Order) bundle.getSerializable("order");
        if (orderSelected == null) {
            return;
        }
        if(orderSelected.getStatus().equals("active")){
            binding.btnCancelOrder.setVisibility(View.VISIBLE);
            binding.btnAcceptOrder.setVisibility(View.VISIBLE);
            binding.txtStatus.setVisibility(View.GONE);
        } else {
            binding.btnCancelOrder.setVisibility(View.GONE);
            binding.btnAcceptOrder.setVisibility(View.GONE);
            binding.txtStatus.setVisibility(View.VISIBLE);
            String completedStatus = "Trạng thái: Đã hoàn thành";
            String canceledStatus = "Trạng thái: Đã hủy";
            int completedColor = ContextCompat.getColor(this, R.color.dark_green);
            int canceledColor = ContextCompat.getColor(this, R.color.red);

            binding.txtStatus.setText(orderSelected.getStatus().equals("completed") ? completedStatus: canceledStatus);
            binding.txtStatus.setTextColor(orderSelected.getStatus().equals("completed") ? completedColor: canceledColor);
        }
        binding.txtDate.setText(orderSelected.getDate());
        binding.txtOrderId.setText("Mã đơn: "+ orderSelected.getId());
        binding.txtNameUser.setText(orderSelected.getShippingAddress().getName());
        binding.txtPhoneNumber.setText(orderSelected.getShippingAddress().getPhoneNumber());
        ShippingAddress shippingAddress = orderSelected.getShippingAddress();
        String fullAddress = shippingAddress.getSoNha() + " Phường "+ shippingAddress.getPhuong()
                + ", Quận "+ shippingAddress.getQuan() + ", "+ shippingAddress.getThanhPho();
        binding.txtAddress.setText(fullAddress);

        binding.txtSumPrice.setText("$"+ orderSelected.getSumPrice());
        binding.txtDiscount.setText("-$"+ orderSelected.getDiscount());
        binding.txtDelivery.setText("$"+ orderSelected.getDelivery());
        binding.txtTotal.setText("$"+ orderSelected.getTotal());
    }

    @SuppressLint("SetTextI18n")
    private void getPaymentOrder(){
        Payment payment = orderSelected.getPayment();
        if(payment == null){
            binding.txtPaymentMethod.setText("Thanh toán khi nhận hàng");
            binding.layoutCardNumber.setVisibility(View.GONE);
        } else {
            binding.txtPaymentMethod.setText("Thanh toán bằng thẻ ngân hàng");
            binding.layoutCardNumber.setVisibility(View.VISIBLE);
            String lastCardNumber = payment.getCardNumber().substring(12);
            binding.txtCardNumber.setText("**** **** **** "+ lastCardNumber);
        }
    }


    private void getUserOrder() {
        String idUser = orderSelected.getIdUser();
        FirebaseDatabase.getInstance().getReference("Account").child(idUser)
                .child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user == null) {
                                return;
                            }
                            if(user.getEmail() != null){
                                binding.txtEmail.setText(user.getEmail());
                            } else binding.txtEmail.setText("Chưa xác định");

                            Glide.with(AdminOrderDetailActivity.this).load(user.getAvatar())
                                    .error(R.drawable.avatar_default)
                                    .placeholder(R.drawable.avatar_default)
                                    .into(binding.imgAvatarOrderUser);

                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.txtNameUser.setText("Người dùng mới");
                        Glide.with(AdminOrderDetailActivity.this).load(R.drawable.avatar_default)
                                .into(binding.imgAvatarOrderUser);
                        Log.d(TAG, "getUserCancelled: " + error.getMessage());
                    }
                });

    }

    private void acceptOrder(Order order){
        showProgressDialog();
        database.getReference("Orders").child(order.getId())
                .child("status").setValue("completed")
                .addOnCompleteListener(task -> {
                    progressDialogFragment.dismiss();
                    if(task.isSuccessful()){
                        showNotifyBottomSheetDialog("Xác nhận đơn hàng thành công!");
                        if(!bottomSheetNotifySuccessFragment.isVisible()){
                            setStatusOrder("completed");
                        }

                    } else
                        Toast.makeText(AdminOrderDetailActivity.this, "Xác nhận đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
                });
    }

    private void cancelOrder(Order order) {
        showProgressDialog();
        database.getReference("Orders").child(order.getId())
                .child("status").setValue("canceled")
                .addOnCompleteListener(task -> {
                    progressDialogFragment.dismiss();
                    if(task.isSuccessful()){
                        showNotifyBottomSheetDialog("Đã hủy đơn hàng thành công!");
                        if(!bottomSheetNotifySuccessFragment.isVisible()){
                            setStatusOrder("canceled");
                        }
                    } else
                        Toast.makeText(AdminOrderDetailActivity.this, "Hủy đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressDialog() {
        progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag("progressDialogFragment");
        if(progressDialogFragment == null){
            progressDialogFragment = new ProgressDialogFragment();
        }
        progressDialogFragment.show(getSupportFragmentManager(), "progressDialogFragment");
    }

    private void showNotifyBottomSheetDialog(String message){
        bottomSheetNotifySuccessFragment = new BottomSheetNotifySuccessFragment(message);
        bottomSheetNotifySuccessFragment.show(getSupportFragmentManager(), "notifyBottomSheetDialog");
        new Handler().postDelayed(() -> bottomSheetNotifySuccessFragment.dismiss(), 3000);
    }


    private void initRcv() {
        binding.rcvListFoodOrder.setLayoutManager(new LinearLayoutManager(this));
        AdminListCartOrderManagerAdapter adminListCartOrderManagerAdapter = new AdminListCartOrderManagerAdapter(orderSelected.getLists());
        binding.rcvListFoodOrder.setAdapter(adminListCartOrderManagerAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void setStatusOrder(String status){
        binding.btnCancelOrder.setVisibility(View.GONE);
        binding.btnAcceptOrder.setVisibility(View.GONE);
        if(status.equals("completed")){
            binding.txtStatus.setText("Trạng thái: Đã hoàn thành");
            binding.txtStatus.setTextColor(ContextCompat.getColor(this, R.color.dark_green));
        } else {
            binding.txtStatus.setText("Trạng thái: Đã hủy");
            binding.txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        binding.txtStatus.setVisibility(View.VISIBLE);

    }


}