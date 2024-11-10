package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityForgotPasswordBinding;
import com.example.foodapp.utils.ValidateStringUtils;
import java.util.Objects;

public class ForgotPasswordActivity extends BaseActivity {
    private ActivityForgotPasswordBinding binding;
    private ProgressDialog progressDialog;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang xử lý...");
        initListener();

    }

    private void initListener() {
        binding.txtCancel.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> {
            sendPasswordResetEmail();
        });
    }

    private void sendPasswordResetEmail() {
        progressDialog.show();
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        if(ValidateStringUtils.validateEmail(email, binding.layoutEmail)){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Link đặt lại mật khẩu đã được gửi tới email đăng kí của bạn.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Log.d(TAG, "sendPasswordResetEmailCancel: "+ Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(this, "Link đặt lại mật khẩu được gửi không thành công.", Toast.LENGTH_SHORT).show();
                        }

                    });

        }
    }
}