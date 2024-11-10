package com.example.foodapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemBannerUserBinding;

import java.util.List;


public class BannerUserAdapter extends RecyclerView.Adapter<BannerUserAdapter.BannerViewHolder> {
    private List<Integer> listBanner;

    public BannerUserAdapter(List<Integer> listBanner) {
        this.listBanner = listBanner;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBannerUserBinding binding = ItemBannerUserBinding.inflate(inflater, parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        int banner = listBanner.get(position);
        holder.binding.imgBanner.setImageResource(banner);

    }

    @Override
    public int getItemCount() {
        if(listBanner != null){
            return listBanner.size();
        }
        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder{
        private ItemBannerUserBinding binding;
        public BannerViewHolder(@NonNull ItemBannerUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
