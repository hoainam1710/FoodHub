package com.example.foodapp.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListCompletedReviewBinding;
import com.example.foodapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCompletedReviewAdapter extends RecyclerView.Adapter<ListCompletedReviewAdapter.MyViewHolder> implements ListImgCompletedReviewAdapter.IListImgCompletedReview {
    @Override
    public void onClick(String imgPath, int position, int numberImage) {
        iListCompletedReview.onClickImage(imgPath, position, numberImage);
    }

    public interface IListCompletedReview{
        void onClickImage(String imgPath, int position, int numberImage);
    }
    private final List<Review> lists;
    private final Context context;
    private final IListCompletedReview iListCompletedReview;
    private static final String TAG = "ListCompletedReviewAdapter";

    public ListCompletedReviewAdapter(Context context, List<Review> lists, IListCompletedReview iListCompletedReview) {
        this.context = context;
        this.lists = lists;
        this.iListCompletedReview = iListCompletedReview;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListCompletedReviewBinding binding = ItemRcvListCompletedReviewBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Review review = lists.get(position);
        if(review == null){
            return;
        }
        holder.binding.txtDate.setText(review.getDate());
        holder.binding.txtDescription.setText(review.getContent());
        holder.binding.ratingStar.setRating(review.getRating());
        Glide.with(context)
                .load(review.getUser().getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(holder.binding.imgAvatar);
        Glide.with(context)
                .load(review.getFood().getImagePath())
                .into(holder.binding.imgFood);
        holder.binding.txtNameUser.setText(review.getUser().getName());
        holder.binding.txtNameFood.setText(review.getFood().getTitle());

        // rcv_list_img_completed_review
        List<String> listImgCompletedReview = new ArrayList<>();
        holder.binding.rcvListImgCompletedReview.setLayoutManager(new GridLayoutManager(context, 3));
        ListImgCompletedReviewAdapter adapter = new ListImgCompletedReviewAdapter(context, listImgCompletedReview, this);
        holder.binding.rcvListImgCompletedReview.setAdapter(adapter);
        loadData(review, listImgCompletedReview, adapter);

        // click
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void loadData(Review review, List<String> listImgCompletedReview, ListImgCompletedReviewAdapter adapter) {
        FirebaseDatabase.getInstance().getReference("Reviews").child(review.getId()).child("listImagePath")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listImgCompletedReview.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot item: snapshot.getChildren()){
                                String urlReview = item.getValue(String.class);
                                if(urlReview == null){
                                    continue;
                                }
                                listImgCompletedReview.add(urlReview);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "getListCompletedReviewInAdapterCancelled: "+ error.getMessage());
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvListCompletedReviewBinding binding;

        public MyViewHolder(@NonNull ItemRcvListCompletedReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
