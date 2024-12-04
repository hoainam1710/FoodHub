package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.activities.ActionChatAdminActivity;
import com.example.foodapp.adapter.AdminListChatAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentChatAdminBinding;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAdminFragment extends BaseFragment implements AdminListChatAdapter.IAdminListChat {
    private FragmentChatAdminBinding binding;
    private List<User> users;
    private List<User> usersClone;
    private AdminListChatAdapter adapter;
    private static final String TAG = "ChatAdminFragment";

    public static ChatAdminFragment getInstance() {
        return new ChatAdminFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersClone = new ArrayList<>();
        initRcv();
        initListener();
    }

    private void initListener() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getListUserSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getListUserSearch(v.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    // search by name
    @SuppressLint("NotifyDataSetChanged")
    private void getListUserSearch(String key) {
        List<User> userSearched = new ArrayList<>();
        if(key.isEmpty()){
            userSearched = usersClone;
        } else {
            userSearched = users.stream()
                    .filter(user -> {
                        String userName;
                        if(user.getName() == null || user.getName().isEmpty()){
                            String id = user.getId();
                            String subId = id.substring(id.length()-6);
                            userName = "User_"+ subId;
                        } else userName = user.getName();
                        return userName.toLowerCase().contains(key.trim().toLowerCase());
                    })
                    .collect(Collectors.toList());
        }

        users.clear();
        users.addAll(userSearched);
        adapter.notifyDataSetChanged();

        if(users.isEmpty()){
            binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
        } else binding.layoutNotifyEmpty.setVisibility(View.GONE);


    }

    private void initRcv() {
        users = new ArrayList<>();
        binding.rcvListChat.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new AdminListChatAdapter(requireActivity(), users, this);
        binding.rcvListChat.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        binding.rcvListChat.addItemDecoration(itemDecoration);
        loadData();
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            usersClone.clear();
                            users.clear();
                            binding.layoutNotifyEmpty.setVisibility(View.GONE);
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String idContact = item.getRef().getKey();
                                if (idContact == null) {
                                    return;
                                }
                                Log.d(TAG, "idContact: " + idContact);
                                database.getReference("Account")
                                        .child(idContact)
                                        .child("profile")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                binding.progressBar.setVisibility(View.GONE);
                                                if (snapshot.exists()) {
                                                    User user = snapshot.getValue(User.class);
                                                    if (user != null) {
                                                        users.add(user);
                                                        usersClone.add(user);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.d(TAG, "getUserCanceled: " + error.getMessage());
                                            }
                                        });
                            }
                        } else binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getListChatCancelled: " + error.getMessage());
                    }
                });
    }

    @Override
    public void onClick(User user) {
        Intent intent = new Intent(requireActivity(), ActionChatAdminActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}