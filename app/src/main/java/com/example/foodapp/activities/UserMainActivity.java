package com.example.foodapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityUserMainBinding;

import java.util.Objects;

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
        // Lấy ID của fragment hiện tại
        int currentFragmentId = Objects.requireNonNull(navController.getCurrentDestination()).getId();

        // Kiểm tra nếu fragment hiện tại không phải là HomeFragment
        if (currentFragmentId != R.id.dashboardAdminFragment) { // Thay R.id.homeFragment bằng ID của HomeFragment
            // Điều hướng về HomeFragment
            navController.navigate(R.id.dashboardAdminFragment);
        } else {
            // Nếu đang ở HomeFragment, kiểm tra biến cờ để thoát ứng dụng
            if (isChecked) {
                super.onBackPressed(); // Thoát ứng dụng
                return;
            }
            // Nếu nhấn Back lần đầu, hiển thị thông báo và đặt biến cờ
            isChecked = true;
            Toast.makeText(this, "Chạm lần nữa để thoát", Toast.LENGTH_SHORT).show();

            // Đặt lại biến cờ sau 2 giây
            new Handler().postDelayed(() -> isChecked = false, 2000);
        }
    }
}