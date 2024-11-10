package com.example.foodapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvListImageReviewBinding;

import java.util.List;

public class ListImageReviewAdapter extends RecyclerView.Adapter<ListImageReviewAdapter.MyViewHolder> {
    private final List<Uri> lists;
    private final Context context;

    public ListImageReviewAdapter(Context context, List<Uri> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListImageReviewBinding binding = ItemRcvListImageReviewBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Uri imageReview = lists.get(position);
        if(imageReview == null){
            return;
        }
        Glide.with(context).load(imageReview).into(holder.binding.imgReview);

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListImageReviewBinding binding;

        public MyViewHolder(@NonNull ItemRcvListImageReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
