package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAddShippingAddressBinding;
import com.example.foodapp.model.ShippingAddress;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddShippingAddressActivity extends BaseActivity {
    private ActivityAddShippingAddressBinding binding;
    private ShippingAddress shippingAddress;
    private ProgressDialog progressDialog;
    public static final int ADD_CHECK = 0;
    public static final int UPDATE_CHECK = 1;
    public static final int CART_CHECK = 2;
    private int checkCurrent = ADD_CHECK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddShippingAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressBar();
        getResultIntent();
        initListener();

    }

    private void initProgressBar(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void getResultIntent() {
        Intent intent = getIntent();
        if(intent != null){
            String resultFromCartActivity = intent.getStringExtra("cartActivityNextToShippingAddress");
            if(resultFromCartActivity != null && resultFromCartActivity.equals("cart")){
                checkCurrent = CART_CHECK;
                binding.btnSaveAddress.setText("Tiếp tục");
                return;
            }
            Bundle bundle = intent.getExtras();
            if(bundle == null){
                return;
            }
            checkCurrent = UPDATE_CHECK;
            shippingAddress = (ShippingAddress) bundle.getSerializable("shippingAddress");
            if(shippingAddress == null){
                return;
            }
            binding.txtTitle.setText("Thay đổi địa chỉ giao hàng");
            binding.btnSaveAddress.setText("Lưu thay đổi");
            binding.edtName.setText(shippingAddress.getName());
            binding.edtThanhPho.setText(shippingAddress.getThanhPho());
            binding.edtQuan.setText(shippingAddress.getQuan());
            binding.edtPhuong.setText(shippingAddress.getPhuong());
            binding.edtSoNha.setText(shippingAddress.getSoNha());

        }
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnSaveAddress.setOnClickListener(v -> {
            if(checkCurrent == ADD_CHECK) {
                addAddress();
            } else if (checkCurrent == CART_CHECK){
                addAddress();
            } else {
                updateAddress();
            }
        });
    }

    private void updateAddress() {
        String name = binding.edtName.getText().toString().trim();
        String phoneNumber = binding.edtPhoneNumber.getText().toString().trim();
        String thanhPho = binding.edtThanhPho.getText().toString().trim();
        String quan = binding.edtQuan.getText().toString().trim();
        String phuong = binding.edtPhuong.getText().toString().trim();
        String strSoNha = binding.edtSoNha.getText().toString().trim();

        progressDialog.show();
        boolean isCheckAllValidate = ValidateStringUtils.validateEmpty(binding.layoutName, name)
                && ValidateStringUtils.validatePhoneNumber(binding.layoutPhoneNumber, phoneNumber)
                && ValidateStringUtils.validateEmpty(binding.layoutThanhPho, thanhPho)
                && ValidateStringUtils.validateEmpty(binding.layoutQuan, quan)
                && ValidateStringUtils.validateEmpty(binding.layoutPhuong, phuong)
                && ValidateStringUtils.validateEmpty(binding.layoutSoNha, strSoNha);
        if(isCheckAllValidate){
            ShippingAddress shippingAddressUpdate = new ShippingAddress(shippingAddress.getId(), name, phoneNumber, thanhPho, quan, phuong, strSoNha, shippingAddress.isUse());

            database.getReference("Account").child(getUid()).child("shippingAddress")
                    .child(String.valueOf(shippingAddress.getId())).setValue(shippingAddressUpdate)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Thay đổi địa chỉ thành công", Toast.LENGTH_SHORT).show();
                            clearEditText();
                        } else {
                            Toast.makeText(this, "Thay đổi địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    private void addAddress() {
        String name = binding.edtName.getText().toString().trim();
        String phoneNumber = binding.edtPhoneNumber.getText().toString().trim();
        String thanhPho = binding.edtThanhPho.getText().toString().trim();
        String quan = binding.edtQuan.getText().toString().trim();
        String phuong = binding.edtPhuong.getText().toString().trim();
        String strSoNha = binding.edtSoNha.getText().toString().trim();

        boolean isCheckAllValidate = ValidateStringUtils.validateEmpty(binding.layoutName, name)
                && ValidateStringUtils.validatePhoneNumber(binding.layoutPhoneNumber, phoneNumber)
                && ValidateStringUtils.validateEmpty(binding.layoutThanhPho, thanhPho)
                && ValidateStringUtils.validateEmpty(binding.layoutQuan, quan)
                && ValidateStringUtils.validateEmpty(binding.layoutPhuong, phuong)
                && ValidateStringUtils.validateEmpty(binding.layoutSoNha, strSoNha);
        if(isCheckAllValidate){
            progressDialog.show();
            int id = (int) System.currentTimeMillis();

            DatabaseReference mRef = database.getReference("Account").child(getUid()).child("shippingAddress");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ShippingAddress shippingAddressAdd;
                    if (snapshot.exists()) {
                        shippingAddressAdd = new ShippingAddress(id, name, phoneNumber,thanhPho, quan, phuong, strSoNha);
                    } else {
                        shippingAddressAdd = new ShippingAddress(id, name, phoneNumber,thanhPho, quan, phuong, strSoNha, true);
                    }
                    mRef.child(String.valueOf(id)).setValue(shippingAddressAdd)
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    if(checkCurrent == CART_CHECK){
                                        checkPaymentExists();
                                    } else {
                                        Toast.makeText(AddShippingAddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                                        clearEditText();
                                    }

                                } else {
                                    Toast.makeText(AddShippingAddressActivity.this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(AddShippingAddressActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void checkPaymentExists(){
        database.getReference("Account").child(getUid()).child("payment")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            startActivity(new Intent(AddShippingAddressActivity.this, AddPaymentActivity.class));
                        } else  {
                            startActivity(new Intent(AddShippingAddressActivity.this, CheckOutActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddShippingAddressActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearEditText(){
        binding.edtName.setText("");
        binding.edtThanhPho.setText("");
        binding.edtPhuong.setText("");
        binding.edtQuan.setText("");
        binding.edtSoNha.setText("");
        binding.edtPhoneNumber.setText("");

    }

//    private void setEnableButton(AppCompatButton button){
//        button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_enable_bg));
//        button.setTextColor(ContextCompat.getColor(this,R.color.text_enable));
//        button.setEnabled(false);
//
//    }
}