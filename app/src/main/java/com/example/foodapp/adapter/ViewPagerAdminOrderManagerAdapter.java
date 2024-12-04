package com.example.foodapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodapp.fragments.AdminActiveOrderFragment;
import com.example.foodapp.fragments.AdminCanceledOrderFragment;
import com.example.foodapp.fragments.AdminCompletedOrderFragment;

public class ViewPagerAdminOrderManagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdminOrderManagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return AdminCompletedOrderFragment.getInstance();
            case 2: return AdminCanceledOrderFragment.getInstance();
            default: return AdminActiveOrderFragment.getInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
