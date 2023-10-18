package com.truedigital.features.tuned.presentation.bottomsheet.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.databinding.ItemBottomSheetOptionBinding
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.foundation.extension.onClick

class BottomSheetOptionsAdapter(
    val useDarkTheme: Boolean = false,
    val onClickListener: (item: PickerOptions) -> Unit
) : RecyclerView.Adapter<BottomSheetOptionsAdapter.OptionViewHolder>() {

    var items: List<PickerOptions> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view =
            ItemBottomSheetOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = items[position]

        with(holder) {
            option.resContentDescription?.let { contentDescription ->
                holder.itemView.contentDescription =
                    binding.root.resources.getString(contentDescription)
            }
            option.resIcon?.let {
                binding.ivIcon.setImageResource(it)
                if (useDarkTheme) ImageViewCompat.setImageTintList(
                    binding.ivIcon,
                    ColorStateList.valueOf(Color.WHITE)
                )
            } ?: run {
                ImageViewCompat.setImageTintList(binding.ivIcon, null)
            }

            binding.tvLabel.setText(option.resLabel)

            if (useDarkTheme) binding.tvLabel.setTextColor(Color.WHITE)
        }

        when (option) {
            PickerOptions.ADD_TO_COLLECTION_DISABLED,
            PickerOptions.FOLLOW_DISABLED,
            PickerOptions.CLEAR_VOTE_DISABLED -> {
                holder.itemView.alpha = FLOAT_0_5F
                holder.itemView.isClickable = false
            }

            else -> {
                holder.itemView.alpha = 1f
                holder.itemView.isClickable = true
            }
        }

        // hide CLEAR_VOTE_DISABLED as requested
        holder.itemView.visibility =
            if (option == PickerOptions.CLEAR_VOTE_DISABLED) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount() = items.size

    inner class OptionViewHolder(
        val binding: ItemBottomSheetOptionBinding,
        onItemClickListener: (item: PickerOptions) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.onClick {
                val option = items[layoutPosition]
                if (option != PickerOptions.ADD_TO_COLLECTION_DISABLED &&
                    option != PickerOptions.FOLLOW_DISABLED &&
                    option != PickerOptions.CLEAR_VOTE_DISABLED
                )
                    onItemClickListener(items[layoutPosition])
            }
        }
    }
}
