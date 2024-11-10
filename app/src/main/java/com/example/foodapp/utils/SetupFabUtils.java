package com.example.foodapp.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetupFabUtils {
    // ẩn hiện fab khi scroll recyclerView
    public static void setUpFabWithRcv(RecyclerView rcv, FloatingActionButton fab) {
        rcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    fab.hide();
                } else if (dy < -5) {
                    fab.show();
                }

            }
        });

    }
}
