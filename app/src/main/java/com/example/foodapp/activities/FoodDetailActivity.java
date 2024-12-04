package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.adapter.ListCompletedReviewAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityFoodDetailBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Food;
import com.example.foodapp.utils.ImageReviewDetail;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailActivity extends BaseActivity implements ListCompletedReviewAdapter.IListCompletedReview {
    private ActivityFoodDetailBinding binding;
    private int quantity = 1;
    private Food foodSelected;
    private boolean isCheckFavorite = false;
    private static final String TAG = "FoodDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().getDecorView().setSystemUiVisibility(0);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().setStatusBarColor(Color.WHITE);

        getResultIntent();
        initCartNumber();
        initRcvListReview();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.txtIncrease.setOnClickListener(v -> {
            if(quantity >= 1 ){
                quantity++;
                binding.txtQuantity.setText(quantity+"");
                binding.txtTotal.setText(foodSelected.getPrice() * quantity + "$");
            }
        });

        binding.txtReduce.setOnClickListener(v -> {
            if(quantity >= 2){
                quantity--;
                binding.txtQuantity.setText(quantity+"");
                binding.txtTotal.setText(foodSelected.getPrice() * quantity + "$");
            }
        });

        binding.layoutCart.setOnClickListener(v ->
                startActivity(new Intent(FoodDetailActivity.this, CartActivity.class)));

        // add to cart
        binding.btnAddToCart.setOnClickListener(v -> addToCart());

        binding.imgFavorite.setOnClickListener(v -> clickFavorite());

        binding.txtViewAllReview.setOnClickListener(v -> onClickViewAllReview());
    }

    private void onClickViewAllReview(){
        Intent intent = new Intent(FoodDetailActivity.this, AllReviewOfFoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", foodSelected);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initCartNumber() {
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            binding.layoutCartNumber.setVisibility(View.VISIBLE);
                            binding.txtCartNumber.setText(String.valueOf(snapshot.getChildrenCount()));
                        } else binding.layoutCartNumber.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getCartNumberCancelled: "+ error.getMessage());
                    }
                });
    }

    private void initRcvListReview() {
        List<Review> listReview = new ArrayList<>();
        binding.rcvListReview.setLayoutManager(new LinearLayoutManager(this));
        ListCompletedReviewAdapter adapter = new ListCompletedReviewAdapter(this, listReview, this);
        binding.rcvListReview.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvListReview.addItemDecoration(itemDecoration);
        loadData(listReview, adapter);
    }

    private void loadData(List<Review> listReview, ListCompletedReviewAdapter adapter) {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            listReview.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getFood().getId() == foodSelected.getId()){
                                    listReview.add(review);
                                }
                            }
                            if(listReview.isEmpty()){
                                binding.txtNumberReview.setText("(0 lượt đánh giá)");
                                binding.txtViewAllReview.setVisibility(View.GONE);
                            } else {
                                binding.txtNumberReview.setText("("+ listReview.size()+ " lượt đánh giá)");
                                binding.txtViewAllReview.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.txtNumberReview.setText("(0 lượt đánh giá)");
                            binding.txtViewAllReview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListReviewCancelled: "+ error.getMessage());
                    }
                });
    }


    private void clickFavorite(){
        if(!isCheckFavorite){
            binding.imgIconFavorite.setImageResource(R.drawable.ic_favorite_checked);
            isCheckFavorite = true;
            saveFavoriteToRealtime(foodSelected);
        } else {
            binding.imgIconFavorite.setImageResource(R.drawable.ic_favorite);
            isCheckFavorite = false;
            removeFavoriteToRealtime(foodSelected);
        }

    }

    private void saveFavoriteToRealtime(Food food){
        database.getReference("Account").child(getUid()).child("favorite")
                .child(String.valueOf(food.getId()))
                .setValue(food)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(FoodDetailActivity.this, "Đã thêm sản phẩm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(FoodDetailActivity.this, "Không thể thêm sản phẩm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                });
    }
    private void removeFavoriteToRealtime(Food food){
        database.getReference("Account").child(getUid()).child("favorite")
                .child(String.valueOf(food.getId())).removeValue()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(FoodDetailActivity.this, "Đã xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FoodDetailActivity.this
                                , "Lỗi, không thể xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToCart() {
        int cartId = (int) System.currentTimeMillis();
        int quantity = Integer.parseInt(binding.txtQuantity.getText().toString());
        float cartTotal = quantity * foodSelected.getPrice();
        Cart cart = new Cart(cartId, foodSelected, quantity, cartTotal, false);
        database.getReference("Account").child(getUid())
                .child("cart").child(String.valueOf(cartId)).setValue(cart)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(FoodDetailActivity.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        setEnableButton(binding.btnAddToCart);
                    } else Toast.makeText(this, "Lỗi, không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("SetTextI18n")
    private void getResultIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        foodSelected = (Food) bundle.getSerializable("food");
        if(foodSelected == null){
            return;
        }

        Glide.with(this).load(foodSelected.getImagePath()).into(binding.imgFood);
        binding.txtTitle.setText(foodSelected.getTitle());
        binding.txtPrice.setText(foodSelected.getPrice() + "$");
        binding.ratingStar.setRating(foodSelected.getStar());
        binding.txtRating.setText(foodSelected.getStar() + "/5");
        binding.txtTimeValue.setText(foodSelected.getTimeValue() + "min");
        binding.txtDescription.setText(foodSelected.getDescription());
        binding.txtQuantity.setText(String.valueOf(quantity));
        binding.txtTotal.setText(foodSelected.getPrice() * quantity + "$");

        // check in cart
        checkFoodInCart(foodSelected);
        // check is favorite
        checkFoodInListFavorite(foodSelected);

    }

    private void checkFoodInCart(Food food){
        database.getReference("Account").child(getUid()).child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if(cart == null){
                                    return;
                                }
                                Food foodItem = cart.getFood();
                                if(foodItem != null && foodItem.getId() == food.getId()){
                                    setEnableButton(binding.btnAddToCart);
                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void checkFoodInListFavorite(Food food){
        database.getReference("Account").child(getUid()).child("favorite")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                if(food.getId() == item.getValue(Food.class).getId()){
                                    isCheckFavorite = true;
                                    binding.imgIconFavorite.setImageResource(R.drawable.ic_favorite_checked);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void setEnableButton(Button button){
        button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_enable_bg));
        button.setText("Đã thêm vào giỏ hàng");
        button.setTextColor(ContextCompat.getColor(this,R.color.text_enable));
        button.setEnabled(false);

    }

    @Override
    public void onClickImage(String imgPath, int position, int numberImage) {
        database.getReference("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Review review = item.getValue(Review.class);
                                if(review == null){
                                    continue;
                                }
                                if(review.getListImagePath().contains(imgPath)){
                                    Intent intent = new Intent(FoodDetailActivity.this, ImageReviewDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    ImageReviewDetail imageReviewDetail = new ImageReviewDetail(review, imgPath, position, numberImage);
                                    bundle.putSerializable("imageReviewDetail", imageReviewDetail);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onClickImageReviewCancelled: "+ error.getMessage());
                    }
                });
    }
}