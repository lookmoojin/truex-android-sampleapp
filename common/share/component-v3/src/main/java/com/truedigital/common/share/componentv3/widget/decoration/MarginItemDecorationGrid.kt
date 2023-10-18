package com.truedigital.common.share.componentv3.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecorationGrid(private val spanCount: Int, private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val index = parent.getChildLayoutPosition(view)
        val isLeft = (index % spanCount == 0)
        outRect.set(
            if (isLeft) spaceSize else spaceSize / 2,
            spaceSize,
            if (isLeft) spaceSize / 2 else spaceSize,
            spaceSize
        )
    }
}
