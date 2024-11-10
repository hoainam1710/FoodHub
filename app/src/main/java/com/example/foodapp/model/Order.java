package com.example.foodapp.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private String idUser;
    private ShippingAddress shippingAddress;
    private Payment payment;
    private List<Cart> lists;   // list food order
    private Voucher voucher;
    private float total;   // tổng thanh toán
    private String date;
    private String status;  // trạng thái đơn hàng: ["active", "completed", "canceled"]
    private float sumPrice; // tổng phụ
    private float delivery;    // phí vận chuyển
    private float discount;    // giảm giá
    private boolean checkedReview;  // kiểm tra đơn hàng đã được đánh giá hay chưa

    public Order() {
    }

    public Order(String id, String idUser, ShippingAddress shippingAddress, Payment payment,
                 List<Cart> lists, Voucher voucher, float total, String date, float sumPrice,
                 float delivery, float discount, String status, boolean checkedReview) {
        this.id = id;
        this.idUser = idUser;
        this.shippingAddress = shippingAddress;
        this.payment = payment;
        this.lists = lists;
        this.voucher = voucher;
        this.total = total;
        this.date = date;
        this.sumPrice = sumPrice;
        this.delivery = delivery;
        this.discount = discount;
        this.status = status;
        this.checkedReview = checkedReview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<Cart> getLists() {
        return lists;
    }

    public void setLists(List<Cart> lists) {
        this.lists = lists;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(float sumPrice) {
        this.sumPrice = sumPrice;
    }

    public float getDelivery() {
        return delivery;
    }

    public void setDelivery(float delivery) {
        this.delivery = delivery;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public boolean isCheckedReview() {
        return checkedReview;
    }

    public void setCheckedReview(boolean checkedReview) {
        this.checkedReview = checkedReview;
    }
}
