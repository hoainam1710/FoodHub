package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.foodapp.R;
import com.example.foodapp.adapter.ListFoodOrderAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityOrderDetailBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.Payment;
import com.example.foodapp.model.ShippingAddress;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailActivity extends BaseActivity {
    private ActivityOrderDetailBinding binding;
    private Order orderSelected;
    public static final int VIEW_REVIEW_TYPE = 0;
    public static final int ACTION_REVIEW_TYPE = 1;
    private int currentType = ACTION_REVIEW_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOrderByIntent();
        getStatusOrder();
        getAddressOrder();
        getListFoodOrder();
        getPaymentOrder();
        getIdOrder();
        getTotalOrder();
        checkedReviewOrder();
        initListener();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnReview.setOnClickListener(v -> {
            if(currentType == ACTION_REVIEW_TYPE){
                nextToReviewActivity();
            } else nextToReviewDetailActivity();
        });
    }

    private void checkedReviewOrder(){
        if(orderSelected.isCheckedReview()){
            binding.btnReview.setText("Xem đánh giá");
            currentType = VIEW_REVIEW_TYPE;
        } else {
            binding.btnReview.setText("Đánh giá");
            currentType = ACTION_REVIEW_TYPE;
        }

    }

    private void nextToReviewActivity(){
        Intent intent = new Intent(OrderDetailActivity.this, ReviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", orderSelected);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void nextToReviewDetailActivity(){
        Intent intent = new Intent(OrderDetailActivity.this, ReviewDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", orderSelected);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getStatusOrder() {
        String status = "";
        int drawableStart = 0;

        if(orderSelected.getStatus().equals("active")){
            status = "Đơn hàng đang trong quá trình xử lý!";
            drawableStart = R.drawable.ic_order_active;
            binding.layoutBottom.setVisibility(View.GONE);
            binding.layoutMain.setPadding(0, 0, 0, 0);
        } else if(orderSelected.getStatus().equals("completed")){
            status = "Đơn hàng đã hoàn thành!";
            drawableStart = R.drawable.ic_order_success;
            binding.layoutBottom.setVisibility(View.VISIBLE);
        } else {
            status = "Đơn hàng đã bị hủy!";
            drawableStart = R.drawable.ic_order_cancel;
            binding.layoutBottom.setVisibility(View.GONE);
            binding.layoutMain.setPadding(0, 0, 0, 0);
        }
        binding.txtStatusOrder.setText(status);
        binding.txtStatusOrder.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0);
    }

    @SuppressLint("SetTextI18n")
    private void getTotalOrder() {
        DecimalFormat format = new DecimalFormat("#.##");
        binding.txtSumPrice.setText("$"+ format.format(orderSelected.getSumPrice()));
        binding.txtDelivery.setText("$"+ format.format(orderSelected.getSumPrice()));
        binding.txtSumDiscount.setText("-$"+ format.format(orderSelected.getDiscount()));
        binding.txtTotal.setText("$"+ format.format(orderSelected.getTotal()));
    }


    private void getIdOrder() {
        binding.txtIdOrder.setText(orderSelected.getId());
        binding.txtDateOrder.setText(orderSelected.getDate());
    }

    @SuppressLint("SetTextI18n")
    private void getPaymentOrder() {
        Payment payment = orderSelected.getPayment();
        if(payment == null){
            binding.txtPaymentNhanHang.setVisibility(View.VISIBLE);
            binding.txtPaymentCard.setVisibility(View.GONE);
            binding.layoutCardNumber.setVisibility(View.GONE);
        } else {
            binding.txtPaymentNhanHang.setVisibility(View.GONE);
            binding.txtPaymentCard.setVisibility(View.VISIBLE);
            binding.layoutCardNumber.setVisibility(View.VISIBLE);
            String lastCardNumber = payment.getCardNumber().substring(12);
            binding.txtCardNumber.setText("**** **** **** "+ lastCardNumber);
        }
    }

    private void getListFoodOrder() {
        List<Cart> listCartOrder = orderSelected.getLists();
        binding.rcvListFoodOrder.setLayoutManager(new LinearLayoutManager(this));
        ListFoodOrderAdapter listFoodOrderAdapter = new ListFoodOrderAdapter(this, listCartOrder);
        binding.rcvListFoodOrder.setAdapter(listFoodOrderAdapter);
    }


    @SuppressLint("SetTextI18n")
    private void getAddressOrder() {
        ShippingAddress shippingAddress = orderSelected.getShippingAddress();
        binding.txtName.setText(shippingAddress.getName()+" |");
        String phoneNumber = shippingAddress.getPhoneNumber();
        String cutPhoneNumber = phoneNumber.substring(1);
        binding.txtPhoneNumber.setText("(+84)"+ cutPhoneNumber);
        String fullAddress = shippingAddress.getSoNha() + " Phường "+ shippingAddress.getPhuong()
                + ", Quận "+ shippingAddress.getQuan() + ", "+ shippingAddress.getThanhPho();
        binding.txtShippingAddress.setText(fullAddress);
    }

    private void getOrderByIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        orderSelected = (Order) bundle.getSerializable("order");
    }
}