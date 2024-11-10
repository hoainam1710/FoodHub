package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodapp.R;
import com.example.foodapp.activities.CartActivity;
import com.example.foodapp.activities.FoodDetailActivity;
import com.example.foodapp.adapter.ListFavoriteAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentFavoriteBinding;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FavoriteFragment extends BaseFragment implements ListFavoriteAdapter.IFoodFavorite {
    private FragmentFavoriteBinding binding;
    private ListFavoriteAdapter adapter;
    private List<Food> listsFood;
    private NavController navController;
    private static final String TAG = "FavoriteFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        initCartNumber();
        initRcv();
        initListener();
    }

    private void initListener() {
        binding.imgCart.setOnClickListener(v -> startActivity(new Intent(requireActivity(), CartActivity.class)));
        binding.btnRequiredShopping.setOnClickListener(v -> navController.navigate(R.id.homeUserFragment));

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

    private void initRcv() {
        listsFood = new ArrayList<>();
        adapter = new ListFavoriteAdapter(requireActivity(), listsFood, this);
        binding.rcvListFood.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rcvListFood.setAdapter(adapter);
        loadRcv();

    }

    private void loadRcv() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("Account").child(getUid()).child("favorite")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.GONE);
                        listsFood.clear();
                        if(snapshot.exists()){
                            for (DataSnapshot item: snapshot.getChildren()){
                                Food food = item.getValue(Food.class);
                                listsFood.add(food);
                            }
                        } else {
                            binding.txtNotExists.setVisibility(View.VISIBLE);
                            binding.btnRequiredShopping.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onClick(Food food) {
        Intent intent = new Intent(requireActivity(), FoodDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("food", food);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteFood(Food food) {
        database.getReference("Account").child(getUid()).child("favorite")
                .child(String.valueOf(food.getId())).removeValue()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(requireActivity(), "Đã xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(requireActivity(), "Không thể xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void addToCart(Food food) {
        int cartId = (int) System.currentTimeMillis();
        float cartTotal = food.getPrice();
        Cart cart = new Cart(cartId, food, 1, cartTotal, false);
        database.getReference("Account").child(getUid())
                .child("cart").child(String.valueOf(cartId)).setValue(cart)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(requireActivity(), "Đã thêm sản phẩm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(requireActivity(), "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                });
    }
}