package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderItemGridBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class FilesGridViewHolder(
    private val binding: TrueCloudv3ViewholderItemGridBinding,
    private val onActionClick: OnGridViewFileActionClickListener,
    private val onItemSelected: FilesAdapter.OnItemSelect?,
    private val selectMode: Boolean = false,
    private val isTrashMode: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    interface OnGridViewFileActionClickListener {
        fun onFileClicked(model: TrueCloudFilesModel.File)
        fun onLongClicked(model: TrueCloudFilesModel.File)
    }

    companion object {
        private const val CACHE_PATH = "/true_cloud_cache/"
        private const val SUFFIX_PATH = ".jpg"
        private const val DEFAULT_IMAGE_RESOURCE = 0
    }

    fun bind(file: TrueCloudFilesModel.File) = with(binding) {
        val mimeType = file.fileMimeType
        trueCloudShapeAbleItemImageView.setImageResource(DEFAULT_IMAGE_RESOURCE)
        trueCloudItemImageView.setImageResource(mimeType.resGrid)
        when (mimeType) {
            FileMimeType.IMAGE, FileMimeType.VIDEO -> {
                if (file.imageCoverAlready) {
                    setCoverImage(file.coverImageKey, file.updatedAt)
                }
                trueCloudNameTextView.gone()
                trueCloudDetailTextView.gone()
            }
            else -> {
                trueCloudNameTextView.visible()
                trueCloudDetailTextView.visible()
                trueCloudNameTextView.text = file.name
                trueCloudDetailTextView.text = file.createdAt
            }
        }

        root.onClick {
            if (selectMode) {
                if (onItemSelected?.getSelectedList()?.contains(file) == true) {
                    onItemSelected.onDeselectItem(file)
                    trueCloudGridSelectImageView.isSelected = false
                } else {
                    onItemSelected?.onSelectItem(file)
                    trueCloudGridSelectImageView.isSelected = true
                }
            } else {
                onActionClick.onFileClicked(file)
            }
        }

        root.setOnLongClickListener {
            if (!selectMode) {
                onActionClick.onLongClicked(file)
            }
            false
        }

        when {
            selectMode -> {
                trueCloudGridSelectImageView.visible()
                trueCloudGridSelectImageView.isSelected =
                    onItemSelected?.getSelectedList()?.contains(file) == true
            }
            isTrashMode -> {
                trueCloudGridSelectImageView.gone()
                trueCloudDetailTextView.text = file.deleteIn
            }
            else -> {
                trueCloudGridSelectImageView.gone()
            }
        }
    }

    private fun setCoverImage(key: String?, updatedAt: String?) = with(binding) {
        val cachePath = root.context.cacheDir.absolutePath + "$CACHE_PATH$key$SUFFIX_PATH"
        Glide.with(root.context)
            .load(cachePath)
            .dontAnimate()
            .priority(Priority.NORMAL)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
            .signature(ObjectKey(updatedAt ?: ""))
            .into(trueCloudShapeAbleItemImageView)
            .waitForLayout()
    }
}
