package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.ShippingAddressAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityShippingAddressBinding;
import com.example.foodapp.events.CheckboxAddressMessageEvent;
import com.example.foodapp.events.CheckboxPaymentMessageEvent;
import com.example.foodapp.model.ShippingAddress;
import com.example.foodapp.utils.SetupFabUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ShippingAddressActivity extends BaseActivity implements ShippingAddressAdapter.IShippingAddress {
    private ActivityShippingAddressBinding binding;
    private ProgressDialog  progressDialog;
    private ShippingAddressAdapter adapter;
    private List<ShippingAddress> lists;
    private EventBus eventBus;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShippingAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventBus = new EventBus();
        initProgressDialog();
        initRcv();
        initListener();
        SetupFabUtils.setUpFabWithRcv(binding.rcvListAddress, binding.fabAddAddress);

    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang lưu...");
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.fabAddAddress.setOnClickListener(v -> {
            startActivity(new Intent(this, AddShippingAddressActivity.class));
        });

        binding.btnRequiredAddAddress.setOnClickListener(v -> startActivity(new Intent(ShippingAddressActivity.this, AddShippingAddressActivity.class)));
    }

    private void initRcv() {
        lists = new ArrayList<>();
        binding.rcvListAddress.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShippingAddressAdapter(lists, this);
        binding.rcvListAddress.setAdapter(adapter);
        loadData();

    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("shippingAddress")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lists.clear();
                        binding.progressBar.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            binding.txtNotExists.setVisibility(View.GONE);
                            binding.btnRequiredAddAddress.setVisibility(View.GONE);
                            binding.fabAddAddress.setVisibility(View.VISIBLE);
                            for(DataSnapshot item: snapshot.getChildren()){
                                ShippingAddress shippingAddress = item.getValue(ShippingAddress.class);
                                if(shippingAddress == null){
                                    continue;
                                }
                                lists.add(shippingAddress);
                            }
                        } else {
                            binding.txtNotExists.setVisibility(View.VISIBLE);
                            binding.btnRequiredAddAddress.setVisibility(View.VISIBLE);
                            binding.fabAddAddress.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @Override
    public void onClickCheckbox(ShippingAddress shippingAddress, boolean isChecked) {
        // đặt biến isUpdate để khi checkbox cũ bị bỏ chọn thì không bị gọi lại hàm onClickCheckbox()
        if(isUpdate){
            return;
        }
        progressDialog.show();
        DatabaseReference mRef = database.getReference("Account").child(getUid()).child("shippingAddress");
        if (!isChecked) {
            setCheckedToRealtime(mRef, shippingAddress, false);
            isUpdate = true;
            CheckboxAddressMessageEvent checkboxMessageEvent = new CheckboxAddressMessageEvent(false);
            eventBus.post(checkboxMessageEvent);
            return;
        }
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        ShippingAddress shippingAddressItem = item.getValue(ShippingAddress.class);
                        if(shippingAddressItem == null){
                            continue;
                        }
                        if (shippingAddressItem.isUse()) {
                            mRef.child(String.valueOf(shippingAddressItem.getId()))
                                    .child("use").setValue(false)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            setCheckedToRealtime(mRef, shippingAddress, true);
                                            isUpdate = true;
                                            CheckboxAddressMessageEvent checkboxMessageEvent = new CheckboxAddressMessageEvent(false);
                                            eventBus.post(checkboxMessageEvent);
                                        }
                                    });
                            break;
                        }
                    }
                    setCheckedToRealtime(mRef, shippingAddress, true);
                    isUpdate = true;
                    CheckboxAddressMessageEvent checkboxMessageEvent = new CheckboxAddressMessageEvent(false);
                    eventBus.post(checkboxMessageEvent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ShippingAddressActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(CheckboxAddressMessageEvent checkboxAddressMessageEvent){
        isUpdate = checkboxAddressMessageEvent.isChecked();
    }

    private void setCheckedToRealtime(DatabaseReference ref, ShippingAddress shippingAddress, boolean isChecked){
        ref.child(String.valueOf(shippingAddress.getId()))
                .child("use")
                .setValue(isChecked)
                .addOnCompleteListener(task1 -> {
                    progressDialog.dismiss();
                    if(!task1.isSuccessful()){
                        Toast.makeText(ShippingAddressActivity.this, "Lỗi, thay đổi địa chỉ mặc định thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void updateAddress(ShippingAddress shippingAddress) {
        Intent intent = new Intent(this, AddShippingAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("shippingAddress", shippingAddress);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteAddress(ShippingAddress shippingAddress) {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("shippingAddress")
                .child(String.valueOf(shippingAddress.getId())).removeValue()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(ShippingAddressActivity.this, "Xóa địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ShippingAddressActivity.this, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show();
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