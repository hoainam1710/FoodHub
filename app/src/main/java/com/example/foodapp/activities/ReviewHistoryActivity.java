package com.example.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.example.foodapp.adapter.ViewPagerReviewHistoryAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityReviewHistoryBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ReviewHistoryActivity extends BaseActivity {
    private ActivityReviewHistoryBinding binding;
    private TabLayoutMediator mediator;
    public static final int REVIEW_ACTIVITY_TYPE = 0;
    public static final int ACCOUNT_FRAGMENT_TYPE = 1;
    private int currentType = ACCOUNT_FRAGMENT_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkFromIntent();
        initViewPager();
        initListener();

    }

    private void checkFromIntent() {
        Intent intent = getIntent();
        String checkFrom = intent.getStringExtra("from");
        if(checkFrom == null){
            return;
        }
        if(checkFrom.equals("AccountFragment")){
            currentType = ACCOUNT_FRAGMENT_TYPE;
        } else {
            currentType = REVIEW_ACTIVITY_TYPE;
        }

    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> {
            if(currentType == ACCOUNT_FRAGMENT_TYPE){
                finish();
            } else {
                startActivity(new Intent(ReviewHistoryActivity.this, UserMainActivity.class));
                finishAffinity();
            }
        });
    }

    private void initViewPager() {
        ViewPagerReviewHistoryAdapter adapter = new ViewPagerReviewHistoryAdapter(this);
        binding.viewPager.setAdapter(adapter);

        mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, i) -> {
            if(i == 0){
                tab.setText("Chưa đánh giá");
            } else tab.setText("Đã đánh giá");
        });
        mediator.attach();

        binding.viewPager.setCurrentItem(1, true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediator.detach();
    }
}