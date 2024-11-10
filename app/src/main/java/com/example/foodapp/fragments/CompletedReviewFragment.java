package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodapp.activities.ImageReviewDetailActivity;
import com.example.foodapp.adapter.ListCompletedReviewAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentCompletedReviewBinding;
import com.example.foodapp.utils.ImageReviewDetail;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompletedReviewFragment extends BaseFragment implements ListCompletedReviewAdapter.IListCompletedReview {
    private FragmentCompletedReviewBinding binding;
    private static final String TAG = "CompletedReviewFragment";


    public static CompletedReviewFragment getInstance() {
        return new CompletedReviewFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentCompletedReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRcvListCompletedReview();

    }

    private void initRcvListCompletedReview() {
        List<Review> list = new ArrayList<>();
        ListCompletedReviewAdapter adapter = new ListCompletedReviewAdapter(requireActivity(), list, this);
        binding.rcvListCompletedReview.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListCompletedReview.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        binding.rcvListCompletedReview.addItemDecoration(itemDecoration);
        loadData(list, adapter);
    }

    private void loadData(List<Review> list, ListCompletedReviewAdapter adapter) {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        list.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getUser().getId().equals(getUid())){
                                    list.add(review);
                                }
                            }
                            if(list.isEmpty()){
                                binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                            } else binding.layoutNotifyEmpty.setVisibility(View.GONE);
                        } else  binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
//                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Intent intent = new Intent(requireActivity(), ImageReviewDetailActivity.class);
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