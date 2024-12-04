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

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.adapter.ListMessageAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityActionChatAdminBinding;
import com.example.foodapp.model.Chat;
import com.example.foodapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActionChatAdminActivity extends BaseActivity {
    private ActivityActionChatAdminBinding binding;
    private User userContact;
    private List<Chat> chats;
    private ListMessageAdapter adapter;
    private static final String TAG = "ActionChatAdminActivity";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActionChatAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserContactIntent();
        initRcvListMessage();
        edtListener();
        initListener();
    }

    private void initRcvListMessage() {
        chats = new ArrayList<>();
        binding.rcvListMessage.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListMessageAdapter(chats);
        binding.rcvListMessage.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        database.getReference("Chats").child(userContact.getId())
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


    @SuppressLint("SetTextI18n")
    private void getUserContactIntent() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        userContact = (User) bundle.getSerializable("user");
        Glide.with(this)
                .load(userContact.getAvatar())
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .into(binding.imgAvatar);

        if(userContact.getName() == null || userContact.getName().isEmpty()){
            String id = userContact.getId();
            String subId = id.substring(id.length()-6);
            binding.txtUserName.setText("User_"+ subId);
        } else binding.txtUserName.setText(userContact.getName());

    }


    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.imgBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(ActionChatAdminActivity.this, AdminMainActivity.class));
            finishAffinity();
        });
        // send message
        binding.imgSend.setOnClickListener(v -> onClickSendMessage());
    }

    private void onClickSendMessage(){
        long idChat = System.currentTimeMillis();
        String message = Objects.requireNonNull(binding.edtMessage.getText()).toString().trim();
        String idReceiver = userContact.getId();
        Chat chat = new Chat(idChat, getUid(), idReceiver, message, getTimeStamp());

        database.getReference("Chats").child(idReceiver)
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