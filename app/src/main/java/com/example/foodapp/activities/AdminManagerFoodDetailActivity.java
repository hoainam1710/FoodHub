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

import com.bumptech.glide.Glide;
import com.example.foodapp.adapter.ListCompletedReviewAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAdminManagerFoodDetailBinding;
import com.example.foodapp.model.Food;
import com.example.foodapp.model.Review;
import com.example.foodapp.utils.ImageReviewDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminManagerFoodDetailActivity extends BaseActivity implements ListCompletedReviewAdapter.IListCompletedReview {
    private ActivityAdminManagerFoodDetailBinding binding;
    private static final String TAG = "AdminManagerFoodDetailA";
    private Food foodSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminManagerFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getResultIntent();
        initRcvListReview();
        initListener();

    }

    @SuppressLint("SetTextI18n")
    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.txtViewAllReview.setOnClickListener(v -> onClickViewAllReview());
    }

    private void onClickViewAllReview(){
        Intent intent = new Intent(this, AllReviewOfFoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", foodSelected);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initRcvListReview() {
        List<Review> listReview = new ArrayList<>();
        binding.rcvListReview.setLayoutManager(new LinearLayoutManager(this));
        ListCompletedReviewAdapter adapter = new ListCompletedReviewAdapter(this, listReview, this);
        binding.rcvListReview.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListReview.addItemDecoration(itemDecoration);
        loadData(listReview, adapter);
    }

    private void loadData(List<Review> listReview, ListCompletedReviewAdapter adapter) {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                            if(listReview.isEmpty()){
                                binding.txtNumberReview.setText("(0 lượt đánh giá)");
                                binding.txtViewAllReview.setVisibility(View.GONE);
                            } else {
                                binding.txtNumberReview.setText("("+ listReview.size()+ " lượt đánh giá)");
                                binding.txtViewAllReview.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.txtNumberReview.setText("(0 lượt đánh giá)");
                            binding.txtViewAllReview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void getResultIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        foodSelected = (Food) bundle.getSerializable("food");
        if(foodSelected == null){
            return;
        }

        Glide.with(this).load(foodSelected.getImagePath()).into(binding.imgFood);
        binding.txtTitle.setText(foodSelected.getTitle());
        binding.txtPrice.setText(foodSelected.getPrice() + "$");
        binding.ratingStar.setRating(foodSelected.getStar());
        binding.txtRating.setText(foodSelected.getStar() + "/5");
        binding.txtTimeValue.setText(foodSelected.getTimeValue() + "min");
        binding.txtDescription.setText(foodSelected.getDescription());

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
                                    Intent intent = new Intent(AdminManagerFoodDetailActivity.this, ImageReviewDetailActivity.class);
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