package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.AdminReviewManagerAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentReviewManagerBinding;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewManagerFragment extends BaseFragment {
    private FragmentReviewManagerBinding binding;
    private List<Review> reviews;
    private AdminReviewManagerAdapter adapter;
    private static final String TAG = "ReviewManagerFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentReviewManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRcvListReview();
    }

    private void initRcvListReview() {
        reviews = new ArrayList<>();
        adapter = new AdminReviewManagerAdapter(reviews);
        binding.rcvListReview.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListReview.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviews.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                reviews.add(review);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
                    }
                });
    }
}