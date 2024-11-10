package com.example.foodapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.foodapp.databinding.BottomSheetDialogCancelOrderSuccessBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetNotifySuccessFragment extends BottomSheetDialogFragment {
    private BottomSheetDialogCancelOrderSuccessBinding binding;
    private final String message;

    public BottomSheetNotifySuccessFragment(String message) {
        this.message = message;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  BottomSheetDialogCancelOrderSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtMessage.setText(message);
    }
}