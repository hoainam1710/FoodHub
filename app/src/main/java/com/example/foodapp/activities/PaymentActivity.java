package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.PaymentAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityPaymentBinding;
import com.example.foodapp.events.CheckboxPaymentMessageEvent;
import com.example.foodapp.model.Payment;
import com.example.foodapp.utils.SetupFabUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends BaseActivity implements PaymentAdapter.IPayment {
    private ActivityPaymentBinding binding;
    private ProgressDialog progressDialog;
    private List<Payment> lists;
    private PaymentAdapter adapter;
    private EventBus eventBus;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventBus = new EventBus();
        initProgressDialog();
        initListener();
        initRcv();
        SetupFabUtils.setUpFabWithRcv(binding.rcvListPayment, binding.fabAdd);
    }
    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang lưu...");
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnRequiredAdd.setOnClickListener(v ->
                startActivity(new Intent(PaymentActivity.this, AddPaymentActivity.class)));
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(PaymentActivity.this, AddPaymentActivity.class)));

    }

    private void initRcv() {
        lists = new ArrayList<>();
        binding.rcvListPayment.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this, lists, this);
        binding.rcvListPayment.setAdapter(adapter);
        loadData();

    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("payment")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lists.clear();
                        binding.progressBar.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            binding.txtNotExists.setVisibility(View.GONE);
                            binding.btnRequiredAdd.setVisibility(View.GONE);
                            binding.fabAdd.setVisibility(View.VISIBLE);
                            for(DataSnapshot item: snapshot.getChildren()){
                                Payment payment = item.getValue(Payment.class);
                                if(payment == null){
                                    return;
                                }
                                lists.add(payment);
                            }
                        } else {
                            binding.fabAdd.setVisibility(View.GONE);
                            binding.txtNotExists.setVisibility(View.VISIBLE);
                            binding.btnRequiredAdd.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.txtNotExists.setVisibility(View.GONE);
                        binding.btnRequiredAdd.setVisibility(View.GONE);
                        binding.fabAdd.setVisibility(View.GONE);
                        Toast.makeText(PaymentActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onClickCheckbox(Payment payment, boolean isChecked) {
        if(isUpdate){
            return;
        }
        progressDialog.show();
        DatabaseReference mRef = database.getReference("Account").child(getUid()).child("payment");
        if(!isChecked){
            setCheckedToRealtime(mRef, payment, false);
            isUpdate = true;
            CheckboxPaymentMessageEvent checkboxMessageEvent = new CheckboxPaymentMessageEvent(false);
            eventBus.post(checkboxMessageEvent);
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Payment paymentItem = item.getValue(Payment.class);
                                if(paymentItem == null){
                                    continue;
                                }
                                if (paymentItem.isUse()) {
                                    mRef.child(String.valueOf(paymentItem.getId())).child("use")
                                            .setValue(false)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    setCheckedToRealtime(mRef, payment, true);
                                                    isUpdate = true;
                                                    CheckboxPaymentMessageEvent checkboxMessageEvent = new CheckboxPaymentMessageEvent(false);
                                                    eventBus.post(checkboxMessageEvent);
                                                }
                                            });
                                    break;
                                }
                            }
                            setCheckedToRealtime(mRef, payment, true);
                            isUpdate = false;
                            CheckboxPaymentMessageEvent checkboxMessageEvent = new CheckboxPaymentMessageEvent(false);
                            eventBus.post(checkboxMessageEvent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(PaymentActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(CheckboxPaymentMessageEvent checkboxMessageEvent){
        isUpdate = checkboxMessageEvent.isChecked();
    }

    private void setCheckedToRealtime(DatabaseReference mRef, Payment payment,  boolean isChecked){
        mRef.child(String.valueOf(payment.getId())).child("use").setValue(isChecked)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if(!task.isSuccessful()){
                        Toast.makeText(PaymentActivity.this, "Lỗi, thay đổi tài khoản mặc định thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
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