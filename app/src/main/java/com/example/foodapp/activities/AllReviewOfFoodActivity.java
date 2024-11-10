package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.adapter.ListCompletedReviewAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAllReviewOfFoodBinding;
import com.example.foodapp.model.Food;
import com.example.foodapp.utils.ImageReviewDetail;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllReviewOfFoodActivity extends BaseActivity implements ListCompletedReviewAdapter.IListCompletedReview {
    private ActivityAllReviewOfFoodBinding binding;
    private Food foodSelected;
    private static final String TAG = "AllReviewOfFoodActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllReviewOfFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getFoodByIntent();
        initListener();
        initRcvListReview();
    }

    private void getFoodByIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        Food food = (Food) bundle.getSerializable("food");
        if(food == null){
            return;
        }
        foodSelected = food;

    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void initRcvListReview() {
        List<Review> listReview = new ArrayList<>();
        binding.rcvListReview.setLayoutManager(new LinearLayoutManager(this));
        ListCompletedReviewAdapter adapter = new ListCompletedReviewAdapter(this, listReview,this);
        binding.rcvListReview.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListReview.addItemDecoration(itemDecoration);
        loadData(listReview, adapter);
    }

    private void loadData(List<Review> listReview, ListCompletedReviewAdapter adapter) {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            listReview.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getFood().getId() == foodSelected.getId()){
                                    listReview.add(review);
                                }
                            }
                            binding.txtTitle.setText("Đánh giá " + "("+ listReview.size() + ")");
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
                    }
                });
    }

    @Override
    public void onClickImage(String imgPath, int position, int numberImage) {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getListImagePath().contains(imgPath)){
                                    Intent intent = new Intent(AllReviewOfFoodActivity.this, ImageReviewDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    ImageReviewDetail imageReviewDetail = new ImageReviewDetail(review, imgPath, position, numberImage);
                                    bundle.putSerializable("imageReviewDetail", imageReviewDetail);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onClickImageReviewCancelled: "+ error.getMessage());
                    }
                });
    }
}