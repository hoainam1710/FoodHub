package com.example.foodapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvListAdminFoodManagerBinding;
import com.example.foodapp.model.Category;
import com.example.foodapp.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminListFoodManagerAdapter extends RecyclerView.Adapter<AdminListFoodManagerAdapter.MyViewHolder> {
    public interface IAdminListFoodManager{
        void viewDetailFood(Food food);
        void deleteFood(Food food);
        void updateFood(Food food);
    }
    private final List<Food> foods;
    private final IAdminListFoodManager iAdminListFoodManager;
    private static final String TAG = "AdminListFoodManagerAda";

    public AdminListFoodManagerAdapter(List<Food> foods,IAdminListFoodManager iAdminListFoodManager) {
        this.foods = foods;
        this.iAdminListFoodManager = iAdminListFoodManager;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListAdminFoodManagerBinding binding = ItemRcvListAdminFoodManagerBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Food food = foods.get(position);
        if (food == null) {
            return;
        }
        Glide.with(holder.itemView.getContext())
                .load(food.getImagePath())
                .into(holder.binding.imgFood);
        holder.binding.txtNameFood.setText(food.getTitle());
        int categoryId = food.getCategoryId();
        findCategoryById(categoryId, holder);

        holder.binding.layoutDetail.setOnClickListener(v -> iAdminListFoodManager.viewDetailFood(food));

        holder.binding.layoutDelete.setOnClickListener(v -> iAdminListFoodManager.deleteFood(food));

        holder.binding.layoutEdit.setOnClickListener(v -> iAdminListFoodManager.updateFood(food));
    }

    private void findCategoryById(int categoryId, MyViewHolder holder) {
        FirebaseDatabase.getInstance().getReference("Category")
                .child(String.valueOf(categoryId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Category category = snapshot.getValue(Category.class);
                            if (category != null) {
                                holder.binding.txtCategory.setText(category.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.binding.txtCategory.setText("");
                        Log.d(TAG, "getCategoryCancelled: " + error.getMessage());
                    }
                });

    }

    @Override
    public int getItemCount() {
        if (foods != null) {
            return foods.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemRcvListAdminFoodManagerBinding binding;

        public MyViewHolder(@NonNull ItemRcvListAdminFoodManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
