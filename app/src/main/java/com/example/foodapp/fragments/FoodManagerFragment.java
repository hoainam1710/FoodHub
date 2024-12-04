package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodapp.activities.AdminAddItemFoodActivity;
import com.example.foodapp.activities.AdminManagerFoodDetailActivity;
import com.example.foodapp.adapter.AdminListFoodManagerAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentFoodManagerBinding;
import com.example.foodapp.model.Food;
import com.example.foodapp.utils.SetupFabUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodManagerFragment extends BaseFragment implements AdminListFoodManagerAdapter.IAdminListFoodManager {
    private FragmentFoodManagerBinding binding;
    private List<Food> foods;
    private AdminListFoodManagerAdapter adapter;
    private static final String TAG = "FoodManagerFragment";
    private ProgressDialogFragment progressDialogFragment;
    private BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentFoodManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRcvListFood();
        SetupFabUtils.setUpFabWithRcv(binding.rcvListFood, binding.fabAdd);
        initListener();
    }

    private void initListener() {
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchFood(Objects.requireNonNull(binding.edtSearch.getText()).toString().trim());
                return true;
            }
            return false;
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFood(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), AdminAddItemFoodActivity.class)));


    }

    private void searchFood(String keySearch){
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Foods")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            foods.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Food food = item.getValue(Food.class);
                                if(food != null && food.getTitle().toLowerCase().contains(keySearch.toLowerCase())){
                                    foods.add(food);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getListFoodCancelled: "+ error.getMessage());
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initRcvListFood() {
        foods = new ArrayList<>();
        binding.rcvListFood.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        adapter = new AdminListFoodManagerAdapter(foods, this);
        binding.rcvListFood.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Foods")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            foods.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Food food = item.getValue(Food.class);
                                if(food == null){
                                    continue;
                                }
                                foods.add(food);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "getListFoodCancelled: "+ error.getMessage());
                        Toast.makeText(requireActivity(), "Lỗi: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNotifyBottomSheetDialog(String message){
        bottomSheetNotifySuccessFragment = (BottomSheetNotifySuccessFragment) requireActivity().getSupportFragmentManager()
                .findFragmentByTag("bottomSheetDialogFragment");
        if(bottomSheetNotifySuccessFragment == null){
            bottomSheetNotifySuccessFragment = new BottomSheetNotifySuccessFragment(message);
        }
        bottomSheetNotifySuccessFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheetDialogFragment");
        new Handler().postDelayed(() -> bottomSheetNotifySuccessFragment.dismiss(), 3000);
    }

    private void showProgressDialog() {
        progressDialogFragment = (ProgressDialogFragment) requireActivity().getSupportFragmentManager()
                .findFragmentByTag("progressDialogFragment");
        if(progressDialogFragment == null){
            progressDialogFragment = new ProgressDialogFragment();
        }
        progressDialogFragment.show(requireActivity().getSupportFragmentManager(), "progressDialogFragment");

    }

    private void onClickDeleteFood(Food food){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false)
                .setTitle("Cảnh báo")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn "+ food.getTitle() + " không?")
                .setNegativeButton("Đồng ý", (dialog, which) -> {
                    showProgressDialog();
                    database.getReference("Foods")
                            .child(String.valueOf(food.getId()))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                progressDialogFragment.dismiss();
                                if(task.isSuccessful()){
                                    showNotifyBottomSheetDialog("Xóa món ăn thành công!");
                                } else Toast.makeText(requireActivity()
                                        , "Xóa món ăn không thành công!", Toast.LENGTH_SHORT).show();
                            });
                })

                .setPositiveButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create().show();
    }


    @Override
    public void viewDetailFood(Food food) {
        Intent intent = new Intent(requireActivity(), AdminManagerFoodDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", food);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteFood(Food food) {
        onClickDeleteFood(food);
    }

    @Override
    public void updateFood(Food food) {
        Intent intent = new Intent(requireActivity(), AdminAddItemFoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", food);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}