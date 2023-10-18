package com.truedigital.core.view

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import com.livefront.bridge.Bridge
import com.truedigital.foundation.extension.getAppColor
import com.truedigital.foundation.view.scene.FoundationFragment

enum class SystemUITheme {
    DARK, LIGHT
}

open class CoreFragment : FoundationFragment {

    companion object {
        private const val TRANSPARENT_COLOR_ID = android.R.color.transparent
        private var currentColorId = 0
    }

    constructor() : super()
    constructor(layoutId: Int) : super(layoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bridge.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Bridge.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Bridge.clear(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(android.R.color.white)
    }

    fun setStatusBarColor(
        @ColorRes resColorId: Int,
        systemUITheme: SystemUITheme = SystemUITheme.LIGHT
    ) {
        if (currentColorId != resColorId) {
            activity?.window?.apply {
                decorView.systemUiVisibility = getStatusBarTheme(systemUITheme)
                statusBarColor = context.getAppColor(resColorId)

                currentColorId = resColorId
            }
        }
    }

    fun setTransparentStatusBar() {
        if (currentColorId != TRANSPARENT_COLOR_ID) {
            activity?.window?.apply {
                statusBarColor = context.getAppColor(TRANSPARENT_COLOR_ID)

                currentColorId = TRANSPARENT_COLOR_ID
            }
        }
    }

    private fun getStatusBarTheme(systemUITheme: SystemUITheme): Int {
        return when (systemUITheme) {
            SystemUITheme.LIGHT -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            SystemUITheme.DARK -> 0
        }
    }
}
