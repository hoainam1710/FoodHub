package com.example.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.foodapp.R;
import com.example.foodapp.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.foodapp.databinding.ActivityIntroBinding binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        binding.btnGetStarted.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(IntroActivity.this, UserMainActivity.class));
//                startActivity(new Intent(IntroActivity.this, AdminMainActivity.class));
                finish();
            }
        });
    }
}