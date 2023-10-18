package com.truedigital.features.tuned.presentation.popups.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_16F
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.common.extensions.windowWidth
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.databinding.DialogFullScreenImageBinding
import javax.inject.Inject

class FullScreenImageDialog(context: Context, val image: String) : Dialog(context) {

    @Inject
    lateinit var imageManager: ImageManager

    private val binding: DialogFullScreenImageBinding by lazy {
        DialogFullScreenImageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(binding.root)

        // Load Image
        val size = context.windowWidth - (context.resources.dp(FLOAT_16F) * 2)
        imageManager.init(context)
            .load(image)
            .options(size)
            .intoRoundedCorner(binding.ivProductImage) { isSuccess ->
                if (isSuccess) {
                    binding.loader.visibilityGone()
                } else {
                    context.toast(R.string.error_loading_images)
                    dismiss()
                }
            }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        dismiss()
        return false
    }
}
