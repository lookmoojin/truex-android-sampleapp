package com.truedigital.features.truecloudv3.widget.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter.Companion.FILE_GRID_ITEM_TYPE
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter.Companion.FOLDER_GRID_ITEM_TYPE
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter.Companion.UPLOAD_GRID_ITEM_TYPE

class FilesDecoration(
    context: Context?,
    @DimenRes resId: Int,
    uploadItemSize: Int = 0,
    folderItemSize: Int = 0
) : RecyclerView.ItemDecoration() {

    companion object {
        private const val NO_MARGIN = 0
        private const val NO_ITEM = 0
        private const val ZERO = 0
        private const val HEADER_SIZE = 1
        private const val DEFAULT_COLUMNS = 2
        private const val DOUBLE = 4
    }

    private val margin: Int
    private val uploadItemSize: Int
    private val folderItemSize: Int
    private val doubleMargin: Int

    init {
        val resources = context?.resources
        this.margin = resources?.getDimensionPixelSize(resId) ?: NO_MARGIN
        this.uploadItemSize = uploadItemSize
        this.folderItemSize = folderItemSize
        this.doubleMargin = margin * DOUBLE
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (RecyclerView.NO_POSITION == itemPosition) return

        when (parent.adapter?.getItemViewType(itemPosition)) {
            UPLOAD_GRID_ITEM_TYPE -> {
                if ((itemPosition % DEFAULT_COLUMNS) == ZERO) {
                    outRect.set(margin, margin, doubleMargin, ZERO)
                } else {
                    outRect.set(doubleMargin, margin, margin, ZERO)
                }
            }
            FOLDER_GRID_ITEM_TYPE -> {
                outRect.set(getFolderGridLeftMargin(itemPosition), margin, margin, margin)
            }
            FILE_GRID_ITEM_TYPE -> {
                val leftMargin = getFileGridLeftMargin(itemPosition)
                outRect.set(
                    leftMargin,
                    margin,
                    if (leftMargin == margin) doubleMargin else margin,
                    margin
                )
            }
        }
    }

    @VisibleForTesting
    fun checkItemSize(rect: Rect, itemSize: Int, itemPosition: Int) {
        if ((itemSize % DEFAULT_COLUMNS) == ZERO) {
            when {
                (itemPosition % DEFAULT_COLUMNS) == ZERO -> rect.set(
                    margin,
                    margin,
                    doubleMargin,
                    margin
                )
                else -> rect.set(doubleMargin, margin, margin, margin)
            }
        } else {
            when {
                (itemPosition % DEFAULT_COLUMNS) == ZERO -> rect.set(
                    doubleMargin,
                    margin,
                    margin,
                    margin
                )
                else -> rect.set(
                    margin,
                    margin,
                    doubleMargin,
                    margin
                )
            }
        }
    }

    private fun getItemPosition(itemPosition: Int): Int {
        var position = itemPosition
        when {
            uploadItemSize == NO_ITEM -> itemPosition
            (uploadItemSize % DEFAULT_COLUMNS) == ZERO -> {
                if ((itemPosition % DEFAULT_COLUMNS) == ZERO) {
                    position = itemPosition + HEADER_SIZE
                } else {
                    itemPosition
                }
            }
            else -> {
                if ((itemPosition % DEFAULT_COLUMNS) == ZERO) {
                    itemPosition
                } else {
                    position = itemPosition + HEADER_SIZE
                }
            }
        }
        return position
    }

    private fun getFolderGridLeftMargin(itemPosition: Int): Int {
        return if (uploadItemSize != 0) {
            val uploadIsEven = checkIsEven(uploadItemSize)
            val positionIsEven = checkIsEven(itemPosition)
            val odd = uploadIsEven && positionIsEven
            val even = !uploadIsEven && !positionIsEven
            if (odd || even) margin else doubleMargin
        } else {
            val mod = getItemPosition(itemPosition) % DEFAULT_COLUMNS
            if (mod == ZERO) doubleMargin else margin
        }
    }

    private fun getFileGridLeftMargin(itemPosition: Int): Int {
        val isEvenItemIndex = itemPosition % DEFAULT_COLUMNS == ZERO
        val isEvenFolderSize = folderItemSize != 0 && checkIsEven(folderItemSize)
        val isSameType = isEvenItemIndex == isEvenFolderSize
        return if (uploadItemSize != 0) {
            if (checkIsEven(uploadItemSize)) {
                if (isSameType) doubleMargin else margin
            } else {
                if (isSameType) margin else doubleMargin
            }
        } else {
            if (isSameType) margin else doubleMargin
        }
    }

    private fun checkIsEven(number: Int): Boolean {
        return number % DEFAULT_COLUMNS == 0
    }
}
