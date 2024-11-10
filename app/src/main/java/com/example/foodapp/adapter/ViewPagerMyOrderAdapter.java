package com.example.foodapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodapp.databinding.ActivityMyOrderBinding;
import com.example.foodapp.fragments.ActiveOrderFragment;
import com.example.foodapp.fragments.ActiveVoucherFragment;
import com.example.foodapp.fragments.CanceledOrderFragment;
import com.example.foodapp.fragments.CompleteOrderFragment;

public class ViewPagerMyOrderAdapter extends FragmentStateAdapter {
    public ViewPagerMyOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return CompleteOrderFragment.getInstance();
            case 2:
                return CanceledOrderFragment.getInstance();
            default:
                return ActiveOrderFragment.getInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
