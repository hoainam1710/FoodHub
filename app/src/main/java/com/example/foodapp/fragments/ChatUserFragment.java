package com.example.foodapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodapp.activities.ActionChatUserActivity;
import com.example.foodapp.databinding.FragmentChatUserBinding;

public class ChatUserFragment extends Fragment {
    private FragmentChatUserBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentChatUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnChat.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ActionChatUserActivity.class)));
    }
}