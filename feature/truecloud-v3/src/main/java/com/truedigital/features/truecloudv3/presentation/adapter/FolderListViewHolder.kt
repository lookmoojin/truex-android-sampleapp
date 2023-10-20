package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderFolderItemListBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class FolderListViewHolder(
    private val binding: TrueCloudv3ViewholderFolderItemListBinding,
    private val onFolderClicked: OnFolderClickListener,
    private val onItemSelected: FilesAdapter.OnItemSelect?,
    private val selectMode: Boolean = false,
    private val locatorMode: Boolean = false,
    private val isTrashMode: Boolean = false,
    private val folderListId: ArrayList<String>? = null
) : RecyclerView.ViewHolder(binding.root) {

    interface OnFolderClickListener {
        fun onClicked(model: TrueCloudFilesModel.Folder)
        fun onMoreClicked(model: TrueCloudFilesModel.Folder)
    }

    companion object {
        private const val ALPHA_07 = 0.7f
        private const val ALPHA_1 = 1f
    }

    fun bind(folder: TrueCloudFilesModel.Folder) = with(binding) {
        folder.let {
            val createDate = it.createdAt
            val folderName = it.name
            val deleteIn = it.deleteIn
            trueCloudNameTextView.text = folderName
            trueCloudDetailTextView.text = createDate
            trueCloudMoreImageView.onClick {
                onFolderClicked.onMoreClicked(it)
            }
            root.onClick {
                if (selectMode) {
                    if (onItemSelected?.getSelectedList()?.contains(it) == true) {
                        onItemSelected.onDeselectItem(it)
                        trueCloudSelectImageView.isSelected = false
                    } else {
                        onItemSelected?.onSelectItem(it)
                        trueCloudSelectImageView.isSelected = true
                    }
                } else {
                    onFolderClicked.onClicked(it)
                }
            }
            when {
                selectMode -> {
                    trueCloudSelectImageView.visible()
                    trueCloudMoreImageView.invisible()
                    trueCloudSelectImageView.isSelected =
                        onItemSelected?.getSelectedList()?.contains(it) == true
                }

                locatorMode -> {
                    if (folderListId != null && !folderListId.contains(folder.id)) {
                        trueCloudOverlayView.gone()
                        root.isClickable = true
                    } else {
                        trueCloudOverlayView.visible()
                        root.alpha = ALPHA_07
                        root.isClickable = false
                    }
                    trueCloudMoreImageView.gone()
                    trueCloudSelectImageView.gone()
                }

                isTrashMode -> {
                    trueCloudDetailTextView.text = deleteIn
                    trueCloudSelectImageView.gone()
                    trueCloudMoreImageView.visible()
                    trueCloudOverlayView.gone()
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
    }
}
