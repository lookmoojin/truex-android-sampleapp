package com.truedigital.features.truecloudv3.presentation.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderUploadItemGridBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.convertBackupToUpload

class AutoBackupGridViewHolder(
    private val binding: TrueCloudv3ViewholderUploadItemGridBinding,
    private var onActionClick: OnGridViewUploadActionClickListener
) : RecyclerView.ViewHolder(binding.root) {

    interface OnGridViewUploadActionClickListener {
        fun onPauseClicked(model: TrueCloudFilesModel.Upload)
        fun onRetryClicked(model: TrueCloudFilesModel.Upload)
        fun onCancelClicked(model: TrueCloudFilesModel.Upload)
    }

    companion object {
        private const val NO_PROGRESS = 0
    }

    fun bind(file: TrueCloudFilesModel.AutoBackup) {
        binding.apply {
            var status = root.context.getString(file.status.textStatus)
            if (TaskStatusType.IN_PROGRESS == file.status) {
                status = root.context.getString(
                    R.string.true_cloudv3_upload_status_uploading,
                    file.progressMessage
                )
            }
            trueCloudLinearProgressIndicator.progress = file.progress?.toInt() ?: NO_PROGRESS
            trueCloudPauseImageView.visibility = file.status.showActionPause
            trueCloudRetryImageView.visibility = file.status.showActionRetry
            setActionViewColor(file.status)
            trueCloudStatusTextView.text = status
            trueCloudItemImageView.setBackgroundResource(file.type.res)

            trueCloudPauseImageView.setOnClickListener {
                onActionClick.onPauseClicked(file.convertBackupToUpload())
            }
            trueCloudRetryImageView.setOnClickListener {
                onActionClick.onRetryClicked(file.convertBackupToUpload())
            }
            trueCloudCancelImageView.setOnClickListener {
                onActionClick.onCancelClicked(file.convertBackupToUpload())
            }
        }
    }

    fun setActionViewColor(status: TaskStatusType) {
        binding.apply {
            trueCloudStatusTextView.setTextColor(
                ContextCompat.getColor(
                    trueCloudStatusTextView.context,
                    status.statusColor
                )
            )

            trueCloudRetryImageView.imageTintList = (
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        trueCloudRetryImageView.context,
                        status.actionColor
                    )
                )
                )
            trueCloudCancelImageView.imageTintList = (
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        trueCloudCancelImageView.context,
                        status.actionColor
                    )
                )
                )

            trueCloudLinearProgressIndicator.trackColor = ContextCompat.getColor(
                trueCloudCancelImageView.context,
                status.indicatorColor
            )
        }
    }
}
