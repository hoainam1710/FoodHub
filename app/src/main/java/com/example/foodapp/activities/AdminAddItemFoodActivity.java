package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityAdminAddItemFoodBinding;
import com.example.foodapp.fragments.BottomSheetNotifySuccessFragment;
import com.example.foodapp.fragments.ProgressDialogFragment;
import com.example.foodapp.model.Category;
import com.example.foodapp.model.Food;
import com.example.foodapp.utils.ValidateStringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminAddItemFoodActivity extends BaseActivity {
    private ActivityAdminAddItemFoodBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private static final String TAG = "AdminAddItemFoodActivity";
    private ArrayAdapter<String> arrayAdapter;
    private Uri uriSelected;
    private ProgressDialogFragment progressDialogFragment;
    private BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment;
    private Food foodUpdate;
    public static final int UPDATE_TYPE = 0;
    public static final int ADD_TYPE = 1;
    private int currentType = ADD_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminAddItemFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUnderLineText();
        registerActivityResultImage();
        getFoodByIntent();
        getAllCategory();
        initListener();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());

        binding.imgUploadImage.setOnClickListener(v -> onClickChangeImage());

        binding.txtUploadImage.setOnClickListener(v -> onClickChangeImage());

        binding.btnSubmit.setOnClickListener(v -> {
            if(currentType == ADD_TYPE){
                saveImageToStorage();
            } else updateFood();
        });
    }

    private void updateFood() {
        if(uriSelected == null){
            uriSelected = Uri.parse(foodUpdate.getImagePath());
            updateFoodToRealtime(uriSelected);
            return;
        }
        showProgressDialogFragment();
        storage.getReference("AddFoodItems")
                .child(String.valueOf(System.currentTimeMillis()))
                .putFile(uriSelected)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl()
                                .addOnSuccessListener(this::updateFoodToRealtime)
                                .addOnFailureListener(e -> {
                                    progressDialogFragment.dismiss();
                                    Toast.makeText(AdminAddItemFoodActivity.this, "Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();});
                    }
                });
    }

    private void updateFoodToRealtime(Uri uri){
        String nameFood = Objects.requireNonNull(binding.edtNameFood.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.edtDescription.getText()).toString().trim();
        String strPrice = Objects.requireNonNull(binding.edtPrice.getText()).toString().trim();
        String strRating = Objects.requireNonNull(binding.edtRating.getText()).toString().trim();
        String strTimeValue = Objects.requireNonNull(binding.edtTimeValue.getText()).toString().trim();
        boolean isBestFood = binding.cboBestFood.isChecked();
        int categoryId = (int) binding.spnCategory.getSelectedItemId();
        int id = foodUpdate.getId();
        String imagePath = uri.toString();

        int timeValue = Integer.parseInt(strTimeValue);
        float price =  Float.parseFloat(strPrice);
        int rating = Integer.parseInt(strRating);

        int timeValueId = -1;
        if(timeValue <= 10){
            timeValueId = 0;
        } else if(timeValue <= 30){
            timeValueId = 1;
        } else timeValueId = 2;

        int priceId = -1;
        if(price >= 1 && price <= 10){
            priceId = 0;
        } else if(price <= 30){
            priceId = 1;
        } else priceId = 2;


        boolean isValid = ValidateStringUtils.validateEmpty(binding.layoutNameFood, nameFood)
                && ValidateStringUtils.validateEmpty(binding.layoutDescription, description)
                && ValidateStringUtils.validateEmpty(binding.layoutPrice, strPrice)
                && checkPrice(price)
                && ValidateStringUtils.validateEmpty(binding.layoutRating, strRating)
                && checkRating(rating)
                && ValidateStringUtils.validateEmpty(binding.layoutTimeValue, strTimeValue);
        if(isValid){
            Food food = new Food(id, nameFood, categoryId, description, imagePath, Float.parseFloat(strPrice),
                    priceId, Float.parseFloat(strRating), timeValueId, timeValue, isBestFood);

            showProgressDialogFragment();
            database.getReference("Foods").child(String.valueOf(foodUpdate.getId()))
                    .setValue(food)
                    .addOnCompleteListener(task -> {
                        progressDialogFragment.dismiss();
                        if(task.isSuccessful()){
                            showBottomSheetNotifySuccessFragment("Thay đổi thông tin món ăn thành công");
                        } else
                            Toast.makeText(AdminAddItemFoodActivity.this, "Thay đổi thông tin món ăn thất bại!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    private void getFoodByIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        Food food = (Food) bundle.getSerializable("food");
        if(food == null){
            currentType = ADD_TYPE;
            binding.txtTitle.setText("Thêm món ăn");
            return;
        }
        foodUpdate = food;
        currentType = UPDATE_TYPE;
        binding.txtTitle.setText("Thay đổi món ăn");
        setFoodUpdate(food);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setFoodUpdate(Food food){
        Glide.with(AdminAddItemFoodActivity.this)
                .load(food.getImagePath())
                .error(R.drawable.ic_error)
                .into(binding.imgUploadImage);
        if(binding.imgUploadImage.getDrawable() != getDrawable(R.drawable.ic_error)){
            binding.imgUploadImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.imgUploadImage.setPadding(0, 0, 0, 0);
        } else {
            binding.imgUploadImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            binding.imgUploadImage.setPadding(30, 30, 30, 30);
        }

        binding.edtNameFood.setText(food.getTitle());
        binding.edtDescription.setText(food.getDescription());
        binding.edtPrice.setText(String.valueOf(food.getPrice()));
        binding.edtRating.setText(String.valueOf(food.getStar()));
        binding.edtTimeValue.setText(String.valueOf(food.getTimeValue()));
        binding.cboBestFood.setChecked(food.isBestFood());

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void registerActivityResultImage() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if(o.getResultCode() == RESULT_OK){
                        Intent intent = o.getData();
                        if(intent == null){
                            return;
                        }
                        Uri uri = intent.getData();
                        if(uri == null){
                            return;
                        }
                        uriSelected = uri;
                        Glide.with(AdminAddItemFoodActivity.this)
                                .load(uri)
                                .error(R.drawable.ic_error)
                                .into(binding.imgUploadImage);
                        if(binding.imgUploadImage.getDrawable() != getDrawable(R.drawable.ic_error)){
                            binding.imgUploadImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            binding.imgUploadImage.setPadding(0, 0, 0, 0);
                        } else {
                            binding.imgUploadImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            binding.imgUploadImage.setPadding(30, 30, 30, 30);
                        }

                    }
                });
    }

    private void saveImageToStorage() {
        if(uriSelected == null){
            Toast.makeText(this, "Bạn chưa thêm ảnh cho món ăn!", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialogFragment();
        storage.getReference("AddFoodItems")
                .child(String.valueOf(System.currentTimeMillis()))
                .putFile(uriSelected)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl()
                                .addOnSuccessListener(this::saveFoodToRealTime)
                                .addOnFailureListener(e -> {
                                        progressDialogFragment.dismiss();
                                        Toast.makeText(AdminAddItemFoodActivity.this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show();});
                    }
                });
    }

    private void getAllCategory(){
        List<String> categories = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnCategory.setAdapter(arrayAdapter);
        database.getReference("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            categories.clear();
                            for (DataSnapshot item: snapshot.getChildren()){
                                Category category = item.getValue(Category.class);
                                if(category == null){
                                    continue;
                                }
                                categories.add(category.getName());
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getAllCategoryCancelled: "+ error.getMessage());
                    }
                });
    }


    private void showProgressDialogFragment(){
        progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag("progressDialogFragment");
        if(progressDialogFragment == null){
            progressDialogFragment = new ProgressDialogFragment();
        }
        progressDialogFragment.show(getSupportFragmentManager(), "progressDialogFragment");
    }

    private void showBottomSheetNotifySuccessFragment(String message){
        bottomSheetNotifySuccessFragment = (BottomSheetNotifySuccessFragment) getSupportFragmentManager().findFragmentByTag("bottomSheetNotifySuccessFragment");
        if(bottomSheetNotifySuccessFragment == null){
            bottomSheetNotifySuccessFragment = new BottomSheetNotifySuccessFragment(message);
        }
        bottomSheetNotifySuccessFragment.show(getSupportFragmentManager(), "bottomSheetNotifySuccessFragment");
        new Handler().postDelayed(() -> bottomSheetNotifySuccessFragment.dismiss(), 3000);
    }

    private boolean checkPrice(float price){
        if(price == 0){
            binding.layoutPrice.setError("Giá món ăn phải lớn hơn $0");
            return false;
        } binding.layoutPrice.setError("");
        return true;
    }

    private boolean checkRating(int rating){
        if(rating < 0 || rating > 5){
            binding.layoutRating.setError("Rating không nhỏ hơn 0 và không lớn hơn 5");
            return false;
        }
        binding.layoutRating.setError("");
        return true;
    }

    private void saveFoodToRealTime(Uri uri){
        String nameFood = Objects.requireNonNull(binding.edtNameFood.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.edtDescription.getText()).toString().trim();
        String strPrice = Objects.requireNonNull(binding.edtPrice.getText()).toString().trim();
        String strRating = Objects.requireNonNull(binding.edtRating.getText()).toString().trim();
        String strTimeValue = Objects.requireNonNull(binding.edtTimeValue.getText()).toString().trim();
        boolean isBestFood = binding.cboBestFood.isChecked();
        int categoryId = (int) binding.spnCategory.getSelectedItemId();
        int id = (int) System.currentTimeMillis();
        String imagePath = uri.toString();

        int timeValue = Integer.parseInt(strTimeValue);
        float price =  Float.parseFloat(strPrice);
        int rating = Integer.parseInt(strRating);

        int timeValueId = -1;
        if(timeValue <= 10){
            timeValueId = 0;
        } else if(timeValue <= 30){
            timeValueId = 1;
        } else timeValueId = 2;

        int priceId = -1;
        if(price >= 1 && price <= 10){
            priceId = 0;
        } else if(price <= 30){
            priceId = 1;
        } else priceId = 2;


        boolean isValid = ValidateStringUtils.validateEmpty(binding.layoutNameFood, nameFood)
                && ValidateStringUtils.validateEmpty(binding.layoutDescription, description)
                && ValidateStringUtils.validateEmpty(binding.layoutPrice, strPrice)
                && checkPrice(price)
                && ValidateStringUtils.validateEmpty(binding.layoutRating, strRating)
                && checkRating(rating)
                && ValidateStringUtils.validateEmpty(binding.layoutTimeValue, strTimeValue);
        if(isValid){
            Food food = new Food(id, nameFood, categoryId, description, imagePath, Float.parseFloat(strPrice),
                    priceId, Float.parseFloat(strRating), timeValueId, timeValue, isBestFood);

            database.getReference("Foods").child(String.valueOf(System.currentTimeMillis()))
                    .setValue(food)
                    .addOnCompleteListener(task -> {
                        progressDialogFragment.dismiss();
                        if(task.isSuccessful()){
                            showBottomSheetNotifySuccessFragment("Thêm món ăn thành công");
                        } else
                            Toast.makeText(AdminAddItemFoodActivity.this, "Thêm món ăn thất bại!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void onClickChangeImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void setUnderLineText(){
        String text = "Tải ảnh lên";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        binding.txtUploadImage.setText(spannableString);
    }


}