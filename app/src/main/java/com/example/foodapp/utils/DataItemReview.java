package com.example.foodapp.utils;

public class DataItemReview {
    private String dataEdt;
    private int rating;

    public DataItemReview(String dataEdt, int rating) {
        this.dataEdt = dataEdt;
        this.rating = rating;
    }

    public String getDataEdt() {
        return dataEdt;
    }

    public void setDataEdt(String dataEdt) {
        this.dataEdt = dataEdt;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
