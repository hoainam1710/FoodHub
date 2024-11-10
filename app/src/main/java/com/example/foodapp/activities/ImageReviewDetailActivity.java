package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.adapter.ViewPagerImageReviewDetailAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityImageReviewDetailBinding;
import com.example.foodapp.utils.ImageReviewDetail;
import com.example.foodapp.model.Review;
public class ImageReviewDetailActivity extends BaseActivity {
    private ActivityImageReviewDetailBinding binding;
    private Review reviewSelected;
    private int positionCurrent = -1;
    private int numberImage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageReviewDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getImageReviewByIntent();
        initViewPager();
        initListener();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void initViewPager() {
        ViewPagerImageReviewDetailAdapter adapter = new ViewPagerImageReviewDetailAdapter(this,reviewSelected.getListImagePath());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(positionCurrent);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                binding.txtPosition.setText(position + 1 + "/"+ numberImage);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getImageReviewByIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        ImageReviewDetail imageReviewDetail = (ImageReviewDetail) bundle.getSerializable("imageReviewDetail");
        if(imageReviewDetail == null){
            return;
        }
        positionCurrent = imageReviewDetail.getPosition();
        numberImage = imageReviewDetail.getNumberImage();
        binding.txtPosition.setText(imageReviewDetail.getPosition() + 1 + "/"+ imageReviewDetail.getNumberImage());
        Review review = imageReviewDetail.getReview();
        reviewSelected = review;
        if(review == null){
            return;
        }
        binding.txtDate.setText(review.getDate());
        binding.txtDescription.setText(review.getContent());
        binding.ratingStar.setRating(review.getRating());
        Glide.with(this)
                .load(review.getUser().getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(binding.imgAvatar);
        Glide.with(this)
                .load(review.getFood().getImagePath())
                .into(binding.imgFood);
        binding.txtNameUser.setText(review.getUser().getName());
        binding.txtNameFood.setText(review.getFood().getTitle());


    }

}