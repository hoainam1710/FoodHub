package com.example.foodapp.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private int id;
    private Food food;
    private int quantity;
    private boolean checked;    // kiểm tra click chọn order
    private float cartTotal;
    private boolean checkedReview;  // kiểm tra cart in order đã được đánh giá chưa

    public Cart() {
    }

    public Cart(int id, Food food, int quantity) {
        this.id = id;
        this.food = food;
        this.quantity = quantity;
    }

    public Cart(int id, Food food, int quantity, boolean checked) {
        this.id = id;
        this.food = food;
        this.quantity = quantity;
        this.checked = checked;
    }

    public Cart(int id, Food food, int quantity, float cartTotal, boolean checkedReview) {
        this.id = id;
        this.food = food;
        this.quantity = quantity;
        this.cartTotal = cartTotal;
        this.checkedReview = checkedReview;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public float getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(float cartTotal) {
        this.cartTotal = cartTotal;
    }

    public boolean isCheckedReview() {
        return checkedReview;
    }

    public void setCheckedReview(boolean checkedReview) {
        this.checkedReview = checkedReview;
    }
}
