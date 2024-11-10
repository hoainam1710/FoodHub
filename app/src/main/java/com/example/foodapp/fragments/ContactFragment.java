package com.example.foodapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodapp.R;
import com.example.foodapp.databinding.FragmentContactBinding;


public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}