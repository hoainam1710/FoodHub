package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.foodapp.databinding.ItemRcvListSubmitReviewBinding;
import com.example.foodapp.utils.DataItemReview;
import com.example.foodapp.model.Cart;
import com.example.foodapp.model.Food;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSubmitReviewAdapter extends RecyclerView.Adapter<ListSubmitReviewAdapter.MyViewHolder> {
    public interface IListSubmitReview{
        void addImageReview(Food food, int position);
        void submitReview(ItemRcvListSubmitReviewBinding itemRcvListSubmitReviewBinding, Cart cart, int position);
    }
    private final List<Cart> lists;
    private List<Uri> listUri;
    private final Context context;
    private final IListSubmitReview iListSubmitReview;
    private final Map<Integer, DataItemReview> mapDataFormReview;

    public ListSubmitReviewAdapter(Context context, List<Cart> lists, IListSubmitReview iListSubmitReview) {
        this.context = context;
        this.lists = lists;
        this.iListSubmitReview =  iListSubmitReview;
        mapDataFormReview = new HashMap<>();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setListUri(List<Uri> listUri, int position){
        this.listUri = listUri;
        notifyItemChanged(position);
    }

    public void clearMapDataReview(int position){
        mapDataFormReview.clear();
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListSubmitReviewBinding binding = ItemRcvListSubmitReviewBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.ratingStar.setOnRatingBarChangeListener(null);
        holder.binding.edtContent.removeTextChangedListener(holder.textWatcher);

        DataItemReview dataItemReview = mapDataFormReview.getOrDefault(position, new DataItemReview("", 0));
        if(dataItemReview == null){
            return;
        }
        holder.binding.ratingStar.setRating(dataItemReview.getRating());
        holder.binding.edtContent.setText(dataItemReview.getDataEdt());

        holder.binding.ratingStar.setOnRatingBarChangeListener(null);

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataItemReview.setDataEdt(s.toString());
                mapDataFormReview.put(position, dataItemReview);
            }
        };

        holder.binding.edtContent.addTextChangedListener(holder.textWatcher);


        // bind data
        Cart cart = lists.get(position);
        if(cart == null){
            return;
        }

        Glide.with(context).load(cart.getFood().getImagePath()).into(holder.binding.imgFood);
        holder.binding.txtName.setText(cart.getFood().getTitle());
        holder.binding.txtQuantity.setText(cart.getQuantity()+" sản phẩm");

        // rcv list image review
        holder.binding.rcvImageReview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ListImageReviewAdapter listImageReviewAdapter = new ListImageReviewAdapter(context, listUri);
        holder.binding.rcvImageReview.setAdapter(listImageReviewAdapter);

        // event
        holder.binding.ratingStar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(fromUser){
                dataItemReview.setRating((int) rating);
                mapDataFormReview.put(position, dataItemReview);

                switch ((int) rating){
                    case 1:
                        holder.binding.txtStatus.setVisibility(View.VISIBLE);
                        holder.binding.txtStatus.setText("Tệ");
                        break;
                    case 2:
                        holder.binding.txtStatus.setVisibility(View.VISIBLE);
                        holder.binding.txtStatus.setText("Không hài lòng");
                        break;
                    case 3:
                        holder.binding.txtStatus.setVisibility(View.VISIBLE);
                        holder.binding.txtStatus.setText("Bình thường");
                        break;
                    case 4:
                        holder.binding.txtStatus.setVisibility(View.VISIBLE);
                        holder.binding.txtStatus.setText("Hài lòng");
                        break;
                    case 5:
                        holder.binding.txtStatus.setVisibility(View.VISIBLE);
                        holder.binding.txtStatus.setText("Tuyệt vời");

                }
            }
        });

        holder.binding.addImage.setOnClickListener(v -> iListSubmitReview.addImageReview(cart.getFood(), position));

        holder.binding.btnSubmit.setOnClickListener(v ->
                iListSubmitReview.submitReview(holder.binding, cart, position));

    }


    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public final ItemRcvListSubmitReviewBinding binding;
        private TextWatcher textWatcher;    // gắn textWatcher vào holder để xóa khi cần

        public MyViewHolder(@NonNull ItemRcvListSubmitReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
