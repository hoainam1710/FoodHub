package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.CartAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityCartBinding;
import com.example.foodapp.events.CheckboxCartMessageEvent;
import com.example.foodapp.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity implements CartAdapter.ICart {
    private ActivityCartBinding binding;
    private List<Cart> lists;
    private CartAdapter adapter;
    private boolean isUpdateChecked = false;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private EventBus eventBus;
    private double total = 0;
    private int itemOrderCounts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventBus = new EventBus();
        initProgressDialog();
        checkEmptyOrder();
        getDataSharedPreference();
        getTotalFromRealtime();
        initRcv();
        initListener();
    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang xử lý...");

    }

    private void getDataSharedPreference(){
        sharedPreferences = getSharedPreferences("CheckedCboAllFood", MODE_PRIVATE);
        boolean isCheckedCboAllFood = sharedPreferences.getBoolean("isCheckedCboAllFood", false);
        binding.cboAllFood.setChecked(isCheckedCboAllFood);
    }

    private void clearSharedPreference(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    private void saveCboAllFood(boolean isChecked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isCheckedCboAllFood", isChecked);
        editor.apply();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnRequiredShopping.setOnClickListener(v -> {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        });

        binding.cboAllFood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onClickCboAllFood(isChecked);
        });

        binding.btnThanhToan.setOnClickListener(v -> onClickBtnThanhToan());

    }

    private void onClickBtnThanhToan() {
        if(itemOrderCounts == 0){
            Toast.makeText(this, "Bạn chưa chọn sản phẩm để mua.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        database.getReference("Account").child(getUid()).child("shippingAddress")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        if(!snapshot.exists()){
                            Intent intent = new Intent(CartActivity.this, AddShippingAddressActivity.class);
                            intent.putExtra("cartActivityNextToShippingAddress","cart");
                            startActivity(intent);
                        } else {
                            checkPaymentExists();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkEmptyOrder(){
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemOrderCounts = 0;
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    itemOrderCounts++;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkPaymentExists(){
        database.getReference("Account").child(getUid()).child("payment")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            Intent intent = new Intent(CartActivity.this, AddPaymentActivity.class);
                            intent.putExtra("cartActivityToAddPayment", "cart");
                            startActivity(intent);
                        } else  {
                            startActivity(new Intent(CartActivity.this, CheckOutActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onClickCboAllFood(boolean isChecked) {
        Log.d("aaa", "cbo_all_food");
        if(isUpdateChecked){
            return;
        }
        DatabaseReference mRef = database.getReference("Account").child(getUid()).child("cart");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        item.getRef().child("checked")
                                .setValue(isChecked)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        saveCboAllFood(isChecked);
                                        isUpdateChecked = true;
                                        CheckboxCartMessageEvent checkboxCartMessageEvent = new CheckboxCartMessageEvent(false);
                                        eventBus.post(checkboxCartMessageEvent);
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void initRcv() {
        lists = new ArrayList<>();
        adapter = new CartAdapter(this, lists, this);
        binding.rcvListCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvListCart.setAdapter(adapter);
        loadRcv();
    }

    private void loadRcv() {
        binding.progressBarListCart.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBarListCart.setVisibility(View.GONE);
                        lists.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart != null){
                                    lists.add(cart);
                                }
                            }
                        } else {
                            binding.txtNotExists.setVisibility(View.VISIBLE);
                            binding.btnRequiredShopping.setVisibility(View.VISIBLE);
                            binding.layoutBottom.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarListCart.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClickItem(Cart cart) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", cart.getFood());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteCart(Cart cart) {
        binding.progressBarListCart.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("cart")
                .child(String.valueOf(cart.getId())).removeValue()
                .addOnCompleteListener(task -> {
                    binding.progressBarListCart.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Xóa khỏi giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                        clearSharedPreference();
                    } else Toast.makeText(this, "Xóa khỏi giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void getTotalFromRealtime(){
        binding.progressBarTotal.setVisibility(View.VISIBLE);
        binding.txtTotal.setText("");
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBarTotal.setVisibility(View.GONE);
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
                            if(total >= 0f){
                                DecimalFormat df = new DecimalFormat("#.##");
                                double totalFormat = Double.parseDouble(df.format(total));
                                binding.txtTotal.setText("$"+ totalFormat);
                            } else binding.txtTotal.setText("$"+ 0);
                        } else binding.txtTotal.setText("$"+ 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarTotal.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClickChecked(Cart cart, boolean isChecked) {
        if(!isChecked && binding.cboAllFood.isChecked()){
            binding.cboAllFood.setChecked(false);
            clearSharedPreference();
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCheckedCboAllFoodEventBus(CheckboxCartMessageEvent checkboxCartMessageEvent){
        isUpdateChecked = checkboxCartMessageEvent.isChecked();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }
}