package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.databinding.ItemRcvVoucherInactiveBinding;
import com.example.foodapp.model.Voucher;

import java.util.List;

public class InactiveVoucherAdapter extends RecyclerView.Adapter<InactiveVoucherAdapter.MyViewHolder> {
    private final List<Voucher> lists;

    public InactiveVoucherAdapter(List<Voucher> lists) {
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvVoucherInactiveBinding binding = ItemRcvVoucherInactiveBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Voucher voucher = lists.get(position);
        if(voucher == null){
            return;
        }
        holder.binding.txtTitle.setText(voucher.getTitle());
        holder.binding.txtCode.setText("Mã: "+ voucher.getCode());
        String date = voucher.getStartDate() +" - "+ voucher.getEndDate();
        holder.binding.txtDate.setText(date);
        String strDiscountAndMinAmount = "Giảm "+ voucher.getDiscount()*100+"%"+", đơn tối thiếu "
                + voucher.getMinOderAmount()+"$";
        holder.binding.txtDiscountAndMinAmount.setText(strDiscountAndMinAmount);
    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcvVoucherInactiveBinding binding;

        public MyViewHolder(@NonNull ItemRcvVoucherInactiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
