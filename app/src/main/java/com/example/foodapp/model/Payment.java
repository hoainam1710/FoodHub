package com.example.foodapp.model;

import java.io.Serializable;

public class Payment implements Serializable {
    private int id;
    private String cardNumber;
    private String name;
    private String date;
    private String cvv;
    private boolean use;

    public Payment() {
    }

    public Payment(int id, String cardNumber, String name, String date, String cvv, boolean use) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.name = name;
        this.date = date;
        this.cvv = cvv;
        this.use = use;
    }

    public Payment(int id, String cardNumber, String name, String date, String cvv) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.name = name;
        this.date = date;
        this.cvv = cvv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
