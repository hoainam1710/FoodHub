package com.example.foodapp.model;

import java.io.Serializable;

public class ShippingAddress implements Serializable {
    private int id;
    private String name;
    private String phoneNumber;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String soNha;
    private boolean use;

    public ShippingAddress() {
    }

    public ShippingAddress(int id, String name, String phoneNumber, String thanhPho, String quan, String phuong, String soNha, boolean use) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.thanhPho = thanhPho;
        this.quan = quan;
        this.phuong = phuong;
        this.soNha = soNha;
        this.use = use;
    }

    public ShippingAddress(int id, String name, String phoneNumber, String thanhPho, String quan, String phuong, String soNha) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.thanhPho = thanhPho;
        this.quan = quan;
        this.phuong = phuong;
        this.soNha = soNha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public String getPhuong() {
        return phuong;
    }

    public void setPhuong(String phuong) {
        this.phuong = phuong;
    }

    public String getSoNha() {
        return soNha;
    }

    public void setSoNha(String soNha) {
        this.soNha = soNha;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
