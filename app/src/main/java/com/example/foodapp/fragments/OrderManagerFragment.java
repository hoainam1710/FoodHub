package com.example.foodapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodapp.adapter.ViewPagerAdminOrderManagerAdapter;
import com.example.foodapp.databinding.FragmentOrderManagerBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderManagerFragment extends Fragment {
    private FragmentOrderManagerBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentOrderManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewPager();

    }

    private void initViewPager() {
        ViewPagerAdminOrderManagerAdapter adapter = new ViewPagerAdminOrderManagerAdapter(requireActivity());
        binding.viewPager.setAdapter(adapter);
        tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, i) -> {
            switch (i){
                case 0:
                    tab.setText("Chở xử lý");
                    return;
                case 1:
                    tab.setText("Đã hoàn thành");
                    return;
                default: tab.setText("Đã hủy");
            }
        });
        tabLayoutMediator.attach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        tabLayoutMediator.detach();
    }
}

