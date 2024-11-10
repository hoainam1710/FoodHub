package com.example.foodapp.utils;

import com.example.foodapp.model.Review;

import java.io.Serializable;

public class ImageReviewDetail implements Serializable {
    private Review review;
    private String imgPath;
    private int position;
    private int numberImage;

    public ImageReviewDetail() {
    }

    public ImageReviewDetail(Review review, String imgPath, int position, int numberImage) {
        this.review = review;
        this.imgPath = imgPath;
        this.position = position;
        this.numberImage = numberImage;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNumberImage() {
        return numberImage;
    }

    public void setNumberImage(int numberImage) {
        this.numberImage = numberImage;
    }
}
