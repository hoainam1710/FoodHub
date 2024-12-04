package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.activities.ReauthenticateAccountActivity;
import com.example.foodapp.activities.SettingPhoneNumberActivity;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentAccountAdminBinding;
import com.example.foodapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AccountAdminFragment extends BaseFragment {
    private FragmentAccountAdminBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ProgressDialog progressDialog;
    private static final String TAG = "AccountAdminFragment";
    private User admin;

    public static AccountAdminFragment getInstance() {
        return new AccountAdminFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentAccountAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserFromRealtime();
        initProgressDialog();
        registerResultAvatar();
        initListener();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang xử lý");
    }


    private void initListener() {
        binding.txtUpdateAvatar.setOnClickListener(v -> clickChangeImage());
        binding.imgAvatar.setOnClickListener(v -> clickChangeImage());
        binding.layoutPhoneNumber.setOnClickListener(v -> onClickSettingPhoneNumber());
        binding.layoutPassword.setOnClickListener(v -> onClickSettingPassword());

    }

    private void onClickSettingPassword() {
        startActivity(new Intent(requireActivity(), ReauthenticateAccountActivity.class));
    }

    private void onClickSettingPhoneNumber() {
        Intent intent = new Intent(requireActivity(), SettingPhoneNumberActivity.class);
        intent.putExtra("phoneNumber", admin.getPhoneNumber());
        intent.putExtra("checkFrom", "admin");
        startActivity(intent);
    }


    private void getUserFromRealtime() {
        database.getReference("Admin").child(getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            admin = snapshot.getValue(User.class);
                            if(admin == null){
                                return;
                            }
                            String email = admin.getEmail();
                            binding.txtEmail.setText(email);
                            String phoneNumber = admin.getPhoneNumber();

                            String hidePhoneNumber = null;
                            // che phoneNumber
                            if(phoneNumber != null){
                                String lastPhoneNumber = phoneNumber.substring(7);
                                hidePhoneNumber = "*******"+ lastPhoneNumber;
                            }
                            setInformation(binding.txtPhoneNumber, hidePhoneNumber);
                            Glide.with(requireActivity())
                                    .load(admin.getAvatar()).
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
            textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.enable2));
        } else {
            textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_grey));
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
        progressDialog.show();
        storage.getReference("Admin").child(getUid()).putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    task.addOnCompleteListener(task12 -> {
                        if(task12.isSuccessful()){
                            Uri urlDownload = task12.getResult();
                            if(urlDownload == null){
                                return;
                            }
                            database.getReference("Admin").child(getUid())
                                    .child("avatar").setValue(urlDownload.toString())
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if(task1.isSuccessful()){
                                            Glide.with(requireActivity())
                                                    .load(urlDownload).
                                                    placeholder(R.drawable.avatar_default)
                                                    .error(R.drawable.avatar_default)
                                                    .into(binding.imgAvatar);
                                            Toast.makeText(requireActivity(), "Lưu ảnh thành công!", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(requireActivity(), "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saveAvatarToRealtimeFail: "+ e.getMessage());
                });
    }

    private void clickChangeImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }


}