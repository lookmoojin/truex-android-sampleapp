package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ViewholderUploadHeaderBinding
import com.truedigital.features.truecloudv3.domain.model.HeaderType
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class FileHeaderViewHolder(
    private val binding: TrueCloudv3ViewholderUploadHeaderBinding,
    private val onActionClickListener: OnHeaderActionClickListener,
    private var expandableUploadStatus: Boolean = true,
    private var expandableAutoBackupStatus: Boolean = true
) : RecyclerView.ViewHolder(binding.root) {

    interface OnHeaderActionClickListener {
        fun onCancelAllClicked()
        fun onUploadExpandClicked(status: Boolean)
        fun onAutoBackupExpandClicked(status: Boolean)
        fun onCancelAllBackupClicked()
        fun onPauseAllBackupClicked()
        fun onResumeAllBackupClicked()
    }

    companion object {
        private const val EMPTY_SIZE = 0
    }

    fun bind(size: Int, title: String, type: HeaderType) {
        binding.apply {
            when (type) {
                HeaderType.UPLOAD -> {
                    if (size > EMPTY_SIZE) {
                        setupViewTypeUpload(size.toString())
                    } else {
                        trueCloudUploadTitleLayout.gone()
                    }
                }

                HeaderType.AUTO_BACKUP -> {
                    if (size > EMPTY_SIZE) {
                        setupViewTypeAutoBackup(size.toString())
                    } else {
                        trueCloudUploadTitleLayout.gone()
                    }
                }

                HeaderType.FILE -> {
                    setupViewTypeFile(title)
                }

                else -> {
                    trueCloudUploadTitleLayout.gone()
                }
            }
        }
    }

    private fun setupViewTypeUpload(size: String) {
        binding.apply {
            trueCloudCancelAllTextView.visible()
            trueCloudExpandImageView.visible()
            trueCloudUploadTitleLayout.visible()
            trueCloudUploadTitleTextView.text =
                trueCloudUploadTitleTextView.context.getString(
                    R.string.true_cloudv3_upload_header,
                    size
                )
            if (expandableUploadStatus) {
                trueCloudExpandImageView.setImageResource(R.drawable.ic_up_24)
            } else {
                trueCloudExpandImageView.setImageResource(R.drawable.ic_down_24)
            }
            setupClickUploadHeader()
            trueCloudCancelAllTextView.onClick {
                onActionClickListener.onCancelAllClicked()
            }
        }
    }

    private fun setupViewTypeAutoBackup(size: String) {
        binding.apply {
            trueCloudCancelAllTextView.gone()
            trueCloudRetryImageView.gone()
            trueCloudExpandImageView.visible()
            trueCloudV3ActionContainer.visible()
            trueCloudCancelImageView.visible()
            trueCloudUploadTitleLayout.visible()
            trueCloudUploadTitleTextView.text =
                trueCloudUploadTitleTextView.context.getString(
                    R.string.true_cloudv3_auto_backup_header,
                    size
                )
            if (expandableUploadStatus) {
                trueCloudExpandImageView.setImageResource(R.drawable.ic_up_24)
            } else {
                trueCloudExpandImageView.setImageResource(R.drawable.ic_down_24)
            }
            setupClickAutoBackupHeader()
            setupClickActionButton()
        }
    }

    private fun setupClickActionButton() {
        binding.apply {
            trueCloudPauseImageView.onClick {
                trueCloudPauseImageView.gone()
                trueCloudRetryImageView.visible()
                onActionClickListener.onPauseAllBackupClicked()
            }
            trueCloudRetryImageView.onClick {
                trueCloudRetryImageView.gone()
                trueCloudPauseImageView.visible()
                onActionClickListener.onResumeAllBackupClicked()
            }
            trueCloudCancelImageView.onClick {
                onActionClickListener.onCancelAllBackupClicked()
            }
        }
    }

    private fun setupClickUploadHeader() {
        binding.root.onClick {
            onActionClickListener.onUploadExpandClicked(expandableUploadStatus)
            if (expandableUploadStatus) {
                binding.trueCloudExpandImageView.setImageResource(R.drawable.ic_down_24)
            } else {
                binding.trueCloudExpandImageView.setImageResource(R.drawable.ic_up_24)
            }
        }
    }

    private fun setupClickAutoBackupHeader() {
        binding.root.onClick {
            onActionClickListener.onAutoBackupExpandClicked(expandableAutoBackupStatus)
            if (expandableAutoBackupStatus) {
                binding.trueCloudExpandImageView.setImageResource(R.drawable.ic_down_24)
            } else {
                binding.trueCloudExpandImageView.setImageResource(R.drawable.ic_up_24)
            }
        }
    }

    private fun setupViewTypeFile(title: String) {
        binding.apply {
            trueCloudCancelAllTextView.gone()
            trueCloudExpandImageView.gone()
            trueCloudUploadTitleLayout.visible()
            trueCloudUploadTitleTextView.text = title
            this.root.setOnClickListener(null)
        }
    }
}
