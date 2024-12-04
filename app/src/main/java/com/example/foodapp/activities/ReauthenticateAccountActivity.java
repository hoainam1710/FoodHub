package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityReauthenticateAccountBinding;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.Objects;

public class ReauthenticateAccountActivity extends BaseActivity {
    private ActivityReauthenticateAccountBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReauthenticateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressDialog();
        initListener();

    }
    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác minh...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initListener() {
        binding.txtCancel.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> onClickSubmit());
    }

    private void onClickSubmit() {
        String emailCurrent = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String passwordCurrent = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

        if(ValidateStringUtils.validateEmail(emailCurrent, binding.layoutEmail)
        && ValidateStringUtils.validatePassword(passwordCurrent, binding.layoutPassword)){
            progressDialog.show();
            AuthCredential authCredential = EmailAuthProvider.getCredential(emailCurrent, passwordCurrent);
            Objects.requireNonNull(mAuth.getCurrentUser()).reauthenticate(authCredential)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(ReauthenticateAccountActivity.this, "Xác minh tài khoản thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReauthenticateAccountActivity.this, UpdatePasswordActivity.class));
                        } else Toast.makeText(this, "Xác minh tài khoản thất bại.\nEmail hoặc mật khẩu không hợp lệ.", Toast.LENGTH_SHORT).show();
                    });

        }


    }
}