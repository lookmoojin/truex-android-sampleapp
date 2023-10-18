package com.truedigital.component.dialog.trueid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truedigital.component.R
import com.truedigital.component.databinding.DialogTypeDBinding
import com.truedigital.component.dialog.trueid.DialogData
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.component.dialog.trueid.TrueIdDialogFragment
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class TypeDDialog : TrueIdDialogFragment() {

    companion object {
        fun newInstance(
            data: DialogData
        ) = TypeDDialog().apply {
            init(
                data.apply {
                    isCancelable = true
                }
            )
        }
    }

    private lateinit var binding: DialogTypeDBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTypeDBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NO_TITLE,
            data?.theme?.fullScreen ?: R.style.TrueIdDialogTheme_FullScreen
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        data?.let { data ->
            dialog?.window?.run {
                setWindowAnimations(data.topNavigation.animation)
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            with(binding) {
                super.initView(
                    icon = ivIcon,
                    title = tvTitle,
                    subTitle = tvSubTitle,
                    primary = btnPrimary
                )
                initBackAndCloseButton(closeButton, backButton)
                when (data.topNavigation) {
                    DialogTopNavigationType.BACK_BUTTON -> {
                        closeButton.gone()
                        backButton.visible()
                    }
                    DialogTopNavigationType.CLOSE_BUTTON -> {
                        backButton.gone()
                        closeButton.visible()
                    }
                    DialogTopNavigationType.NO_BUTTON -> {
                        backButton.gone()
                        closeButton.gone()
                    }
                }
            }
        }
    }
}
