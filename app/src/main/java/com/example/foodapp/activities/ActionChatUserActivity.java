package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.ListMessageAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityActionChatUserBinding;
import com.example.foodapp.model.Chat;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActionChatUserActivity extends BaseActivity {
    private ActivityActionChatUserBinding binding;
    private static final String TAG = "ActionChatUserActivity";
    private String adminId;      // idReceiver == idAdmin
    private List<Chat> chats;
    private ListMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActionChatUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIdAdmin();
        initRcvListMessage();
        initListener();
        edtListener();

    }

    private void initRcvListMessage() {
        chats = new ArrayList<>();
        binding.rcvListMessage.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListMessageAdapter(chats);
        binding.rcvListMessage.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        database.getReference("Chats").child(getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            chats.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Chat chat = item.getValue(Chat.class);
                                if(chat == null){
                                    continue;
                                }
                                chats.add(chat);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListChatsCancelled: "+ error.getMessage());
                    }
                });
    }

    private void getIdAdmin() {
        DatabaseReference ref = database.getReference("Admin");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()  == 1){
                    DataSnapshot item = snapshot.getChildren().iterator().next();
                    User user =  item.getValue(User.class);
                    if(user != null){
                        adminId = user.getId();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getIdAdminCancelled: "+ error.getMessage());
            }
        });
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.imgBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(ActionChatUserActivity.this, UserMainActivity.class));
            finishAffinity();
        });

        binding.imgSend.setOnClickListener(v -> onClickSend());
    }

    // send message
    private void onClickSend() {
        long idChat = System.currentTimeMillis();
        String message = Objects.requireNonNull(binding.edtMessage.getText()).toString().trim();
        Chat chat = new Chat(idChat, getUid(), adminId, message, getTimeStamp());

        database.getReference("Chats").child(getUid())
                .child(String.valueOf(idChat))
                .setValue(chat);

        clearText();
    }

    private String getTimeStamp(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
        return format.format(new Date());
    }

    private void edtListener(){
        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = s.toString().trim();
                if(!message.isEmpty()){
                    binding.imgSend.setVisibility(View.VISIBLE);
                } else binding.imgSend.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void clearText(){
        binding.edtMessage.setText("");
    }


}