package com.example.foodapp.utils;

import android.text.TextUtils;
import android.util.Patterns;


import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidateStringUtils {
    public static boolean validateEmail(String email, TextInputLayout layout){
        if (TextUtils.isEmpty(email)) {
            layout.setError("Email không rỗng");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout.setError("Email chưa hợp lệ");
            return false;
        } else {
            layout.setError(null);
            layout.clearFocus();
            return true;
        }
    }

    public static boolean validatePassword(String password, TextInputLayout layout){
        if (TextUtils.isEmpty(password)) {
            layout.setError("Password không rỗng");
            return false;
        } else if (password.length() < 6 || password.length() > 20) {
            layout.setError("Password không nhỏ hơn 6 và không lớn hơn 20 kí tự");
            return false;
        } else if (!password.matches(".*[A-Z].*")) {
            layout.setError("Password phải có ít nhất 1 chữ cái in hoa");
            return false;
        } else if (!password.matches(".*[a-z].*")) {
            layout.setError("Password phải có ít nhất 1 chữ cái in thương");
            return false;
        } else if (!password.matches(".*[0-9].*")) {
            layout.setError("Password phải có ít nhất 1 chữ số");
            return false;
        } else if (!password.matches(".*[@#\\$%^&+=!].*")) {
            layout.setError("Password phải có ít nhất 1 kí tự đặc biệt (@#\\$%^&+=!)");
            return false;
        } else {
            layout.setError(null);
            layout.clearFocus();
            return true;
        }
    }

    public static boolean validateConfirmPassword(String confirm, String password, TextInputLayout layout){
        if (TextUtils.isEmpty(password)) {
            layout.setError("Password không rỗng");
            return false;
        } else if (password.length() < 6) {
            layout.setError("Password phải có hơn 6 kí tự");
            return false;
        } else if (!password.matches(".*[A-Z].*")) {
            layout.setError("Password phải có ít nhất 1 chữ cái in hoa");
            return false;
        } else if (!password.matches(".*[a-z].*")) {
            layout.setError("Password phải có ít nhất 1 chữ cái in thương");
            return false;
        } else if (!password.matches(".*[0-9].*")) {
            layout.setError("Password phải có ít nhất 1 chữ số");
            return false;
        } else if (!password.matches(".*[@#\\$%^&+=!].*")) {
            layout.setError("Password phải có ít nhất 1 kí tự đặc biệt (@#\\$%^&+=!)");
            return false;
        } else if(!confirm.equals(password)) {
            layout.setError("Password không trùng khớp");
            return false;
        } else {
            layout.setError(null);
            layout.clearFocus();
            return true;
        }
    }

    public static boolean validateEmpty(TextInputLayout layout, String s){
        if(s.isEmpty()){
            layout.setError("Thông tin bắt buộc!");
            return false;
        }
        layout.setError(null);
        layout.clearFocus();
        return true;
    }

    public static boolean validateCardNumber(TextInputLayout layout, String cardNumber){
        if(cardNumber.isEmpty()){
            layout.setError("Thông tin bắt buộc!");
            return false;
        } else if(cardNumber.length() != 16){
            layout.setError("Số tài khoản phải có 16 chữ số.");
            return false;
        }
        layout.setError(null);
        layout.clearFocus();
        return true;

    }

    public static boolean validateCVV(TextInputLayout layout, String cvv){
        if(cvv.isEmpty()){
            layout.setError("Thông tin bắt buộc!");
            return false;
        } else if (cvv.length() != 3){
            layout.setError("CVV phải đủ 3 chữ số");
            return false;
        }
        layout.setError(null);
        layout.clearFocus();
        return true;
    }

    public static boolean validatePhoneNumber(TextInputLayout layout, String phoneNumber){
        String pattern = "0[0-9]{9}";
        if(!phoneNumber.matches(pattern)){
            layout.setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng chữ số 0");
            return false;
        }
        layout.setError(null);
        return true;
    }


}
