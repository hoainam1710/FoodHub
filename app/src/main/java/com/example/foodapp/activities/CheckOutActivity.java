package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityCheckOutBinding;
import com.example.foodapp.fragments.ItemOrderDialogFragment;
import com.example.foodapp.fragments.ProgressDialogFragment;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.Payment;
import com.example.foodapp.model.ShippingAddress;
import com.example.foodapp.model.Voucher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends BaseActivity implements ItemOrderDialogFragment.IItemOrderDialogFragment {
    private ActivityCheckOutBinding binding;
    private ItemOrderDialogFragment itemOrderDialogFragment;
    private float discount = 0;
    private float sumPrice = 0;
    private float total = 0;
    private int itemOrderCounts = 0;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ShippingAddress shippingAddressOrder;
    private Payment paymentOrder;
    private Voucher voucherOder;
    private List<Cart> listCartOrder;
    private String dateOder;
    private ProgressDialogFragment progressDialogFragment;
    private Dialog dialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialogFragment = new ProgressDialogFragment();
        binding.txtVoucherCount.setText("0 Phiếu giảm giá");
        binding.txtDiscount.setVisibility(View.GONE);
        binding.txtPhiVanChuyen.setText("$"+ 5.00);

        getResultVoucherIntent();
        setColorRadioButton();
        getAddressFromRealtime();
        getPaymentFromRealtime();
        getItemOderCount();
        getTotalOrder();
        getDateOrder();
        getListCartOrder();
        initListener();

    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());

        binding.layoutShippingAddress.setOnClickListener(v ->
                startActivity(new Intent(CheckOutActivity.this, ShippingAddressActivity.class)));

        binding.imgEditCardNumber.setOnClickListener(v ->
                startActivity(new Intent(CheckOutActivity.this, PaymentActivity.class)));

        binding.rdoPaymentCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                binding.layoutCardNumber.setVisibility(View.VISIBLE);
            } else binding.layoutCardNumber.setVisibility(View.GONE);
        });

        binding.layoutCountItem.setOnClickListener(v -> showBottomSheetDialogFragment());

        binding.layoutVoucher.setOnClickListener(v ->
                activityResultLauncher.launch(new Intent(CheckOutActivity.this, VoucherActivity.class)));

        binding.btnOrder.setOnClickListener(v -> {
            saveOrderToRealtime();
        });
    }


    private void getTotalOrder(){
        binding.progressBarTotal.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBarTotal.setVisibility(View.GONE);
                        sumPrice = 0;
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    sumPrice += cart.getCartTotal();
                                }
                            }
                            DecimalFormat df = new DecimalFormat("#.##");
                            sumPrice = Float.parseFloat(df.format(sumPrice));
                            binding.txtSumPrice.setText("$"+ sumPrice);

                            total = sumPrice + 5 - discount;
                            binding.txtTotal1.setText("$"+ total);
                            binding.txtTotal2.setText("$"+ total);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarTotal.setVisibility(View.GONE);
                        Toast.makeText(CheckOutActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    private void getResultVoucherIntent() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , o -> {
                    if (o.getResultCode() == RESULT_OK) {
                        Intent intent = o.getData();
                        if (intent == null) {
                            return;
                        }

                        Voucher voucher = (Voucher) intent.getSerializableExtra("voucher");
                        discount = intent.getFloatExtra("discount", 0);
                        if(voucher == null){
                            discount = 0;
                            binding.txtDiscount.setVisibility(View.GONE);
                            binding.txtVoucherCount.setText("0 Phiếu giảm giá");
                            binding.txtSumDiscount.setText("-$"+ discount);

                        } else {
                            voucherOder = voucher;
                            binding.txtDiscount.setVisibility(View.VISIBLE);
                            binding.txtDiscount.setText("-$" + discount);
                            binding.txtVoucherCount.setText("1 Phiếu giảm giá:");
                            binding.txtSumDiscount.setText("-$"+ discount);
                        }

                        getTotalOrder();
                    }

                });
    }


    private void saveOrderToRealtime() {
        progressDialogFragment.show(getSupportFragmentManager(), "progressDialogFragment");
        if(mAuth.getCurrentUser() ==  null){
            return;
        }
        String idOrder = "ORD"+ System.currentTimeMillis();
        String idUser = mAuth.getCurrentUser().getUid();
        String status = "active";
        float delivery = 5;    // phí vận chuyển
        if(binding.rdoPaymentNhanHang.isChecked()){
            paymentOrder = null;
        }
        Order order = new Order(idOrder, idUser, shippingAddressOrder, paymentOrder, listCartOrder,
                voucherOder, total, dateOder, sumPrice, delivery, discount ,status, false);
        database.getReference("Orders").child(idOrder).setValue(order)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if (!CheckOutActivity.this.isFinishing()) {
                            database.getReference("Account")
                                    .child(getUid())
                                    .child("order_history")
                                    .child(idOrder)
                                    .child("order_id").setValue(idOrder)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialogFragment.dismiss();
                                        if (task1.isSuccessful()) {
                                            showSuccessOrderDialog();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showSuccessOrderDialog(){
        dialog = new Dialog(this);
        dialog.setCancelable(false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notify_success_order);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnOrderDetail = dialog.findViewById(R.id.btn_order_detail);
        Button btnGoHome = dialog.findViewById(R.id.btn_go_home);

        btnGoHome.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            new Handler().postDelayed(() -> {
                startActivity(new Intent(CheckOutActivity.this, UserMainActivity.class));
                finishAffinity();
            }, 100);

        });

        btnOrderDetail.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            new Handler().postDelayed(this::nextToMyOrderActivity, 100);
        });

        if (!CheckOutActivity.this.isFinishing()) {
            dialog.show();
        }
    }

    private void nextToMyOrderActivity(){
        Intent intent = new Intent(CheckOutActivity.this, MyOrderActivity.class);
        intent.putExtra("from", "CheckOutActivity");
        startActivity(intent);
    }

    private void getListCartOrder() {
        listCartOrder = new ArrayList<>();
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listCartOrder.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    listCartOrder.add(cart);
                                }
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckOutActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDateOrder(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateOder = dateFormat.format(System.currentTimeMillis());
    }

    private void showBottomSheetDialogFragment() {
        itemOrderDialogFragment = new ItemOrderDialogFragment();
        itemOrderDialogFragment.show(getSupportFragmentManager(), "itemOrderDialogFragment");
    }

    private void getAddressFromRealtime() {
        database.getReference("Account").child(getUid()).child("shippingAddress")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                ShippingAddress shippingAddress = item.getValue(ShippingAddress.class);
                                if(shippingAddress == null){
                                    continue;
                                }
                                if(shippingAddress.isUse()){
                                    shippingAddressOrder = shippingAddress;
                                    binding.txtName.setText(shippingAddress.getName()+" |");
                                    String phoneNumber = shippingAddress.getPhoneNumber();
                                    String cutPhoneNumber = phoneNumber.substring(1);
                                    binding.txtPhoneNumber.setText("(+84)"+ cutPhoneNumber);
                                    String fullAddress = shippingAddress.getSoNha() + " Phường "+ shippingAddress.getPhuong()
                                            + ", Quận "+ shippingAddress.getQuan() + ", "+ shippingAddress.getThanhPho();
                                    binding.txtShippingAddress.setText(fullAddress);

                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckOutActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getPaymentFromRealtime() {
        database.getReference("Account").child(getUid()).child("payment")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Payment payment = item.getValue(Payment.class);
                                if(payment == null){
                                    continue;
                                }
                                if(payment.isUse()){
                                    paymentOrder = payment;
                                    String lastCardNumber = payment.getCardNumber().substring(12);
                                    binding.txtCardNumber.setText("**** **** **** "+ lastCardNumber);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckOutActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getItemOderCount(){
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemOrderCounts = 0;
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    continue;
                                }
                                if(cart.isChecked()){
                                    itemOrderCounts++;
                                }
                            }
                            binding.txtCountItem.setText(itemOrderCounts + " mặt hàng");
                            binding.txtLabelSumPrice.setText("Tổng phụ "+ "("+ itemOrderCounts+" mục"+ "):");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckOutActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setColorRadioButton() {
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.rdo_bg_color);
        binding.rdoPaymentNhanHang.setButtonTintList(colorStateList);
        binding.rdoPaymentCard.setButtonTintList(colorStateList);

    }

    @Override
    public void onClick() {
        itemOrderDialogFragment.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}