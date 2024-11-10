package com.example.foodapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.adapter.ListSubmitReviewAdapter;
import com.example.foodapp.base.BaseActivity;
import com.example.foodapp.databinding.ActivityReviewBinding;
import com.example.foodapp.databinding.ItemRcvListSubmitReviewBinding;
import com.example.foodapp.fragments.BottomSheetNotifySuccessFragment;
import com.example.foodapp.fragments.ProgressDialogFragment;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Food;
import com.example.foodapp.model.Order;
import com.example.foodapp.model.Review;
import com.example.foodapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReviewActivity extends BaseActivity implements ListSubmitReviewAdapter.IListSubmitReview {
    private ActivityReviewBinding binding;
    private Order orderSelected;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private List<Uri> listUriReviews;
    private ListSubmitReviewAdapter listSubmitReviewAdapter;
    private ProgressDialogFragment progressDialogFragment;
    private static final String TAG = "ReviewActivity";
    private int cartCounter = -1;
    private List<Task<Void>> listTasks;
    private int cartPositionAddImage = -1;
    private User userReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialogFragment = new ProgressDialogFragment();
        listUriReviews = new ArrayList<>();
        listTasks = new ArrayList<>();

        getOrderByIntent();
        getUserReview();
        initRcvListSubmitReview();
        registerGetImageResult();
        initListener();
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void registerGetImageResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , o -> {
                    Intent intent = o.getData();
                    if(intent == null){
                        return;
                    }
                    Uri uri = intent.getData();
                    if(uri == null){
                        return;
                    }
                    listUriReviews.add(uri);
                    listSubmitReviewAdapter.setListUri(listUriReviews, cartPositionAddImage);

                });
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

    private void initRcvListSubmitReview() {
        List<Cart> listCartOrder = new ArrayList<>();
        listSubmitReviewAdapter = new ListSubmitReviewAdapter(this, listCartOrder, this);
        binding.rcvListSubmitReview.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvListSubmitReview.setAdapter(listSubmitReviewAdapter);
        loadListSubmitReview(listCartOrder, listSubmitReviewAdapter);
    }

    private void loadListSubmitReview(List<Cart> listCartOrder, ListSubmitReviewAdapter listSubmitReviewAdapter){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        database.getReference("Orders").child(orderSelected.getId()).child("lists")
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
                                if(!cart.isCheckedReview()){
                                    listCartOrder.add(cart);
                                }
                            }
                        }
                        cartCounter = listCartOrder.size();
                        listSubmitReviewAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListSubmitReviewOnCancelled: "+ error.getMessage());
                        Toast.makeText(ReviewActivity.this, "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void addImageReview(Food food, int position) {
        Log.d(TAG, "addImageReview");
        cartPositionAddImage = position;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);

    }

    @Override
    public void submitReview(ItemRcvListSubmitReviewBinding itemRcvListSubmitReviewBinding, Cart cart, int position) {
        progressDialogFragment.show(getSupportFragmentManager(), TAG);
        List<Task<Uri>> listUploadTask = new ArrayList<>();
        for(Uri uri: listUriReviews){
            String id = UUID.randomUUID().toString();
            StorageReference sRef = storage.getReference("Reviews").child(getUid()).child(id);
//            UploadTask task = sRef.putFile(uri);
            Task<Uri> uploadTask = sRef.putFile(uri)
                    .continueWithTask(task -> {
                        if(!task.isSuccessful()){
                            throw Objects.requireNonNull(task.getException());
                        }
                        return sRef.getDownloadUrl();
                    });
            listUploadTask.add(uploadTask);
        }
        Task<List<Object>> listTask = Tasks.whenAllSuccess(listUploadTask);
        listTask.addOnSuccessListener(objects -> {
            List<String> listUlrDownload = new ArrayList<>();
            for (Object uri : objects) {
                listUlrDownload.add(uri.toString());
            }
            saveReviewToRealtime(itemRcvListSubmitReviewBinding, cart, listUlrDownload, position);

        });
        listTask.addOnFailureListener(e -> Toast.makeText(ReviewActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

//    private void saveReviewToRealtime(ItemRcvListSubmitReviewBinding itemRcvListSubmitReviewBinding, Cart cart, List<String> listUrlDownload){
//        String id = "RV"+ System.currentTimeMillis();
//        int idFood = cart.getFood().getId();
//        int rating = (int) itemRcvListSubmitReviewBinding.ratingStar.getRating();
//        String idUser = getUid();
//        String content = itemRcvListSubmitReviewBinding.edtContent.getText().toString().trim();
//        String idOrder = orderSelected.getId();
//        Review review = new Review(id, idUser, idFood, idOrder, rating, content, listUrlDownload);
//
//        database.getReference("Reviews").child(review.getId())
//                .setValue(review)
//                .addOnCompleteListener(task -> {
//                    progressDialogFragment.dismiss();
//                    if(task.isSuccessful()){
//
//                        database.getReference("Orders").child(idOrder).child("checkedReview").setValue(true);
//                        BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment
//                                = new BottomSheetNotifySuccessFragment("Đánh giá sản phẩm thành công!");
//                        bottomSheetNotifySuccessFragment.show(getSupportFragmentManager(), "Review Success");
//                        new Handler().postDelayed(() -> {
//                            bottomSheetNotifySuccessFragment.dismiss();
//                            startActivity(new Intent(ReviewActivity.this, ReviewHistoryActivity.class));
//                        }, 3000);
//                    } else {
//                        Toast.makeText(ReviewActivity.this, "Đánh giá sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void saveReviewToRealtime(ItemRcvListSubmitReviewBinding itemRcvListSubmitReviewBinding, Cart cart, List<String> listUrlDownload, int position){
        String id = "RV"+ System.currentTimeMillis();
        Food food = cart.getFood();
        int rating = (int) itemRcvListSubmitReviewBinding.ratingStar.getRating();
        String content = itemRcvListSubmitReviewBinding.edtContent.getText().toString().trim();
        String idOrder = orderSelected.getId();
        String dateReview = getDateReview();
        Review review = new Review(id, userReview, food, orderSelected, rating, content, dateReview, listUrlDownload);


        database.getReference("Reviews").child(review.getId())
                .setValue(review)
                .addOnCompleteListener(task -> {
                    progressDialogFragment.dismiss();
                    if(task.isSuccessful()){
                        database.getReference("Orders").child(idOrder).child("checkedReview").setValue(true);
                        DatabaseReference cartRef = database.getReference("Orders").child(idOrder).child("lists");
                        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot item: snapshot.getChildren()){
                                    Cart cartItem = item.getValue(Cart.class);
                                    if(cartItem == null){
                                        continue;
                                    }
                                    if(cartItem.getId() == cart.getId()){
                                        cartRef.child(Objects.requireNonNull(item.getKey())).child("checkedReview")
                                                .setValue(true);
                                        break;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "Lỗi: "+ error.getMessage());
                            }
                        });
                        listUriReviews.clear();
                        listSubmitReviewAdapter.clearMapDataReview(position);
                        listTasks.add(task);
                        checkAllTaskSuccess();

                    } else {
                        Toast.makeText(ReviewActivity.this, "Đánh giá sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });

        checkAllTaskSuccess();


    }

    private void checkAllTaskSuccess(){
        Log.d(TAG, "cartCounter: "+ cartCounter);
        if(listTasks.size() == cartCounter){
            Tasks.whenAllSuccess(listTasks)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            showNotifySuccessFragment();
                        } else {
                            Log.d(TAG, "saveReviewToRealtimeError: "+ Objects.requireNonNull(task.getException()).getMessage());
                            Toast.makeText(ReviewActivity.this, "Lỗi: "+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String getDateReview(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return format.format(calendar.getTime());
    }

    private void getUserReview(){
        database.getReference("Account").child(getUid()).child("profile")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user == null){
                            return;
                        }
                        userReview = user;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getUserOrderCancelled: "+ error.getMessage());
                    }
                });
    }


    private void showNotifySuccessFragment(){
        BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment
                = new BottomSheetNotifySuccessFragment("Đánh giá sản phẩm thành công!");
        bottomSheetNotifySuccessFragment.show(getSupportFragmentManager(), "Review Success");
        new Handler().postDelayed(() -> {
            bottomSheetNotifySuccessFragment.dismiss();
            nextToReviewHistoryActivity();
        }, 3000);
    }

    private void nextToReviewHistoryActivity(){
        Intent intent = new Intent(ReviewActivity.this, ReviewHistoryActivity.class);
        intent.putExtra("from", "ReviewActivity");
        startActivity(intent);
    }

}