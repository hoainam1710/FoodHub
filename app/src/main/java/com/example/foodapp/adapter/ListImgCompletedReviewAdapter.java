package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.databinding.ItemRcvListImgCompletedReviewBinding;

import java.util.List;

public class ListImgCompletedReviewAdapter extends RecyclerView.Adapter<ListImgCompletedReviewAdapter.MyViewHolder> {
    public interface IListImgCompletedReview{
        void onClick(String imgPath, int position, int numberImage);
    }

    private final List<String> lists;
    private final Context context;
    private final IListImgCompletedReview iListImgCompletedReview;

    public ListImgCompletedReviewAdapter(Context context, List<String> lists, IListImgCompletedReview iListImgCompletedReview) {
        this.context = context;
        this.lists = lists;
        this.iListImgCompletedReview = iListImgCompletedReview;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListImgCompletedReviewBinding binding = ItemRcvListImgCompletedReviewBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imageReview = lists.get(position);
        if(imageReview == null){
            return;
        }
        Glide.with(context).load(imageReview).into(holder.binding.imgCompletedReview);

        holder.binding.getRoot().setOnClickListener(v ->
                iListImgCompletedReview.onClick(imageReview, position, lists.size()));

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListImgCompletedReviewBinding binding;

        public MyViewHolder(@NonNull ItemRcvListImgCompletedReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
