package com.example.foodapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListAdminReviewManagerBinding;
import com.example.foodapp.model.Review;

import java.util.List;

public class AdminReviewManagerAdapter extends RecyclerView.Adapter<AdminReviewManagerAdapter.MyViewHolder> {
    private final List<Review> reviews;

    public AdminReviewManagerAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListAdminReviewManagerBinding binding = ItemRcvListAdminReviewManagerBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Review review = reviews.get(position);
        if(review == null){
            return;
        }
        holder.binding.txtNameFood.setText(review.getFood().getTitle());
        holder.binding.txtDescription.setText(review.getContent());
        holder.binding.txtUserName.setText(review.getUser().getName());
        holder.binding.txtRating.setText(String.valueOf(review.getRating()));
        holder.binding.txtTimeStamp.setText(review.getDate());
        Glide.with(holder.itemView.getContext())
                .load(review.getUser().getAvatar())
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .into(holder.binding.imgAvatar);

        Glide.with(holder.itemView.getContext())
                .load(review.getFood().getImagePath())
                .into(holder.binding.imgFood);
    }

    @Override
    public int getItemCount() {
        if(reviews != null){
            return reviews.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListAdminReviewManagerBinding binding;

        public MyViewHolder(@NonNull ItemRcvListAdminReviewManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
