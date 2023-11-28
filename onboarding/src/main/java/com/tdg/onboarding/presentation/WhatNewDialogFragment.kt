package com.tdg.onboarding.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.tdg.onboarding.R
import com.tdg.onboarding.databinding.DialogWhatNewBinding
import com.tdg.onboarding.domain.model.WhatNewData
import com.tdg.onboarding.domain.model.WhatNewType
import com.tdg.onboarding.injections.WhatNewComponent
import com.truedigital.core.extensions.dismissSafe
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.RESIZE_NONE
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class WhatNewDialogFragment : DialogFragment() {

    init {
        setStyle(STYLE_NO_FRAME, R.style.FullscreenDialog)
    }

    private val binding: DialogWhatNewBinding by viewBinding(DialogWhatNewBinding::bind)

    companion object {
        val TAG = WhatNewDialogFragment::class.java.simpleName
        const val WHAT_NEW_KEY = "WHAT_NEW_KEY"

        fun newInstance(whatsNewData: WhatNewData): WhatNewDialogFragment {
            val whatNewDialog = WhatNewDialogFragment()
            whatNewDialog.arguments = Bundle().apply {
                putParcelable(WHAT_NEW_KEY, whatsNewData)
            }
            return whatNewDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WhatNewComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_what_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(WHAT_NEW_KEY, WhatNewData::class.java)?.let { data ->
                display(data)
                onClick(data)
            }
        } else {
            arguments?.getParcelable<WhatNewData>(WHAT_NEW_KEY)?.let { data ->
                display(data)
                onClick(data)
            }
        }
    }

    private fun onClick(data: WhatNewData) = with(binding) {
        closeButton.onClick {
            dismissSafe()
        }
        openButton.onClick {
            openContent(data)
        }
    }

    private fun display(data: WhatNewData) = with(binding) {
        loadImage(data)
        when (data.type) {
            WhatNewType.NONE -> {
                openButton.gone()
            }

            WhatNewType.WEBVIEW -> {
                openButton.visible()
            }
        }
    }

    private fun loadImage(data: WhatNewData) = with(binding) {
        val isTablet = context?.resources?.getBoolean(R.bool.is_tablet) ?: false
        val url = if (isTablet) {
            data.imageTablet
        } else {
            data.imageMobile
        }
        whatsNewImageView.load(
            context,
            url,
            null,
            ImageView.ScaleType.CENTER_CROP,
            RESIZE_NONE
        )
    }

    private fun openContent(data: WhatNewData) {
        dismissSafe()
        when (data.type) {
            WhatNewType.WEBVIEW -> {
                val ft = parentFragmentManager.beginTransaction()
                val whatsNewDialog = WebViewDialogFragment.newInstance(data.url)
                ft.add(whatsNewDialog, WebViewDialogFragment.TAG)
                ft.commitAllowingStateLoss()
            }

            else -> {}
        }
    }
}