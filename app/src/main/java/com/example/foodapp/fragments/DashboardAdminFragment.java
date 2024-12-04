package com.example.foodapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodapp.R;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentDashboardAdminBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdminFragment extends BaseFragment {
    private FragmentDashboardAdminBinding binding;
    private int totalOrderCompleted = 0;
    private int totalOrderActive = 0;
    private int totalOrderCanceled = 0;

    private boolean isTaskOrderCompletedSuccess = false;
    private boolean isTaskOrderActiveSuccess = false;
    private boolean isTaskOrderCanceledSuccess = false;
    private static final String TAG = "DashboardAdminFragment";
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentDashboardAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTotalOrders();
        initTotalCustomer();
        initTotalFoods();
        initTotalReviews();

        initTotalOrderInActive();
        initTotalOrderInCompleted();
        initTotalOrderInCanceled();
        initTotalOrderInActive();

        initNavController();
        initListener();

    }

    private void initNavController() {
        navController = Navigation.findNavController(binding.getRoot());
    }

    private void initListener() {
        binding.layoutOrderManager.setOnClickListener(v -> navController.navigate(R.id.orderManagerFragment));
    }

    private void initOrderChart() {
        Log.d(TAG, "totalComplete = "+ totalOrderCompleted);
        Log.d(TAG, "totalActive = "+ totalOrderActive);
        Log.d(TAG, "totalCanceled = "+ totalOrderCanceled);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalOrderCanceled, "Đã hủy"));
        entries.add(new PieEntry(totalOrderCompleted, "Hoàn thành"));
        entries.add(new PieEntry(totalOrderActive, "Xủ lý"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Đơn hàng");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(R.color.blue_grey);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();
        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setCenterText("Đơn hàng");
        binding.pieChart.animate();
    }

    private void checkAllTaskOrderSuccess(){
        if(isTaskOrderCompletedSuccess && isTaskOrderActiveSuccess && isTaskOrderCanceledSuccess){
            initOrderChart();
        }
    }

    private void initTotalOrderInActive() {
        database.getReference("Orders").orderByChild("status").equalTo("active")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.txtTotalOrderActive.setText(String.valueOf(snapshot.getChildrenCount()));
                    totalOrderActive = (int) snapshot.getChildrenCount();
                    isTaskOrderActiveSuccess = true;
                    checkAllTaskOrderSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getTotalOrderActiveCancelled: "+ error.getMessage());
                binding.txtTotalOrderActive.setText("0");
                isTaskOrderActiveSuccess = true;
                checkAllTaskOrderSuccess();
            }
        });
    }

    private void initTotalOrderInCompleted() {
        database.getReference("Orders").orderByChild("status").equalTo("completed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            binding.txtTotalOrderCompleted.setText(String.valueOf(snapshot.getChildrenCount()));
                            totalOrderCompleted = (int) snapshot.getChildrenCount();
                            isTaskOrderCompletedSuccess = true;
                            checkAllTaskOrderSuccess();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getTotalOrderCompletedError: "+ error.getMessage());
                        binding.txtTotalOrderCompleted.setText("0");
                        isTaskOrderCompletedSuccess = true;
                        checkAllTaskOrderSuccess();
                    }
                });
    }

    private void initTotalOrderInCanceled() {
        database.getReference("Orders").orderByChild("status").equalTo("canceled")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            binding.txtTotalOrderCanceled.setText(String.valueOf(snapshot.getChildrenCount()));
                            totalOrderCanceled = (int) snapshot.getChildrenCount();
                            isTaskOrderCanceledSuccess = true;
                            checkAllTaskOrderSuccess();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getTotalOrderCanceledError: "+ error.getMessage());
                        binding.txtTotalOrderCanceled.setText("0");
                        isTaskOrderCanceledSuccess = true;
                        checkAllTaskOrderSuccess();
                    }
                });
    }

    private void initTotalReviews() {
        database.getReference("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.txtTotalReview.setText(String.valueOf(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getTotalReviewCancelled: "+ error.getMessage());
                binding.txtTotalReview.setText("0");
            }
        });
    }

    private void initTotalFoods() {
        database.getReference("Foods").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.txtTotalFood.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getTotalFoodsCancelled: "+ error.getMessage());
                binding.txtTotalFood.setText("0");
            }
        });
    }

    private void initTotalCustomer() {
        database.getReference("Account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.txtTotalCustomer.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getTotalCustomerCancelled: "+ error.getMessage());
                binding.txtTotalCustomer.setText("0");
            }
        });
    }

    private void initTotalOrders() {
        database.getReference("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.txtTotalOrder1.setText(String.valueOf(snapshot.getChildrenCount()));
                    binding.txtTotalOrder2.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getTotalOrderCancelled: "+ error.getMessage());
                binding.txtTotalOrder1.setText("0");
                binding.txtTotalOrder2.setText("0");
            }
        });
    }
}