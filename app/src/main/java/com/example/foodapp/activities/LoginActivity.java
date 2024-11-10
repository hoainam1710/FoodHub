package com.example.foodapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityLoginBinding;
import com.example.foodapp.model.User;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setColorRadioButton();
        initProgressDialog();
        initListener();

    }

    private void initListener() {
        binding.layoutRegister.setOnClickListener(v -> onClickRegister());

        binding.btnLogin.setOnClickListener(v -> onClickLogin());

        binding.txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void onClickRegister(){
        if(binding.rdoUser.isChecked()){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        } else Toast.makeText(this, "Bạn không có quyền đăng kí tài khoản quản trị viên", Toast.LENGTH_SHORT).show();
    }

    private void onClickLogin() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
        String roleLogin = binding.rdoUser.isChecked() ? "user" : "admin";

        if (ValidateStringUtils.validateEmail(email, binding.edtLayoutEmail)
                && ValidateStringUtils.validatePassword(password, binding.edtLayoutPassword)) {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(roleLogin.equals("user")){
                                loginWithUser(roleLogin);
                            } else {
                                loginWithAdmin(roleLogin);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loginWithAdmin(String roleLogin) {
        database.getReference("Admin").child(getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                if (roleLogin.equals(user.getRole())) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập quản trị viên thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                    finishAffinity();
                                } else
                                    Toast.makeText(LoginActivity.this, "Chỉ người dùng mới có quyền đăng nhập", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(LoginActivity.this, "Tài khoản quản trị viên không tồn tại", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập quản trị viên thất bại!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "loginCancelled: "+ error.getMessage());
                    }
                });
    }

    private void loginWithUser(String roleLogin){
        database.getReference("Account").child(getUid()).child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                if (roleLogin.equals(user.getRole())) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập người dùng thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                    finishAffinity();
                                } else
                                    Toast.makeText(LoginActivity.this, "Chỉ quản trị viên mới có quyền đăng nhập", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(LoginActivity.this, "Tài khoản người dùng không tồn tại", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập người dùng thất bại!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "loginCancelled: "+ error.getMessage());
                    }
                });
    }


    private void setColorRadioButton() {
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.rdo_bg_color);
        binding.rdoAdmin.setButtonTintList(colorStateList);
        binding.rdoUser.setButtonTintList(colorStateList);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang đăng nhập...");
    }
}