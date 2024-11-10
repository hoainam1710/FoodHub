package com.example.foodapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityUserMainBinding;

public class UserMainActivity extends BaseActivity {
    private ActivityUserMainBinding binding;
    private NavController navController;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initNav();

    }

    private void initNav() {
        navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }

    @Override
    public void onBackPressed() {
        if(!isChecked){
            Toast.makeText(this, "Nhấn trở lại lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();
            isChecked = true;
            return;
        }
        super.onBackPressed();
    }
}