package com.truedigital.component.view.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final boolean isAddToLastItem;
    private final int horizontalSpaceHeight;

    public HorizontalSpacingItemDecoration(int horizontalSpaceHeight, boolean isAddToLastItem) {
        this.horizontalSpaceHeight = horizontalSpaceHeight;
        this.isAddToLastItem = isAddToLastItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isAddToLastItem || (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)) {
            outRect.right = horizontalSpaceHeight;
        }
    }
}