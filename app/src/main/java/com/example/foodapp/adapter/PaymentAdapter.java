package com.example.foodapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.databinding.ItemRcvListPaymentBinding;
import com.example.foodapp.model.Payment;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    public interface IPayment{
        void onClickCheckbox(Payment payment, boolean isChecked);
    }
    private List<Payment> lists;
    private Context context;
    private IPayment iPayment;

    public PaymentAdapter(Context context, List<Payment> lists, IPayment iPayment) {
        this.context = context;
        this.lists = lists;
        this.iPayment = iPayment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvListPaymentBinding binding = ItemRcvListPaymentBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Payment payment = lists.get(position);
        if(payment == null){
            return;
        }

        if(!payment.isUse()){
            holder.binding.layoutCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey_btn_enable));
            holder.binding.txtDate.setTextColor(ContextCompat.getColor(context, R.color.enable2));
            holder.binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.enable2));
            holder.binding.txtCardNumber.setTextColor(ContextCompat.getColor(context, R.color.enable2));
        } else {
            holder.binding.layoutCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.payment_dark));
            holder.binding.txtDate.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.txtCardNumber.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        String lastCardNumber = payment.getCardNumber().substring(12);

        holder.binding.txtCardNumber.setText("**** **** **** "+ lastCardNumber);
        holder.binding.txtName.setText(payment.getName());
        holder.binding.txtDate.setText(payment.getDate());
        holder.binding.cboUseDefault.setChecked(payment.isUse());

        holder.binding.cboUseDefault.setOnCheckedChangeListener((buttonView, isChecked) ->
                iPayment.onClickCheckbox(payment, isChecked));

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemRcvListPaymentBinding binding;

        public MyViewHolder(@NonNull ItemRcvListPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
