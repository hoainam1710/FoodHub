package com.example.foodapp.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class Review implements Serializable {
    private String id;
    private User user;
    private Food food;
    private Order order;
    private int rating;
    private String content;
    private String date;
    private List<String> listImagePath;

    public Review() {
    }

    public Review(String id, User user, Food food, Order order, int rating, String content, String date, List<String> listImagePath) {
        this.id = id;
        this.user = user;
        this.food = food;
        this.order = order;
        this.rating = rating;
        this.content = content;
        this.date = date;
        this.listImagePath = listImagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getListImagePath() {
        return listImagePath;
    }

    public void setListImagePath(List<String> listImagePath) {
        this.listImagePath = listImagePath;
    }
}
