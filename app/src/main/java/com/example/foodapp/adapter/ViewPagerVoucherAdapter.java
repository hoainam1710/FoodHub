package com.example.foodapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodapp.fragments.ActiveVoucherFragment;
import com.example.foodapp.fragments.InactiveVoucherFragment;


public class ViewPagerVoucherAdapter extends FragmentStateAdapter {

    public ViewPagerVoucherAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new ActiveVoucherFragment();
        } else if(position == 1){
            return new InactiveVoucherFragment();
        }
        return new ActiveVoucherFragment();

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
