package com.example.foodapp.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Food implements Serializable {
    @PropertyName("Id")
    private int id;
    @PropertyName("Title")
    private String title;
    @PropertyName("CategoryId")
    private int categoryId;
    @PropertyName("Description")
    private String description;
    @PropertyName("ImagePath")
    private String imagePath;
    @PropertyName("Price")
    private float price;
    @PropertyName("PriceId")
    private int priceId;
    @PropertyName("Star")
    private float star;
    @PropertyName("TimeId")
    private int timeId;
    @PropertyName("TimeValue")
    private int timeValue;
    @PropertyName("BestFood")
    private boolean isBestFood;

    public Food() {
    }

    public Food(int id, String title, int categoryId, String description, String imagePath, float price, int priceId, float star, int timeId, int timeValue, boolean isBestFood) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
        this.description = description;
        this.imagePath = imagePath;
        this.price = price;
        this.priceId = priceId;
        this.star = star;
        this.timeId = timeId;
        this.timeValue = timeValue;
        this.isBestFood = isBestFood;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getPriceId() {
        return priceId;
    }

    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(int timeValue) {
        this.timeValue = timeValue;
    }

    public boolean isBestFood() {
        return isBestFood;
    }

    public void setBestFood(boolean bestFood) {
        isBestFood = bestFood;
    }
}
