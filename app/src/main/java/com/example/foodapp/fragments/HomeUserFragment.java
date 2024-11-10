package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.foodapp.R;
import com.example.foodapp.activities.CartActivity;
import com.example.foodapp.activities.FoodDetailActivity;
import com.example.foodapp.activities.ListFoodActivity;
import com.example.foodapp.activities.ListFoodPopularActivity;
import com.example.foodapp.activities.ListFoodSearchActivity;
import com.example.foodapp.adapter.BannerUserAdapter;
import com.example.foodapp.adapter.CategoryAdapter;
import com.example.foodapp.adapter.FoodPopularAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentHomeUserBinding;
import com.example.foodapp.model.Category;
import com.example.foodapp.model.Food;
import com.example.foodapp.utils.DepthPageTransformer;
import com.example.foodapp.model.Location;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeUserFragment extends BaseFragment implements FoodPopularAdapter.IFoodPopular {
    private FragmentHomeUserBinding binding;
    private List<Category> listCategory;
    private CategoryAdapter categoryAdapter;
    private List<Food> listFoodPopular;
    private FoodPopularAdapter foodAdapter;
    private static final String TAG = "HomeUserFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentHomeUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSpnLocation();
        initCartNumber();
        initBanner();
        initRcvCategory();
        initRcvListFoodPopular();
        initListener();

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

    private void initListener() {
        binding.txtSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), ListFoodPopularActivity.class));
        });
        
        binding.imgActionSearch.setOnClickListener(v -> {
            onClickActionSearch();
        });

        binding.imgCart.setOnClickListener(v -> startActivity(new Intent(requireActivity(), CartActivity.class)));

    }


    private void onClickActionSearch() {
        String strSearch = binding.edtSearch.getText().toString().trim();
        if(strSearch.isEmpty()){
            strSearch = binding.edtSearch.getHint().toString();
        }
        Intent intent = new Intent(requireActivity(), ListFoodSearchActivity.class);
        intent.putExtra("strSearch", strSearch);
        startActivity(intent);
    }

    private void initRcvListFoodPopular() {
        listFoodPopular = new ArrayList<>();
        binding.rcvFoodPopular.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        foodAdapter = new FoodPopularAdapter(requireActivity(), listFoodPopular, this);
        binding.rcvFoodPopular.setAdapter(foodAdapter);
        loadRcvListFoodPopular();

    }

    private void loadRcvListFoodPopular() {
        binding.progressBarFoodPopular.setVisibility(View.VISIBLE);
        database.getReference("Foods")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBarFoodPopular.setVisibility(View.GONE);
                        if(snapshot.exists()){
                            listFoodPopular.clear();
                            for(DataSnapshot item: snapshot.getChildren()){
                                Food food = item.getValue(Food.class);
                                if(food != null && food.isBestFood()){
                                    listFoodPopular.add(food);
                                }
                            }
                            foodAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarFoodPopular.setVisibility(View.GONE);
                        Log.d(TAG, "getListFoodCancelled: "+ error.getMessage());
                    }
                });
    }

    private void initRcvCategory() {
        listCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(requireActivity(), listCategory, new CategoryAdapter.IRcvCategory() {
            @Override
            public void onClick(Category category) {
                onClickItemCategory(category);
            }
        });
        binding.rcvCategory.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvCategory.setAdapter(categoryAdapter);
        loadRcvCategory();
    }

    private void loadRcvCategory(){
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        database.getReference("Category")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.progressBarCategory.setVisibility(View.GONE);
                        listCategory.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                Category category = item.getValue(Category.class);
                                if(category == null){
                                    return;
                                }
                                listCategory.add(category);
                            }
                            categoryAdapter.notifyDataSetChanged();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarFoodPopular.setVisibility(View.GONE);
                        Log.d(TAG, "getListCategoryCancelled: "+ error.getMessage());
                    }
                });

    }

    private void initBanner() {
        BannerUserAdapter adapter = new BannerUserAdapter(getListBanner());
        binding.viewPagerBanner.setAdapter(adapter);
        binding.circleIndicationBanner.setViewPager(binding.viewPagerBanner);
        DepthPageTransformer depthPageTransformer = new DepthPageTransformer();
        binding.viewPagerBanner.setPageTransformer(depthPageTransformer);
    }

    private List<Integer> getListBanner() {
        List<Integer> listBanner = new ArrayList<>();
        listBanner.add(R.drawable.banner_food4);
        listBanner.add(R.drawable.banner_food1);
        listBanner.add(R.drawable.banner_food3);
        listBanner.add(R.drawable.banner_food2);
        listBanner.add(R.drawable.banner_food8);
        listBanner.add(R.drawable.banner_food7);
        listBanner.add(R.drawable.banner_food5);
        listBanner.add(R.drawable.banner_food6);
        return listBanner;
    }

    private void initSpnLocation(){
        List<String> listLocation = new ArrayList<>();
        DatabaseReference mRef = database.getReference("Location");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLocation.clear();
                if(snapshot.exists()){
                    for(DataSnapshot item: snapshot.getChildren()){
                        Location location = item.getValue(Location.class);
                        if(location == null){
                            return;
                        }
                        String loc = location.getLoc();
                        listLocation.add(loc);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, listLocation);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spnLocation.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "getListLocationCancelled: "+ error.getMessage());
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

    private void onClickItemCategory(Category category) {
        Intent intent = new Intent(requireActivity(), ListFoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}