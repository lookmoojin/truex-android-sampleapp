package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3SharedFileViewBinding
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.loadWithImageCallback
import com.truedigital.foundation.extension.visible

class TrueCloudV3SharedFileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val CATEGORY_IMAGE = "IMAGE"
        private const val CATEGORY_VIDEO = "VIDEO"
        private const val CATEGORY_AUDIO = "AUDIO"
        private const val CATEGORY_OTHER = "OTHER"
    }

    private val binding: TrueCloudv3SharedFileViewBinding =
        TrueCloudv3SharedFileViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
    }

    fun setDisplayItem(item: SharedFileModel, onSuccessCallBack: () -> Unit) {
        when (item.category) {
            CATEGORY_IMAGE -> {
                binding.trueCloudImageView.loadWithImageCallback(
                    context = context,
                    url = item.fileUrl,
                    onSuccess = onSuccessCallBack
                )
            }

            CATEGORY_VIDEO -> {
                binding.trueCloudImageView.gone()
                binding.trueCloudImageViewPlaceHolder.setImageResource(R.drawable.ic_place_holder_true_cloudv3_video)
                binding.trueCloudImageViewPlaceHolder.visible()
            }

            CATEGORY_AUDIO -> {
                binding.trueCloudImageView.gone()
                binding.trueCloudImageView.setImageResource(R.drawable.ic_place_holder_true_cloudv3_audio)
                binding.trueCloudImageViewPlaceHolder.visible()
            }

            CATEGORY_OTHER -> {
                binding.trueCloudImageView.gone()
                binding.trueCloudImageView.setImageResource(R.drawable.ic_place_holder_true_cloudv3_file)
                binding.trueCloudImageViewPlaceHolder.visible()
            }

            else -> {
                binding.trueCloudImageView.gone()
                binding.trueCloudImageView.setImageResource(R.drawable.ic_place_holder_true_cloudv3_file)
                binding.trueCloudImageViewPlaceHolder.visible()
            }
        }
    }
}
