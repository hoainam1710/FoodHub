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
import com.example.foodapp.databinding.ActivityListFoodPopularBinding;
import com.example.foodapp.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListFoodPopularActivity extends BaseActivity implements ListFoodAdapter.IListFood {
    private ActivityListFoodPopularBinding binding;
    private List<Food> lists;
    private ListFoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListFoodPopularBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtTitle.setText("Phổ biến");
        initRcv();
        initListener();

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
                                if(food != null && food.isBestFood()){
                                    lists.add(food);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(ListFoodPopularActivity.this, "Lỗi: "+  error.getMessage(), Toast.LENGTH_SHORT).show();
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