package com.example.foodapp.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.adapter.ListCustomerAdapter;
import com.example.foodapp.base.BaseFragment;
import com.example.foodapp.databinding.FragmentCustomerManagerBinding;
import com.example.foodapp.model.User;
import com.example.foodapp.utils.SetupFabUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerManagerFragment extends BaseFragment implements ListCustomerAdapter.IListCustomer {
    private FragmentCustomerManagerBinding binding;
    private List<User> users;
    private ListCustomerAdapter adapter;
    private static final String TAG = "CustomerManagerFragment";
    private BottomSheetNotifySuccessFragment bottomSheetNotifySuccessFragment;
    private ProgressDialogFragment progressDialogFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetupFabUtils.setUpFabWithRcv(binding.rcvListCustomer, binding.fabAdd);
        initRcv();
        initListener();

    }

    private void initListener() {
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCustomer(Objects.requireNonNull(binding.edtSearch.getText()).toString().trim());
                return true;
            }
            return false;
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCustomer(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // wait...
            }
        });

    }

    private void initRcv() {
        users = new ArrayList<>();
        binding.rcvListCustomer.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new ListCustomerAdapter(users, this);
        binding.rcvListCustomer.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutNotifyEmpty.setVisibility(View.GONE);
        DatabaseReference ref = database.getReference("Account");
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot account : snapshot.getChildren()) {
                        User user = account.child("profile").getValue(User.class);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    if (users.isEmpty()) {
                        binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                    } else binding.layoutNotifyEmpty.setVisibility(View.GONE);
                } else binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.layoutNotifyEmpty.setVisibility(View.GONE);
                Log.d(TAG, "getListCustomerCancelled: " + error.getMessage());
                Toast.makeText(requireActivity(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchCustomer(String keySearch) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutNotifyEmpty.setVisibility(View.GONE);
        DatabaseReference ref = database.getReference("Account");
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot account : snapshot.getChildren()) {
                        User user = account.child("profile").getValue(User.class);
                        if (user != null) {
                            if (user.getEmail().toLowerCase().contains(keySearch.toLowerCase())) {
                                users.add(user);
                            }
                        }
                    }
                    if (users.isEmpty()) {
                        binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                    } else binding.layoutNotifyEmpty.setVisibility(View.GONE);
                } else binding.layoutNotifyEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.layoutNotifyEmpty.setVisibility(View.GONE);
                Log.d(TAG, "getListCustomerCancelled: " + error.getMessage());
                Toast.makeText(requireActivity(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showBottomSheetDialog(User user) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_customer_manager);

        ImageView imgAvatar = bottomSheetDialog.findViewById(R.id.img_avatar);
        TextView txtName = bottomSheetDialog.findViewById(R.id.txt_name);
        TextView txtEmail = bottomSheetDialog.findViewById(R.id.txt_email);
        RelativeLayout layoutViewDetailCustomer = bottomSheetDialog.findViewById(R.id.layout_view_detail_customer);
        RelativeLayout layoutDeleteCustomer = bottomSheetDialog.findViewById(R.id.layout_delete_customer);


        if (imgAvatar != null) {
            Glide.with(requireActivity())
                    .load(user.getAvatar())
                    .error(R.drawable.avatar_default)
                    .placeholder(R.drawable.avatar_default)
                    .into(imgAvatar);
        }

        if (txtEmail != null) {
            txtEmail.setText(user.getEmail());
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            String id = user.getId();
            String subId = id.substring(id.length() - 6);
            if (txtName != null) {
                txtName.setText("User_" + subId);
            }
        } else {
            if (txtName != null) {
                txtName.setText(user.getName());
            }
        }

        //event
        if (layoutViewDetailCustomer != null) {
            layoutViewDetailCustomer.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                showDialogProfile(user);
            });
        }
        if (layoutDeleteCustomer != null) {
            layoutDeleteCustomer.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                showDeleteDialog(user);
            });
        }

        bottomSheetDialog.show();
    }

    private void showDeleteDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Cảnh báo!")
                .setIcon(R.drawable.ic_warning)
                .setCancelable(true)
                .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    showProgressDialogFragment();
                    database.getReference("Account")
                            .child(user.getId())
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                progressDialogFragment.dismiss();
                                if (task.isSuccessful()) {
                                    showBottomSheetNotifySuccessFragment();
                                } else
                                    Toast.makeText(requireActivity(), "Xóa người dùng thất bại!", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create().show();

    }

    private void showBottomSheetNotifySuccessFragment() {
        bottomSheetNotifySuccessFragment = (BottomSheetNotifySuccessFragment) requireActivity()
                .getSupportFragmentManager().findFragmentByTag("BottomSheetNotifySuccessFragment");
        if (bottomSheetNotifySuccessFragment == null) {
            bottomSheetNotifySuccessFragment = new BottomSheetNotifySuccessFragment("Xóa ngưởi dùng thành công!");
        }
        bottomSheetNotifySuccessFragment.show(requireActivity().getSupportFragmentManager(), "BottomSheetNotifySuccessFragment");
        new Handler().postDelayed(() -> bottomSheetNotifySuccessFragment.dismiss(), 3000);
    }

    private void showProgressDialogFragment() {
        progressDialogFragment = (ProgressDialogFragment) requireActivity()
                .getSupportFragmentManager().findFragmentByTag("ProgressDialogFragment");
        if (progressDialogFragment == null) {
            progressDialogFragment = new ProgressDialogFragment();
        }
        progressDialogFragment.show(requireActivity().getSupportFragmentManager(), "ProgressDialogFragment");
    }

    @SuppressLint("SetTextI18n")
    private void showDialogProfile(User user) {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_view_detail_customer);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams params = window.getAttributes();
        if (params == null) {
            return;
        }
        params.dimAmount = 0.7f;
        window.setAttributes(params);

        ImageView imgAvatar = dialog.findViewById(R.id.img_avatar);
        ImageView imgBack = dialog.findViewById(R.id.img_back);
        TextView txtEmail = dialog.findViewById(R.id.txt_email);
        TextView txtName = dialog.findViewById(R.id.txt_name);
        TextView txtPhoneNumber = dialog.findViewById(R.id.txt_phone_number);
        TextView txtSeX = dialog.findViewById(R.id.txt_sex);

        imgBack.setOnClickListener(v -> dialog.dismiss());

        Glide.with(requireActivity())
                .load(user.getAvatar())
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .into(imgAvatar);

        if (txtEmail != null) {
            txtEmail.setText(user.getEmail());
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            String id = user.getId();
            String subId = id.substring(id.length() - 6);
            if (txtName != null) {
                txtName.setText("User_" + subId);
            }
        } else {
            if (txtName != null) {
                txtName.setText(user.getName());
            }
        }
        if (user.getSex() == null) {
            txtSeX.setText("Chưa xác định");
        } else txtSeX.setText(user.getSex());

        if (user.getPhoneNumber() != null) {
            txtPhoneNumber.setText(user.getPhoneNumber());
        } else txtPhoneNumber.setText("Chưa xác định");

        dialog.show();

    }
}