package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderFolderItemGridBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class FolderGridViewHolder(
    private val binding: TrueCloudv3ViewholderFolderItemGridBinding,
    private val onFolderClicked: OnFolderClickListener,
    private val onItemSelected: FilesAdapter.OnItemSelect?,
    private val selectMode: Boolean = false,
    private val isTrashMode: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    interface OnFolderClickListener {
        fun onClicked(model: TrueCloudFilesModel.Folder)
    }

    fun bind(folder: TrueCloudFilesModel.Folder) = with(binding) {
        folder.let {
            val folderName = it.name
            val deleteIn = it.deleteIn
            trueCloudNameTextView.text = folderName
            root.onClick {
                if (selectMode) {
                    if (onItemSelected?.getSelectedList()?.contains(it) == true) {
                        onItemSelected.onDeselectItem(it)
                        trueCloudGridSelectImageView.isSelected = false
                    } else {
                        onItemSelected?.onSelectItem(it)
                        trueCloudGridSelectImageView.isSelected = true
                    }
                } else {
                    onFolderClicked.onClicked(it)
                }
            }

            when {
                selectMode -> {
                    trueCloudDetailTextView.gone()
                    trueCloudGridSelectImageView.visible()
                    trueCloudGridSelectImageView.isSelected =
                        onItemSelected?.getSelectedList()?.contains(it) == true
                }

                isTrashMode -> {
                    trueCloudGridSelectImageView.gone()
                    trueCloudDetailTextView.visible()
                    trueCloudDetailTextView.text = deleteIn
                }

                else -> {
                    trueCloudGridSelectImageView.gone()
                    trueCloudDetailTextView.gone()
                }
            }
        }
    }
}
