package com.example.foodapp.activities;

import android.os.Bundle;

import com.example.foodapp.adapter.ViewPagerVoucherAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityVoucherBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class VoucherActivity extends BaseActivity {
    private ActivityVoucherBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private ViewPagerVoucherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewPagerVoucher();
        initListener();

    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void initViewPagerVoucher() {
        adapter = new ViewPagerVoucherAdapter(this);
        binding.viewPagerVoucher.setAdapter(adapter);

        tabLayoutMediator = new TabLayoutMediator(binding.tabLayoutVoucher, binding.viewPagerVoucher, (tab, i) -> {
            if(i == 0){
                tab.setText("Có sẵn");
            } else tab.setText("Không khả dụng");
        });
        tabLayoutMediator.attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayoutMediator.detach();
    }
}