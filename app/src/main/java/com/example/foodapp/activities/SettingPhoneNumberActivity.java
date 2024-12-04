package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivitySettingPhoneNumberBinding;
import com.example.foodapp.utils.ValidateStringUtils;

import java.util.Objects;

public class SettingPhoneNumberActivity extends BaseActivity {
    private ActivitySettingPhoneNumberBinding binding;
    private ProgressDialog progressDialog;
    private String oldPhoneNumber = "";
    public static final int ADMIN_TYPE = 0;
    public static final int USER_TYPE = 1;
    private int checkType = USER_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressDialog();
        setUiDefault();
        getResultIntent();
        setOnTextChange();
        initListener();

    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void getResultIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        oldPhoneNumber = intent.getStringExtra("phoneNumber");
        if(oldPhoneNumber != null){
            binding.edtPhoneNumber.setText(oldPhoneNumber);
        } else binding.edtPhoneNumber.setText("");

        String type = intent.getStringExtra("checkFrom");
        if(type != null && type.equals("user")){
            checkType = USER_TYPE;
        } else checkType = ADMIN_TYPE;

    }

    private void setUiDefault(){
        binding.txtSave.setEnabled(false);
        binding.txtSave.setTextColor(ContextCompat.getColor(this, R.color.grey_btn_enable));
    }

    private void initListener() {
        binding.txtCancel.setOnClickListener(v -> finish());
        binding.txtSave.setOnClickListener(v -> {
            String newPhoneNumber = Objects.requireNonNull(binding.edtPhoneNumber.getText()).toString().trim();
            if(ValidateStringUtils.validatePhoneNumber(binding.layoutPhoneNumber, newPhoneNumber)){
                progressDialog.show();
                if(checkType == USER_TYPE){
                    savePhoneNumberUser(newPhoneNumber);
                } else savePhoneNumberAdmin(newPhoneNumber);
            }
        });
    }

    private void savePhoneNumberAdmin(String newPhoneNumber) {
        database.getReference("Admin").child(getUid())
                .child("phoneNumber").setValue(newPhoneNumber)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Toast.makeText(SettingPhoneNumberActivity.this, "Lưu số điện thoại thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(SettingPhoneNumberActivity.this, "Lưu số điện thoại thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePhoneNumberUser(String newPhoneNumber){
        database.getReference("Account").child(getUid()).child("profile")
                .child("phoneNumber").setValue(newPhoneNumber)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Toast.makeText(SettingPhoneNumberActivity.this, "Lưu số điện thoại thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(SettingPhoneNumberActivity.this, "Lưu số điện thoại thất bại", Toast.LENGTH_SHORT).show();
                });
    }


    private void setOnTextChange(){
        binding.edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPhoneNumber =  s.toString();
                if(!newPhoneNumber.isEmpty() && !newPhoneNumber.equals(oldPhoneNumber)){
                    binding.txtSave.setEnabled(true);
                    binding.txtSave.setTextColor(ContextCompat.getColor(SettingPhoneNumberActivity.this, R.color.red));
                }
                else {
                    binding.txtSave.setEnabled(false);
                    binding.txtSave.setTextColor(ContextCompat.getColor(SettingPhoneNumberActivity.this, R.color.grey_btn_enable));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}