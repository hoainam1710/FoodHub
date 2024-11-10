package com.example.foodapp.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.databinding.ItemRcvVoucherBinding;
import com.example.foodapp.fragments.ActiveVoucherFragment;
import com.example.foodapp.model.Voucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.MyViewHolder> {
    public interface IVoucher{
        void onClick(Voucher voucher, boolean checked);
    }
    private final List<Voucher> lists;
    private final IVoucher iVoucher;
    private final Context context;
    private int selectedPosition = -1;


    public VoucherAdapter(Context context, List<Voucher> lists, IVoucher iVoucher) {
        this.context = context;
        this.lists = lists;
        this.iVoucher = iVoucher;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater  inflater = LayoutInflater.from(parent.getContext());
        ItemRcvVoucherBinding binding = ItemRcvVoucherBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

        //checkbox
//        SharedPreferences sharedPreferences = context.getSharedPreferences(ActiveVoucherFragment.PREF_NAME, Context.MODE_PRIVATE);
//        int idVoucherChecked = sharedPreferences.getInt("idVoucher", -1);
//        if(voucher.getId() == idVoucherChecked){
//            selectedPosition = position;
//
//        }

        holder.binding.cboChecked.setChecked(position == selectedPosition);

        // event
        holder.binding.cboChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedPosition = position;

                } else {
                    if(selectedPosition == position) {
                        selectedPosition = -1;
                    } else selectedPosition = position;
                }
                iVoucher.onClick(voucher, isChecked);

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
        private final ItemRcvVoucherBinding binding;

        public MyViewHolder(@NonNull ItemRcvVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
