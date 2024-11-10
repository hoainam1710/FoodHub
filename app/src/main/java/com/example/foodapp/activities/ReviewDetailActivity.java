package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.adapter.ListCompletedReviewAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityReviewDetailBinding;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewDetailActivity extends BaseActivity implements ListCompletedReviewAdapter.IListCompletedReview {
    private ActivityReviewDetailBinding binding;
    private Order orderSelected;
    private static final String TAG = "ReviewDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOrderByIntent();
        initListener();
        initRcvListCompletedReview();

    }

    private void getOrderByIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        Order order = (Order) bundle.getSerializable("order");
        if(order == null){
            return;
        }
        orderSelected = order;
        Log.d(TAG, "getOrderByIntent: "+ order.getId());
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void initRcvListCompletedReview() {
        List<Review> list = new ArrayList<>();
        ListCompletedReviewAdapter adapter = new ListCompletedReviewAdapter(this, list, this);
        binding.rcvListCompletedReview.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvListCompletedReview.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListCompletedReview.addItemDecoration(itemDecoration);
        loadData(list, adapter);
    }

    private void loadData(List<Review> list, ListCompletedReviewAdapter adapter) {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getOrder().getId().equals(orderSelected.getId())){
                                    list.add(review);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
                        Toast.makeText(ReviewDetailActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClickImage(String imgPath, int position, int numberImage) {

    }
}