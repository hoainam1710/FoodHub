package com.example.foodapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.databinding.ItemRcvShippingAddressBinding;
import com.example.foodapp.model.ShippingAddress;

import java.util.List;

public class ShippingAddressAdapter extends RecyclerView.Adapter<ShippingAddressAdapter.MyViewHolder> {
    public interface IShippingAddress{
        void onClickCheckbox(ShippingAddress shippingAddress, boolean isChecked);
        void updateAddress(ShippingAddress shippingAddress);
        void deleteAddress(ShippingAddress shippingAddress);
    }
    private List<ShippingAddress> lists;
    private IShippingAddress iShippingAddress;

    public ShippingAddressAdapter(List<ShippingAddress> lists, IShippingAddress iShippingAddress) {
        this.lists = lists;
        this.iShippingAddress = iShippingAddress;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRcvShippingAddressBinding binding = ItemRcvShippingAddressBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ShippingAddress shippingAddress = lists.get(position);
        if(shippingAddress == null){
            return;
        }

        holder.binding.cboUseAddress.setChecked(shippingAddress.isUse());
        holder.binding.txtNameUser.setText(shippingAddress.getName());
        holder.binding.txtPhoneNumber.setText(shippingAddress.getPhoneNumber());
        String fullAddress = shippingAddress.getSoNha() + " Phường "+ shippingAddress.getPhuong()
                + ", Quận "+ shippingAddress.getQuan() + ", "+ shippingAddress.getThanhPho();
        holder.binding.txtAddress.setText(fullAddress);

        holder.binding.imgDelete.setOnClickListener(v -> iShippingAddress.deleteAddress(shippingAddress));
        holder.binding.imgEdit.setOnClickListener(v -> iShippingAddress.updateAddress(shippingAddress));
        holder.binding.cboUseAddress.setOnCheckedChangeListener(
                (buttonView, isChecked) -> iShippingAddress.onClickCheckbox(shippingAddress, isChecked));

    }

    @Override
    public int getItemCount() {
        if(lists != null){
            return lists.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemRcvShippingAddressBinding binding;

        public MyViewHolder(@NonNull ItemRcvShippingAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
