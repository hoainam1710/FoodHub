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
import com.example.foodapp.databinding.ActivitySettingNameBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SettingNameActivity extends BaseActivity {
    private ActivitySettingNameBinding binding;
    private ProgressDialog progressDialog;
    private String oldName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressDialog();
        getResultIntent();
        setOnTextChange();
        initListener();

    }
    
    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initListener() {
        binding.txtCancel.setOnClickListener(v -> finish());
        binding.txtSave.setOnClickListener(v -> {
            String newName = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
            if(validateName(binding.layoutName, newName)){
                progressDialog.show();
                database.getReference("Account").child(getUid()).child("profile")
                        .child("name").setValue(newName)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(SettingNameActivity.this, "Lưu tên thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else
                                Toast.makeText(SettingNameActivity.this, "Lưu tên thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }


    private void getResultIntent() {
        Intent intent = getIntent();
        oldName = intent.getStringExtra("name");
        binding.txtSave.setEnabled(false);
        binding.txtSave.setTextColor(ContextCompat.getColor(this, R.color.grey_btn_enable));
        if(oldName != null){
            binding.edtName.setText(oldName);
        }

    }

    private boolean validateName(TextInputLayout layout, String name){
       if(name.length() > 30){
            layout.setError("Tên không quá 30 kí tự.");
            return false;
        } 
        layout.setError(null);
        return true;
    }

    private void setOnTextChange(){
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName =  s.toString();
                if(!newName.isEmpty() && !newName.equals(oldName)){
                    binding.txtSave.setEnabled(true);
                    binding.txtSave.setTextColor(ContextCompat.getColor(SettingNameActivity.this, R.color.red));
                } else {
                    binding.txtSave.setEnabled(false);
                    binding.txtSave.setTextColor(ContextCompat.getColor(SettingNameActivity.this, R.color.grey_btn_enable));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
