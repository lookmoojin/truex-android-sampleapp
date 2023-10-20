package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderItemListBinding
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class FilesListViewHolder(
    private val binding: TrueCloudv3ViewholderItemListBinding,
    private val onActionClick: OnListViewFileActionClickListener,
    private val onItemSelected: FilesAdapter.OnItemSelect?,
    private val selectMode: Boolean = false,
    private val locatorMode: Boolean = false,
    private val isTrashMode: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    interface OnListViewFileActionClickListener {
        fun onFileClicked(model: TrueCloudFilesModel.File)
        fun onMoreClicked(model: TrueCloudFilesModel.File)
    }

    companion object {
        private const val CACHE_PATH = "/true_cloud_cache/"
        private const val SUFFIX_PATH = ".jpg"
        private const val COMMA = ", "
        private const val ALPHA_07 = 0.7f
        private const val ALPHA_1 = 1f
    }

    fun bind(file: TrueCloudFilesModel.File) = with(binding) {
        val mimeType = file.fileMimeType
        val time = file.createdAt
        val sizeFormat = file.size
        val key = file.coverImageKey
        val deleteIn = file.deleteIn
        if (file.imageCoverAlready) {
            setCoverImage(key, mimeType, file.updatedAt)
        } else {
            trueCloudShapeAbleItemImageView.setImageResource(mimeType.res)
        }
        trueCloudNameTextView.text = file.name
        trueCloudDetailTextView.text = "$time$COMMA$sizeFormat"
        trueCloudMoreImageView.onClick {
            onActionClick.onMoreClicked(file)
        }
        if (file.isPrivate != null) {
            trueCloudIsPrivateImageView.visible()
            if (file.isPrivate == true) trueCloudIsPrivateImageView.setImageResource(R.drawable.ic_lock_19)
            else trueCloudIsPrivateImageView.setImageResource(R.drawable.ic_glob_19)
        } else {
            trueCloudIsPrivateImageView.gone()
        }
        root.onClick {
            if (selectMode) {
                if (onItemSelected?.getSelectedList()?.contains(file) == true) {
                    onItemSelected.onDeselectItem(file)
                    trueCloudSelectImageView.isSelected = false
                } else {
                    onItemSelected?.onSelectItem(file)
                    trueCloudSelectImageView.isSelected = true
                }
            } else {
                onActionClick.onFileClicked(file)
            }
        }
        when {
            selectMode -> {
                trueCloudSelectImageView.visible()
                trueCloudMoreImageView.invisible()
                trueCloudSelectImageView.isSelected =
                    onItemSelected?.getSelectedList()?.contains(file) == true
            }

            locatorMode -> {
                trueCloudMoreImageView.gone()
                trueCloudSelectImageView.gone()
                trueCloudOverlayView.visible()
                root.alpha = ALPHA_07
                root.isClickable = false
            }

            isTrashMode -> {
                trueCloudDetailTextView.text = deleteIn
            }

            else -> {
                trueCloudSelectImageView.gone()
                trueCloudMoreImageView.visible()
                trueCloudOverlayView.gone()
                root.alpha = ALPHA_1
                root.isClickable = true
            }
        }
    }

    private fun setCoverImage(key: String?, mimeType: FileMimeType, updatedAt: String?) {
        val cachePath = binding.root.context.cacheDir.absolutePath + "$CACHE_PATH$key$SUFFIX_PATH"
        Glide.with(binding.root.context)
            .load(cachePath)
            .centerCrop()
            .priority(Priority.NORMAL)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
            .signature(ObjectKey(updatedAt ?: ""))
            .placeholder(mimeType.res)
            .error(mimeType.res)
            .into(binding.trueCloudShapeAbleItemImageView)
            .waitForLayout()
    }
}
