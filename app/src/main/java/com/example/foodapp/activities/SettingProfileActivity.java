package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivitySettingProfileBinding;
import com.example.foodapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingProfileActivity extends BaseActivity {
    private ActivitySettingProfileBinding binding;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private User userCurrent = null;
    private static final String TAG = "SettingProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserFromRealtime();
        initProgressDialog();
        registerResultAvatar();
        initListener();

    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.txtUpdateAvatar.setOnClickListener(v -> clickChangeImage());
        binding.imgAvatar.setOnClickListener(v -> clickChangeImage());
        binding.layoutName.setOnClickListener(v -> onClickSettingName());
        binding.layoutSex.setOnClickListener(v -> showSexDialog());
        binding.layoutPhoneNumber.setOnClickListener(v -> onClickSettingPhoneNumber());
        binding.layoutPassword.setOnClickListener(v -> onClickSettingPassword());

    }

    private void onClickSettingPassword() {
        startActivity(new Intent(this, ReauthenticateAccountActivity.class));
    }

    private void onClickSettingPhoneNumber() {
        Intent intent = new Intent(this, SettingPhoneNumberActivity.class);
        intent.putExtra("phoneNumber", userCurrent.getPhoneNumber());
        intent.putExtra("checkFrom", "user");
        startActivity(intent);
    }

    private void showSexDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Chọn giới tính:");
        String[] items = new String[]{"Nam", "Nữ", "Giới tính khác"};
        builder.setItems(items, (dialog, which) -> {
            saveSexToRealtime(items[which]);
        });

        builder.create().show();

    }

    private void saveSexToRealtime(String sex) {
        progressDialog.setMessage("Đang lưu giới tính...");
        progressDialog.show();
        database.getReference("Account").child(getUid()).child("profile")
                .child("sex").setValue(sex)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Toast.makeText(SettingProfileActivity.this, "Lưu giới tính thành công", Toast.LENGTH_SHORT).show();
                        binding.txtSex.setText(sex);
                        binding.txtSex.setTextColor(ContextCompat.getColor(SettingProfileActivity.this, R.color.blue_grey));
                    } else
                        Toast.makeText(SettingProfileActivity.this, "Lưu giới tính thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void onClickSettingName() {
        Intent intent = new Intent(this, SettingNameActivity.class);
        intent.putExtra("name", userCurrent.getName());
        startActivity(intent);
    }

    private void getUserFromRealtime() {
        database.getReference("Account").child(getUid()).child("profile")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userCurrent = snapshot.getValue(User.class);
                            if(userCurrent == null){
                                return;
                            }
                            String name = userCurrent.getName();
                            String email = userCurrent.getEmail();
                            String sex  = userCurrent.getSex();
                            String phoneNumber = userCurrent.getPhoneNumber();

                            String hidePhoneNumber = null;
                            // che phoneNumber
                            if(phoneNumber != null){
                                String lastPhoneNumber = phoneNumber.substring(7);
                                hidePhoneNumber = "*******"+ lastPhoneNumber;
                            }
                            setInformation(binding.txtPhoneNumber, hidePhoneNumber);

                            if(name == null || name.isEmpty()){
                                String id = userCurrent.getId();
                                String subId = id.substring(id.length()-6);
                                binding.txtName.setText("User_"+ subId);
                            } else binding.txtName.setText(name);
//                            setInformation(binding.txtName, name);
                            setInformation(binding.txtEmail, email);
                            setInformation(binding.txtSex, sex);


                            Glide.with(SettingProfileActivity.this)
                                    .load(userCurrent.getAvatar()).
                                    placeholder(R.drawable.avatar_default)
                                    .error(R.drawable.avatar_default)
                                    .into(binding.imgAvatar);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getUserCancelled: "+ error.getMessage());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setInformation(TextView textView, String information){
        if(information == null){
            textView.setText("Chưa xác định");
            textView.setTextColor(ContextCompat.getColor(this, R.color.enable2));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.blue_grey));
            textView.setText(information);
        }

    }

    private void registerResultAvatar() {
        activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    Intent intent = o.getData();
                    if(intent == null){
                        return;
                    }
                    Uri uri = intent.getData();
                    if(uri == null){
                        return;
                    }
                    saveAvatarToRealtime(uri);

                });
    }

    private void saveAvatarToRealtime(Uri uri) {
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.show();
        storage.getReference("Avatar").child(getUid()).putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    task.addOnCompleteListener(task12 -> {
                        if(task12.isSuccessful()){
                            Uri urlDownload = task12.getResult();
                            if(urlDownload == null){
                                return;
                            }
                            database.getReference("Account").child(getUid()).child("profile")
                                    .child("avatar").setValue(urlDownload.toString())
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if(task1.isSuccessful()){
                                            Glide.with(SettingProfileActivity.this)
                                                    .load(urlDownload).
                                                    placeholder(R.drawable.avatar_default)
                                                    .error(R.drawable.avatar_default)
                                                    .into(binding.imgAvatar);
                                            Toast.makeText(SettingProfileActivity.this, "Lưu ảnh thành công!", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(SettingProfileActivity.this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saveAvatarToRealtimeFail: "+ e.getMessage());
                });
    }

    private void clickChangeImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }
}