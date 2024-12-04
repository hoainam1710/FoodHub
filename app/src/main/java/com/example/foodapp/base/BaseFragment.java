package com.example.foodapp.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseFragment extends Fragment {
    protected FirebaseAuth mAuth;
    protected FirebaseDatabase database;
    protected FirebaseStorage storage;
    protected EventBus mEventBus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mEventBus = EventBus.getDefault();

    }

    // lấy id account
    protected String getUid(){
        FirebaseUser user  = mAuth.getCurrentUser();
        if(user != null){
            return user.getUid();
        }
        return null;
    }
}