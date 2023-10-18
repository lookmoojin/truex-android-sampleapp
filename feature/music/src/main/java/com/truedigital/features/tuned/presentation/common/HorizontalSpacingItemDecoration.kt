package com.truedigital.features.tuned.presentation.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R

class HorizontalSpacingItemDecoration(val spacing: Int? = null) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val padding =
            spacing ?: view.context.resources.getDimensionPixelSize(R.dimen.horizontal_list_spacing)
        outRect.left = padding

        // Put right spacing for last item in row
        if (parent.getChildLayoutPosition(view) == (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.right = padding
        }
    }
}
