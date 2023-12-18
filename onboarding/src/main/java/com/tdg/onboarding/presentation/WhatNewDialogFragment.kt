package com.tdg.onboarding.presentation

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

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
        setupDialogWindow()
        getArgumentsWhatNewData()
    }

    private fun setupDialogWindow() {
        dialog?.window?.let { window ->
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun getArgumentsWhatNewData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(WHAT_NEW_KEY, WhatNewData::class.java)
        } else {
            arguments?.getParcelable<WhatNewData>(WHAT_NEW_KEY)
        }?.let {
            display(it)
            onClick(it)
        }
    }

    private fun onClick(data: WhatNewData) = with(binding) {
        closeButton.onClick {
            dismissSafe()
        }
        whatsNewImageView.onClick {
            if (data.type != WhatNewType.NONE) {
                openContent(data)
            }
        }
    }

    private fun display(data: WhatNewData) = with(binding) {
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
            WhatNewType.INAPPBROWSER -> {
                val ft = parentFragmentManager.beginTransaction()
                val whatsNewDialog = WebViewDialogFragment.newInstance(data.url)
                ft.add(whatsNewDialog, WebViewDialogFragment.TAG)
                ft.commitAllowingStateLoss()
            }

            WhatNewType.EXTERNALBROWSER -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                requireActivity().startActivity(browserIntent)
            }

            else -> {}
        }
    }
}