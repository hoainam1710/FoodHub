package com.example.foodapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodapp.fragments.CompletedReviewFragment;
import com.example.foodapp.fragments.NotReviewFragment;

public class ViewPagerReviewHistoryAdapter extends FragmentStateAdapter {
    public ViewPagerReviewHistoryAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1){
            return CompletedReviewFragment.getInstance();
        } else return NotReviewFragment.getInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
