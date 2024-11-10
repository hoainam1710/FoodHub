package com.example.foodapp.activities;

import android.os.Bundle;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends BaseActivity {
    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}