package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityRegisterBinding;
import com.example.foodapp.model.User;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initProgressDialog();
        initListener();

    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang đăng ký...");
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> createAccountUser());
    }

    private void createAccountUser() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
        String confirm = Objects.requireNonNull(binding.edtConfirmPass.getText()).toString().trim();
        String role = "user";

        if(ValidateStringUtils.validateEmail(email, binding.edtLayoutEmail)
        && ValidateStringUtils.validatePassword(password, binding.edtLayoutPassword)
        && ValidateStringUtils.validateConfirmPassword(confirm, password, binding.edtLayoutConfirmPass)){
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản user thành công!", Toast.LENGTH_SHORT).show();
                            saveToRealtime(email, role);
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void saveToRealtime(String email, String role){
        if(mAuth.getCurrentUser() != null){
            String id = getUid();
            User user = new User(id, email, role);
            DatabaseReference mRef = database.getReference("Account");
            mRef.child(id).child("profile").setValue(user)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Save account user to realtime success");
                        } else {
                            Log.d(TAG, "Save account user to realtime fail", task.getException());
                        }
                    });
        }
    }
}