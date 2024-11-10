package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton$InspectionCompanion;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAddPaymentBinding;
import com.example.foodapp.databinding.ActivityPaymentBinding;
import com.example.foodapp.model.Payment;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPaymentActivity extends BaseActivity {
    private ActivityAddPaymentBinding binding;
    private ProgressDialog progressDialog;
    private int checkCurrent = 0;
    public static final int CART_CHECK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getResultIntent();
        initProgressDialog();
        initListener();

    }
    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void getResultIntent(){
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        String resultFromCartActivity = intent.getStringExtra("cartActivityToAddPayment");
        if(resultFromCartActivity != null && resultFromCartActivity.equals("cart")){
            checkCurrent = CART_CHECK;
            binding.btnSave.setText("Tiếp tục");
        }
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> addPayment());
        binding.edtDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void addPayment() {
        String name = binding.edtName.getText().toString().trim();
        String cardNumber = binding.edtCardNumber.getText().toString().trim();
        String strCvv = binding.edtCvv.getText().toString().trim();
        String strDate = binding.edtDate.getText().toString().trim();

        boolean isAllValidate = ValidateStringUtils.validateEmpty(binding.layoutName, name)
        && ValidateStringUtils.validateCardNumber(binding.layoutCardNumber, cardNumber)
        && ValidateStringUtils.validateCVV(binding.layoutCvv, strCvv);

        if(isAllValidate){
            progressDialog.show();
            int id = (int) System.currentTimeMillis();
            DatabaseReference mRef = database.getReference("Account").child(getUid()).child("payment");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Payment payment;
                            if(snapshot.exists()){
                                payment = new Payment(id,cardNumber, name, strDate, strCvv);
                            } else {
                                payment = new Payment(id,cardNumber, name, strDate, strCvv, true);
                            }
                            mRef.child(String.valueOf(id)).setValue(payment)
                                    .addOnCompleteListener(task -> {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            if(checkCurrent == CART_CHECK){
                                                startActivity(new Intent(AddPaymentActivity.this, CheckOutActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(AddPaymentActivity.this, "Thêm tài khoản ngân hàng thành công", Toast.LENGTH_SHORT).show();
                                                clearText();
                                            }
                                        } else
                                            Toast.makeText(AddPaymentActivity.this, "Thêm tài khoản ngân hàng thất bại", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPaymentActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showDatePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int monthCurrent = calendar.get(Calendar.MONTH);
        int yearCurrent = calendar.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
            calendar.set(year, month, dayOfMonth);
            binding.edtDate.setText(dateFormat.format(calendar.getTime()));
        }, yearCurrent, monthCurrent, dayCurrent);

       dialog.show();
    }

    private void clearText() {
        binding.edtName.setText("");
        binding.edtCardNumber.setText("");
        binding.edtCvv.setText("");
        binding.edtDate.setText("");

    }
}