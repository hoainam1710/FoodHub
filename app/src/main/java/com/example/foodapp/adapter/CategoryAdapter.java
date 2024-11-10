package com.example.foodapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvCatetoryBinding;
import com.example.foodapp.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public interface IRcvCategory{
        void onClick(Category category);
    }
    private List<Category> lists;
    private Context context;
    private IRcvCategory iRcvCategory;

    public CategoryAdapter(Context context, List<Category> lists, IRcvCategory iRcvCategory) {
        this.context = context;
        this.lists = lists;
        this.iRcvCategory = iRcvCategory;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvCatetoryBinding binding = ItemRcvCatetoryBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = lists.get(position);
        if(category == null){
            return;
        }
        holder.binding.txtNameCategory.setText(category.getName());
        setBackgroundItem(holder, position);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iRcvCategory.onClick(category);
            }
        });

    }


    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        private ItemRcvCatetoryBinding binding;
        public CategoryViewHolder(@NonNull ItemRcvCatetoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    private void setBackgroundItem(CategoryViewHolder holder, int position){
        switch (position){
            case 0:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_1);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_5_bg));
                break;
            case 1:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_2);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_2_bg));
                break;
            case 2:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_3);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_3_bg));
                break;
            case 3:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_4);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_4_bg));
                break;
            case 4:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_5);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_1_bg));
                break;
            case 5:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_6);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_6_bg));
                break;
            case 6:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_7);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_7_bg));
                break;
            default:
                holder.binding.imgCategory.setImageResource(R.drawable.btn_8);
                holder.binding.layoutCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_8_bg));

        }
    }
}
