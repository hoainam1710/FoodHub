package com.example.foodapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemMessageReceiverBinding;
import com.example.foodapp.databinding.ItemMessageSenderBinding;
import com.example.foodapp.model.Chat;
import com.example.foodapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SENDER_TYPE = 0;
    public static final int RECEIVER_TYPE = 1;
    private static final String TAG = "ListMessageAdapter";
    private final List<Chat> chats;
    public static final String ADMIN_ID = "6ltGrjTP3VTPgFL58SgVcQI2Iw13";


    public ListMessageAdapter(List<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == SENDER_TYPE){
            ItemMessageSenderBinding itemMessageSenderBinding =
                    ItemMessageSenderBinding.inflate(inflater, parent, false);
            return new SenderViewHolder(itemMessageSenderBinding);
        } else {
            ItemMessageReceiverBinding itemMessageReceiverBinding =
                    ItemMessageReceiverBinding.inflate(inflater, parent, false);
            return new ReceiverViewHolder(itemMessageReceiverBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if(chat == null){
            return;
        }
        if(holder.getItemViewType() == SENDER_TYPE){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.binding.txtMessageSender.setText(chat.getMessage());
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.binding.txtMessageReceiver.setText(chat.getMessage());

            getReceiver(chat.getSenderId(), receiverViewHolder);
        }
    }
    private void getReceiver(String idReceiver, ReceiverViewHolder receiverViewHolder){
        FirebaseDatabase.getInstance().getReference("Account")
                .child(idReceiver)
                .child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if(user != null){
                                Glide.with(receiverViewHolder.itemView.getContext())
                                        .load(user.getAvatar())
                                        .error(R.drawable.avatar_default)
                                        .placeholder(R.drawable.avatar_default)
                                        .into(receiverViewHolder.binding.imgAvatarReceiver);
                            }
                        } else {
                            Glide.with(receiverViewHolder.itemView.getContext())
                                    .load(R.drawable.logo)
                                    .error(R.drawable.avatar_default)
                                    .placeholder(R.drawable.avatar_default)
                                    .into(receiverViewHolder.binding.imgAvatarReceiver);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getReceiverCancelled: "+ error.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {
        if(chats != null){
            return chats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chats.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(id.equals(ADMIN_ID)){
                if(chat.getReceiverId().equals(ADMIN_ID))
                    return RECEIVER_TYPE;
                return SENDER_TYPE;
            } else {
                if(chat.getReceiverId().equals(ADMIN_ID))
                    return SENDER_TYPE;
                return RECEIVER_TYPE;
            }

        }
        return -1;

    }


    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageSenderBinding binding;
        public SenderViewHolder(@NonNull ItemMessageSenderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageReceiverBinding binding;
        public ReceiverViewHolder(@NonNull ItemMessageReceiverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
