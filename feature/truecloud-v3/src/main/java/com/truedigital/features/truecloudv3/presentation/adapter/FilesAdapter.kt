package com.truedigital.features.truecloudv3.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderEmptyBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderFolderItemGridBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderFolderItemListBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderItemGridBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderItemListBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderUploadHeaderBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderUploadItemGridBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderUploadItemListBinding
import com.truedigital.features.truecloudv3.domain.model.HeaderType
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel

class FilesAdapter(
    onActionClickListener: OnActionClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @Suppress("TooManyFunctions")
    interface OnActionClickListener {
        fun onPauseClicked(model: TrueCloudFilesModel.Upload)
        fun onRetryClicked(model: TrueCloudFilesModel.Upload)
        fun onCancelClicked(model: TrueCloudFilesModel.Upload)
        fun onFileClicked(model: TrueCloudFilesModel.File)
        fun onMoreClicked(model: TrueCloudFilesModel.File)
        fun onLongClicked(model: TrueCloudFilesModel.File)
        fun onCancelAllClicked()
        fun onUploadExpandClicked(status: Boolean)
        fun onFolderClicked(model: TrueCloudFilesModel.Folder)
        fun onFolderMoreClicked(model: TrueCloudFilesModel.Folder)
        fun onAutoBackupExpandClicked(status: Boolean)
        fun onCancelAllBackupClicked()
        fun onPauseAllBackupClicked()
        fun onResumeAllBackupClicked()
    }

    interface OnItemSelect {
        fun onSelectItem(trueCloudV3Object: TrueCloudFilesModel)
        fun onDeselectItem(trueCloudV3Object: TrueCloudFilesModel)
        fun getSelectedList(): MutableList<TrueCloudFilesModel>
    }

    companion object {
        private const val UNKNOWN_ITEM_TYPE = -1
        private const val HEADER_ITEM_TYPE = 0
        private const val UPLOAD_LIST_ITEM_TYPE = 1
        const val UPLOAD_GRID_ITEM_TYPE = 2
        private const val AUTO_BACKUP_LIST_ITEM_TYPE = 3
        const val AUTO_BACKUP_GRID_ITEM_TYPE = 4
        private const val FOLDER_LIST_ITEM_TYPE = 5
        const val FOLDER_GRID_ITEM_TYPE = 6
        private const val FILE_LIST_ITEM_TYPE = 7
        const val FILE_GRID_ITEM_TYPE = 8
    }

    private var expandableUploadStatus = true
    private var expandableAutoBackupStatus = true
    private var items = mutableListOf<TrueCloudFilesModel>()
    var isShowHorizontalLayout = true
    private var selectListener: OnItemSelect? = null
    private var selectingMode = false
    private var isLocateMode = false
    private var isTrashMode = false
    private val folderSelectedId = arrayListOf<String>()

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position] is TrueCloudFilesModel.Header -> HEADER_ITEM_TYPE
            items[position] is TrueCloudFilesModel.Upload && isShowHorizontalLayout -> UPLOAD_LIST_ITEM_TYPE
            items[position] is TrueCloudFilesModel.Upload && !isShowHorizontalLayout -> UPLOAD_GRID_ITEM_TYPE
            items[position] is TrueCloudFilesModel.AutoBackup && isShowHorizontalLayout -> AUTO_BACKUP_LIST_ITEM_TYPE
            items[position] is TrueCloudFilesModel.AutoBackup && !isShowHorizontalLayout -> AUTO_BACKUP_GRID_ITEM_TYPE
            items[position] is TrueCloudFilesModel.Folder && isShowHorizontalLayout -> FOLDER_LIST_ITEM_TYPE
            items[position] is TrueCloudFilesModel.Folder && !isShowHorizontalLayout -> FOLDER_GRID_ITEM_TYPE
            items[position] is TrueCloudFilesModel.File && isShowHorizontalLayout -> FILE_LIST_ITEM_TYPE
            items[position] is TrueCloudFilesModel.File && !isShowHorizontalLayout -> FILE_GRID_ITEM_TYPE
            else -> UNKNOWN_ITEM_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_ITEM_TYPE -> FileHeaderViewHolder(
                TrueCloudv3ViewholderUploadHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onHeaderActionClickListener,
                expandableUploadStatus,
                expandableAutoBackupStatus
            )

            UPLOAD_LIST_ITEM_TYPE -> {
                when {
                    expandableUploadStatus -> {
                        UploadListViewHolder(
                            TrueCloudv3ViewholderUploadItemListBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onListViewUploadActionClickListener
                        )
                    }

                    else -> {
                        UploadEmptyViewHolder(
                            TrueCloudv3ViewholderEmptyBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }
                }
            }

            UPLOAD_GRID_ITEM_TYPE -> {
                when {
                    expandableUploadStatus -> {
                        UploadGridViewHolder(
                            TrueCloudv3ViewholderUploadItemGridBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onGridViewUploadActionClickListener
                        )
                    }

                    else -> {
                        UploadEmptyViewHolder(
                            TrueCloudv3ViewholderEmptyBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }
                }
            }

            AUTO_BACKUP_LIST_ITEM_TYPE -> {
                when {
                    expandableAutoBackupStatus -> {
                        AutoBackupListViewHolder(
                            TrueCloudv3ViewholderUploadItemListBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onListViewAutoBackupActionClickListener
                        )
                    }

                    else -> {
                        UploadEmptyViewHolder(
                            TrueCloudv3ViewholderEmptyBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }
                }
            }

            AUTO_BACKUP_GRID_ITEM_TYPE -> {
                when {
                    expandableAutoBackupStatus -> {
                        AutoBackupGridViewHolder(
                            TrueCloudv3ViewholderUploadItemGridBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onGridViewAutoBackupActionClickListener
                        )
                    }

                    else -> {
                        UploadEmptyViewHolder(
                            TrueCloudv3ViewholderEmptyBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }
                }
            }

            FOLDER_LIST_ITEM_TYPE -> FolderListViewHolder(
                TrueCloudv3ViewholderFolderItemListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onListFolderClickListener,
                selectListener,
                selectingMode,
                isLocateMode,
                isTrashMode,
                folderSelectedId
            )

            FOLDER_GRID_ITEM_TYPE -> FolderGridViewHolder(
                TrueCloudv3ViewholderFolderItemGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onGridFolderClickListener,
                selectListener,
                selectingMode,
                isTrashMode
            )

            FILE_LIST_ITEM_TYPE -> FilesListViewHolder(
                TrueCloudv3ViewholderItemListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onListViewFileActionClickListener,
                selectListener,
                selectingMode,
                isLocateMode,
                isTrashMode
            )

            FILE_GRID_ITEM_TYPE -> FilesGridViewHolder(
                TrueCloudv3ViewholderItemGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onGridViewFileActionClickListener,
                selectListener,
                selectingMode,
                isTrashMode
            )

            else -> FileHeaderViewHolder(
                TrueCloudv3ViewholderUploadHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onHeaderActionClickListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let { item ->
            when {
                holder is FileHeaderViewHolder && item is TrueCloudFilesModel.Header -> {
                    holder.bind(
                        size = getUploadListSize(item.headerType),
                        title = item.title,
                        type = item.headerType
                    )
                }

                holder is UploadGridViewHolder && item is TrueCloudFilesModel.Upload -> holder.bind(
                    item
                )

                holder is UploadListViewHolder && item is TrueCloudFilesModel.Upload -> holder.bind(
                    item
                )

                holder is AutoBackupGridViewHolder && item is TrueCloudFilesModel.AutoBackup -> holder.bind(
                    item
                )

                holder is AutoBackupListViewHolder && item is TrueCloudFilesModel.AutoBackup -> holder.bind(
                    item
                )

                holder is FolderGridViewHolder && item is TrueCloudFilesModel.Folder -> holder.bind(
                    item
                )

                holder is FolderListViewHolder && item is TrueCloudFilesModel.Folder -> holder.bind(
                    item
                )

                holder is FilesGridViewHolder && item is TrueCloudFilesModel.File -> holder.bind(
                    item
                )

                holder is FilesListViewHolder && item is TrueCloudFilesModel.File -> holder.bind(
                    item
                )

                else -> {
                    /* Not do everything */
                }
            }
        }
    }

    fun setExpandableUpload(expand: Boolean) {
        expandableUploadStatus = expand
        fileAdapterSetNotifyDataChange()
    }
    fun setExpandableAutoBackup(expand: Boolean) {
        expandableAutoBackupStatus = expand
        fileAdapterSetNotifyDataChange()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getUploadListSize(type: HeaderType): Int {
        return if (type == HeaderType.UPLOAD) {
            items.filterIsInstance<TrueCloudFilesModel.Upload>().size
        } else {
            items.filterIsInstance<TrueCloudFilesModel.AutoBackup>().size
        }
    }

    fun refreshItemList(fileList: List<TrueCloudFilesModel>) {
        items.clear()
        items.addAll(fileList)
        fileAdapterSetNotifyDataChange()
    }

    fun setUpdateFileAtIndex(fileList: List<TrueCloudFilesModel>, updateIndex: Int) {
        items.clear()
        items.addAll(fileList)
        fileAdapterSetNotifyItemChanged(updateIndex)
    }

    fun addFiles(filesList: List<TrueCloudFilesModel>) {
        items.addAll(filesList)
        fileAdapterSetNotifyDataChange()
    }

    fun setSelectedFile() {
        isLocateMode = true
    }

    fun setTrashMode() {
        isTrashMode = true
    }

    fun addFolderSelectedId(folderId: ArrayList<String>) {
        folderSelectedId.addAll(folderId)
    }

    fun setMultipleSelect(itemSelectListener: OnItemSelect) {
        selectListener = itemSelectListener
        selectingMode = true
        fileAdapterSetNotifyDataChange()
    }

    fun closeMultipleSelect() {
        selectListener = null
        selectingMode = false
        fileAdapterSetNotifyDataChange()
    }

    fun deleteObject(model: TrueCloudFilesModel) {
        items.remove(model)
        fileAdapterSetNotifyDataChange()
    }

    fun deleteMultipleObject(model: List<TrueCloudFilesModel>) {
        model.forEach { _file ->
            items.remove(_file)
        }
        fileAdapterSetNotifyDataChange()
    }

    fun getItem(): List<TrueCloudFilesModel> {
        return items
    }

    fun notifyId() {
        fileAdapterSetNotifyDataChange()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fileAdapterSetNotifyDataChange() {
        try {
            this.notifyDataSetChanged()
        } catch (e: Exception) {
            // Do nothing
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fileAdapterSetNotifyItemChanged(index: Int) {
        try {
            this.notifyItemChanged(index)
        } catch (e: Exception) {
            // Do nothing
        }
    }

    private val onGridViewUploadActionClickListener =
        object : UploadGridViewHolder.OnGridViewUploadActionClickListener {
            override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onPauseClicked(model)
            }

            override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onRetryClicked(model)
            }

            override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onCancelClicked(model)
            }
        }
    private val onListViewUploadActionClickListener =
        object : UploadListViewHolder.OnListViewUploadActionClickListener {
            override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onPauseClicked(model)
            }

            override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onRetryClicked(model)
            }

            override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onCancelClicked(model)
            }
        }
    private val onGridViewAutoBackupActionClickListener =
        object : AutoBackupGridViewHolder.OnGridViewUploadActionClickListener {
            override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onPauseClicked(model)
            }

            override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onRetryClicked(model)
            }

            override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onCancelClicked(model)
            }
        }
    private val onListViewAutoBackupActionClickListener =
        object : AutoBackupListViewHolder.OnListViewUploadActionClickListener {
            override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onPauseClicked(model)
            }

            override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onRetryClicked(model)
            }

            override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
                onActionClickListener.onCancelClicked(model)
            }
        }
    private val onListViewFileActionClickListener =
        object : FilesListViewHolder.OnListViewFileActionClickListener {
            override fun onFileClicked(model: TrueCloudFilesModel.File) {
                onActionClickListener.onFileClicked(model)
            }

            override fun onMoreClicked(model: TrueCloudFilesModel.File) {
                onActionClickListener.onMoreClicked(model)
            }
        }

    private val onGridViewFileActionClickListener =
        object : FilesGridViewHolder.OnGridViewFileActionClickListener {
            override fun onFileClicked(model: TrueCloudFilesModel.File) {
                onActionClickListener.onFileClicked(model)
            }

            override fun onLongClicked(model: TrueCloudFilesModel.File) {
                onActionClickListener.onLongClicked(model)
            }
        }

    private val onListFolderClickListener =
        object : FolderListViewHolder.OnFolderClickListener {
            override fun onClicked(model: TrueCloudFilesModel.Folder) {
                onActionClickListener.onFolderClicked(model)
            }

            override fun onMoreClicked(model: TrueCloudFilesModel.Folder) {
                onActionClickListener.onFolderMoreClicked(model)
            }
        }

    private val onGridFolderClickListener =
        object : FolderGridViewHolder.OnFolderClickListener {
            override fun onClicked(model: TrueCloudFilesModel.Folder) {
                onActionClickListener.onFolderClicked(model)
            }
        }

    private val onHeaderActionClickListener =
        object : FileHeaderViewHolder.OnHeaderActionClickListener {
            override fun onCancelAllClicked() {
                onActionClickListener.onCancelAllClicked()
            }

            override fun onUploadExpandClicked(status: Boolean) {
                onActionClickListener.onUploadExpandClicked(status)
            }

            override fun onAutoBackupExpandClicked(status: Boolean) {
                onActionClickListener.onAutoBackupExpandClicked(status)
            }

            override fun onCancelAllBackupClicked() {
                onActionClickListener.onCancelAllBackupClicked()
            }

            override fun onPauseAllBackupClicked() {
                onActionClickListener.onPauseAllBackupClicked()
            }

            override fun onResumeAllBackupClicked() {
                onActionClickListener.onResumeAllBackupClicked()
            }
        }
}
