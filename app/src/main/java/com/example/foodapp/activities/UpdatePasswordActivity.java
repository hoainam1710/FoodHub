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
import com.example.foodapp.databinding.ActivityUpdatePasswordBinding;
import com.example.foodapp.utils.ValidateStringUtils;

import java.util.Objects;

public class UpdatePasswordActivity extends BaseActivity {
    private ActivityUpdatePasswordBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdatePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressDialog();
        setOnTextChange();
        initListener();

    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initListener() {
        binding.txtCancel.setOnClickListener(v -> {
//            startActivity(new Intent(UpdatePasswordActivity.this, SettingProfileActivity.class));
            finish();
        });
        binding.txtSave.setOnClickListener(v -> {
            String newPassword = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
            if(ValidateStringUtils.validatePassword(newPassword, binding.layoutPassword)){
                progressDialog.show();
                Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(UpdatePasswordActivity.this,
                                        "Thay đổi mật khẩu thành công.\nVui lòng đăng nhập lại với mật khẩu mới."
                                        , Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(UpdatePasswordActivity.this, LoginActivity.class));
                                finishAffinity();
                            } else
                                Toast.makeText(UpdatePasswordActivity.this,
                                        "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }



    private void setOnTextChange(){
        binding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPassword =  s.toString();
                if(!newPassword.isEmpty()){
                    binding.txtSave.setEnabled(true);
                    binding.txtSave.setTextColor(ContextCompat.getColor(UpdatePasswordActivity.this, R.color.red));
                }
                else {
                    binding.txtSave.setEnabled(false);
                    binding.txtSave.setTextColor(ContextCompat.getColor(UpdatePasswordActivity.this, R.color.grey_btn_enable));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}