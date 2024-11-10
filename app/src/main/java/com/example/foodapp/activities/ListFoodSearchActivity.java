package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodapp.R;
import com.example.foodapp.adapter.ListFoodAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityListFoodSearchBinding;
import com.example.foodapp.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListFoodSearchActivity extends BaseActivity implements ListFoodAdapter.IListFood {
    private ActivityListFoodSearchBinding binding;
    private List<Food> lists;
    private ListFoodAdapter adapter;
    private String strSeacch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListFoodSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtTitle.setText("Món ăn tìm kiếm");
        getResultIntent();
        initRcv();
        initListener();

    }

    private void getResultIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        strSeacch =  intent.getStringExtra("strSearch");
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void initRcv() {
        lists = new ArrayList<>();
        binding.rcvListFood.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ListFoodAdapter(this, lists, this);
        binding.rcvListFood.setAdapter(adapter);
        loadRcv();

    }

    private void loadRcv() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Foods")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        lists.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Food food = item.getValue(Food.class);
                                if(food != null && food.getTitle().toLowerCase().contains(strSeacch.toLowerCase())){
                                    lists.add(food);
                                }
                            }
                            if(lists.isEmpty()){
                                binding.txtNotExists.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.txtNotExists.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(ListFoodSearchActivity.this, "Lỗi: "+  error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(Food food) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", food);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}