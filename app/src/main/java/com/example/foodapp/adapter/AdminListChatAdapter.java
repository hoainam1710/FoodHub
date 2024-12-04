package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListChatAdminBinding;
import com.example.foodapp.model.Chat;
import com.example.foodapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminListChatAdapter extends RecyclerView.Adapter<AdminListChatAdapter.MyViewHolder> {
    public interface IAdminListChat{
        void onClick(User user);
    }
    private final Context context;
    private final List<User> users;
    private final IAdminListChat iAdminListChat;
    private static final String TAG = "AdminListChatAdapter";

    public AdminListChatAdapter(Context context, List<User> users, IAdminListChat iAdminListChat) {
        this.context = context;
        this.users = users;
        this.iAdminListChat = iAdminListChat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListChatAdminBinding binding = ItemRcvListChatAdminBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = users.get(position);
        if(user == null){
            return;
        }
        getLastMessage(user, holder.binding);
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(holder.binding.imgAvatar);

        if(user.getName() == null || user.getName().isEmpty()){
            String id = user.getId();
            String subId = id.substring(id.length()-6);
            holder.binding.txtUserName.setText("User_"+ subId);
        } else holder.binding.txtUserName.setText(user.getName());

        holder.binding.getRoot().setOnClickListener(v-> iAdminListChat.onClick(user));

    }

    private void getLastMessage(User user, ItemRcvListChatAdminBinding binding){
        FirebaseDatabase.getInstance().getReference("Chats")
                .child(user.getId())
                .orderByKey()
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot item = snapshot.getChildren().iterator().next();
                        Chat chat = item.getValue(Chat.class);
                        String idAdmin = FirebaseAuth.getInstance().getUid();
                        if(chat != null){
                            if(chat.getSenderId().equals(idAdmin)){
                                binding.txtLastMessage.setText("Bạn: "+ chat.getMessage());
                            } else {
                                String userName = user.getName();
                                if(user.getName() == null || user.getName().isEmpty()){
                                    String id = user.getId();
                                    String subId = id.substring(id.length()-6);
                                    userName = "User_"+ subId;
                                }
                                binding.txtLastMessage.setText(userName +": "+ chat.getMessage());
                            }
                            binding.txtTimeStamp.setText(chat.getTimeStamp());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getLastMessageCancelled: "+ error.getMessage());
                    }
                });

    }

    @Override
    public int getItemCount() {
        if(users != null){
            return users.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListChatAdminBinding binding;

        public MyViewHolder(@NonNull ItemRcvListChatAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
