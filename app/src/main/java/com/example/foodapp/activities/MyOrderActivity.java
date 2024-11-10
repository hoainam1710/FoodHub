package com.example.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodapp.adapter.ViewPagerMyOrderAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityMyOrderBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyOrderActivity extends BaseActivity {
    private ActivityMyOrderBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private String checkFromActivity;   // check activity gửi intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTabLayoutMediator();
        getResultFromIntent();
        initListener();

    }

    private void getResultFromIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        checkFromActivity = intent.getStringExtra("from");
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> {
            if(checkFromActivity.equals("AccountFragment")){
                finish();
            } else if(checkFromActivity.equals("CheckOutActivity")){
                startActivity(new Intent(MyOrderActivity.this, UserMainActivity.class));
                finishAffinity();
            }
        });
    }

    private void initTabLayoutMediator() {
        ViewPagerMyOrderAdapter adapter = new ViewPagerMyOrderAdapter(this);
        binding.viewPagerMyOrder.setAdapter(adapter);
        tabLayoutMediator = new TabLayoutMediator(binding.tabLayoutMyOder, binding.viewPagerMyOrder, (tab, i) -> {
            switch (i){
                case 0: tab.setText("Đang xử lý");
                        break;
                case 1: tab.setText("Đã hoàn thành");
                        break;
                case 2: tab.setText("Đã hủy");
                        break;
            }
        });
        tabLayoutMediator.attach();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayoutMediator.detach();
    }
}