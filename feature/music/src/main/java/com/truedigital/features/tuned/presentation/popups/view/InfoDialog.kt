package com.truedigital.features.tuned.presentation.popups.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.windowWidth
import com.truedigital.features.tuned.presentation.components.LifecycleComponentDialog

abstract class InfoDialog(dialogContext: Context) : LifecycleComponentDialog(dialogContext) {
    companion object {
        private const val WIDTH = 0.8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.requestFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setLayout(getWidth(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.music_bg_dialog
            )
        )
    }

    override fun setContentView(view: View) {
        super.setContentView(view)

        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setLayout(getWidth(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.music_bg_dialog
            )
        )
    }

    private fun getWidth(): Int = (context.windowWidth * WIDTH).toInt()
}
