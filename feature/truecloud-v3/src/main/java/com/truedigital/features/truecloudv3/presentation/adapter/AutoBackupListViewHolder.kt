package com.truedigital.features.truecloudv3.presentation.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderUploadItemListBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.convertBackupToUpload

class AutoBackupListViewHolder(
    private val binding: TrueCloudv3ViewholderUploadItemListBinding,
    private var onActionClick: OnListViewUploadActionClickListener
) : RecyclerView.ViewHolder(binding.root) {

    interface OnListViewUploadActionClickListener {
        fun onPauseClicked(model: TrueCloudFilesModel.Upload)
        fun onRetryClicked(model: TrueCloudFilesModel.Upload)
        fun onCancelClicked(model: TrueCloudFilesModel.Upload)
    }

    fun bind(task: TrueCloudFilesModel.AutoBackup) {
        binding.apply {
            trueCloudLayoutUploadItem.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    task.status.layoutColor
                )
            )
            trueCloudStatusTextView.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    task.status.statusColor
                )
            )
            trueCloudNameTextView.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    task.status.textNameColor
                )
            )
            trueCloudDetailTextView.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    task.status.textDetailColor
                )
            )
            trueCloudCancelImageView.imageTintList = (
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            root.context,
                            task.status.actionColor
                        )
                    )
                    )
            trueCloudRetryImageView.imageTintList = (
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            root.context,
                            task.status.actionColor
                        )
                    )
                    )
            trueCloudUploadProgress.progress = task.progress?.toInt() ?: 0
            var status = root.context.getString(task.status.textStatus)
            if (TaskStatusType.IN_PROGRESS == task.status) {
                status = root.context.getString(
                    R.string.true_cloudv3_upload_status_uploading,
                    task.progressMessage
                )
            }
            trueCloudPauseImageView.visibility = task.status.showActionPause
            trueCloudRetryImageView.visibility = task.status.showActionRetry
            trueCloudStatusTextView.text = status
            trueCloudNameTextView.text = task.name
            trueCloudDetailTextView.text = task.size
            trueCloudPauseImageView.setOnClickListener { onActionClick.onPauseClicked(task.convertBackupToUpload()) }
            trueCloudRetryImageView.setOnClickListener { onActionClick.onRetryClicked(task.convertBackupToUpload()) }
            trueCloudCancelImageView.setOnClickListener { onActionClick.onCancelClicked(task.convertBackupToUpload()) }
        }
    }
}
