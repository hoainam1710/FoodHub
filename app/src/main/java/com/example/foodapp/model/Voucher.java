package com.example.foodapp.model;

import java.io.Serializable;

public class Voucher implements Serializable {
    private int id;
    private String title;
    private String description;
    private String code;
    private float discount;
    private float minOderAmount;    // giá trị đơn hàng tối thiểu
    private float maxDiscountAmount;       //số tiền giảm tối đa
    private String startDate;
    private String endDate;
    private String status; // trạng thái
    private int count;  // số lượng
    private int limitUse;   // giới hạn dùng cho mỗi tài khoản

    public Voucher() {
    }

    public Voucher(int id, String title, String description, String code, float discount, float minOderAmount, String startDate, String endDate, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.discount = discount;
        this.minOderAmount = minOderAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getMinOderAmount() {
        return minOderAmount;
    }

    public void setMinOderAmount(float minOderAmount) {
        this.minOderAmount = minOderAmount;
    }

    public float getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(float maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLimitUse() {
        return limitUse;
    }

    public void setLimitUse(int limitUse) {
        this.limitUse = limitUse;
    }
}
