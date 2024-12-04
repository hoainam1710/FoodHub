package com.example.foodapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAdminMainBinding;
import com.example.foodapp.databinding.HeaderNavAdminBinding;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AdminMainActivity extends BaseActivity {
    private ActivityAdminMainBinding binding;
    private boolean isChecked = false;
    private NavController navController;
    private AppBarConfiguration configuration;
    private static final String TAG = "AdminMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarAdmin);
        initNav();
        getInformationAdmin();
        onClickLogOut();

    }

    private void getInformationAdmin() {
        View headerView = binding.navAdmin.getHeaderView(0);
        HeaderNavAdminBinding headerNavAdminBinding = HeaderNavAdminBinding.bind(headerView);

        database.getReference("Admin").child(getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User admin = snapshot.getValue(User.class);
                            if(admin != null){
                                headerNavAdminBinding.txtEmail.setText(admin.getEmail());
                                Glide.with(AdminMainActivity.this)
                                        .load(admin.getAvatar())
                                        .error(R.drawable.avatar_default)
                                        .placeholder(R.drawable.avatar_default)
                                        .into(headerNavAdminBinding.imgAvatar);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getAdminCancelled: "+ error.getMessage());
                    }
                });
    }

    private void onClickLogOut(){
        binding.navAdmin.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.logout){
                showDialogLogOut();
                return true;
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                binding.mainAdmin.closeDrawer(binding.navAdmin);
                return true;
            }
        });
    }

    private void showDialogLogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Xác nhận đăng xuất tài khoản")
                .setCancelable(false)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void initNav(){
        navController = Navigation.findNavController(this, R.id.fragment_container_admin);

        configuration = new AppBarConfiguration.Builder(
                R.id.dashboardAdminFragment,
                R.id.chatAdminFragment,
                R.id.accountAdminFragment,
                R.id.orderManagerFragment,
                R.id.customerManagerFragment,
                R.id.reviewManagerFragment,
                R.id.foodManagerFragment,
                R.id.categoryManagerFragment)
                .setOpenableLayout(binding.mainAdmin)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        Drawable toggleIcon = binding.toolbarAdmin.getNavigationIcon();
        if (toggleIcon != null) {
            toggleIcon.setTint(R.drawable.ic_toggle);
        }
        NavigationUI.setupWithNavController(binding.bottomNavAdmin, navController);
        NavigationUI.setupWithNavController(binding.navAdmin, navController);
    }


    public void onBackPressed() {
        if (binding.mainAdmin.isDrawerOpen(binding.navAdmin)) {
            // Đóng Navigation View nếu nó đang mở
            binding.mainAdmin.closeDrawer(binding.navAdmin);
        } else {
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

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, configuration) || super.onSupportNavigateUp();
    }

}