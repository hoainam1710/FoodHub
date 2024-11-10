package com.example.foodapp.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemViewpagerImageReviewDetailBinding;
import java.util.List;

public class ViewPagerImageReviewDetailAdapter extends RecyclerView.Adapter<ViewPagerImageReviewDetailAdapter.MyViewHolder> {
    private final List<String> lists;
    private final Context context;

    public ViewPagerImageReviewDetailAdapter(Context context, List<String> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewpagerImageReviewDetailBinding binding = ItemViewpagerImageReviewDetailBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String imgPath = lists.get(position);
        if(imgPath == null){
            return;
        }
        Glide.with(context).load(imgPath).into(holder.binding.imgReview);

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemViewpagerImageReviewDetailBinding binding;

        public MyViewHolder(@NonNull ItemViewpagerImageReviewDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
