package com.truedigital.component.dialog.info

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.truedigital.component.R
import com.truedigital.component.databinding.DialogAppInfoBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

open class AppInfoDialog : DialogFragment() {

    var onButtonClickListener: (() -> Unit)? = null
    var onButtonLeftClickListener: (() -> Unit)? = null
    private var iconRes: Int? = null
    private var appInfoData: AppInfoData? = null

    private val dialogAppInfoBinding: DialogAppInfoBinding by lazy {
        DialogAppInfoBinding.inflate(LayoutInflater.from(context))
    }

    companion object {
        val TAG = AppInfoDialog::class.java.canonicalName as String
        fun newInstance() = AppInfoDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return dialogAppInfoBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false

        appInfoData?.let { data ->
            setupIcon(data.icon)
            setupTitle(data.title)
            setupMessage(data.message)
            setupErrorCode(data.code)

            when (data) {
                is OneButton -> {
                    setupRightButton(
                        data.buttonMessage,
                        data.buttonColor,
                        data.buttonContentDescription
                    )
                }
                is TwoButton -> {
                    setupLeftButton(
                        data.leftButtonMessage,
                        data.leftButtonColor,
                        data.leftButtonContentDescription
                    )
                    setupRightButton(
                        data.rightButtonMessage,
                        data.rightButtonColor,
                        data.rightButtonContentDescription
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (targetFragment is DialogInterface.OnDismissListener) {
            (targetFragment as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }

    fun setAppInfoData(data: AppInfoData) {
        appInfoData = data
    }

    fun setIcon(@DrawableRes icon: Int) {
        iconRes = icon
    }

    private fun setupIcon(icon: IconType) {
        dialogAppInfoBinding.apply {
            iconRes?.let {
                iconImageView.setImageResource(it)
                iconImageView.visible()
            } ?: run {
                when (icon) {
                    IconType.ERROR -> {
                        iconImageView.setImageResource(R.drawable.ic_error_dialog)
                        iconImageView.visible()
                    }
                    IconType.SUCCESS -> {
                        iconImageView.setImageResource(R.drawable.ic_success_dialog)
                        iconImageView.visible()
                    }
                    IconType.WARNING -> {
                        iconImageView.setImageResource(R.drawable.ic_alertpopup)
                        iconImageView.visible()
                    }
                    else -> iconImageView.gone()
                }
            }
        }
    }

    private fun setupTitle(title: String) {
        dialogAppInfoBinding.apply {
            if (title.isNotEmpty()) {
                titleTextView.text = title
                titleTextView.visible()
            } else {
                titleTextView.gone()
            }
        }
    }

    private fun setupMessage(message: String) {
        dialogAppInfoBinding.apply {
            if (message.isNotEmpty()) {
                messageTextView.text = message
                messageTextView.visible()
            } else {
                messageTextView.gone()
            }
        }
    }

    private fun setupErrorCode(errorCode: String) {
        dialogAppInfoBinding.apply {
            if (errorCode.isNotEmpty()) {
                val caption = context?.getString(R.string.error_code_caption)
                codeErrorTextView.text = "$caption $errorCode"
                codeErrorTextView.visible()
            } else {
                codeErrorTextView.gone()
            }
        }
    }

    private fun setupLeftButton(
        buttonMessage: String,
        buttonColor: ColorButton,
        buttonContentDescription: String?
    ) {
        dialogAppInfoBinding.apply {
            leftButton.apply {
                visible()
                text = buttonMessage.ifEmpty { getString(R.string.close) }
                background = ContextCompat.getDrawable(context, getColorDrawable(buttonColor))
                buttonContentDescription?.let { contentDescription = it }
                onClick {
                    onButtonLeftClickListener?.invoke()
                    dismiss()
                }
            }
        }
    }

    private fun setupRightButton(
        buttonMessage: String,
        buttonColor: ColorButton,
        buttonContentDescription: String?
    ) {
        dialogAppInfoBinding.apply {
            rightButton.apply {
                visible()
                text = buttonMessage.ifEmpty { getString(R.string.ok) }
                buttonContentDescription?.let { contentDescription = it }
                background = ContextCompat.getDrawable(context, getColorDrawable(buttonColor))
                onClick {
                    onButtonClickListener?.invoke()
                }
            }
        }
    }

    private fun getColorDrawable(color: ColorButton): Int {
        return when (color) {
            ColorButton.GRAY -> R.drawable.bg_button_round_gray
            ColorButton.RED -> R.drawable.bg_button_round_red
            ColorButton.BLACK -> R.drawable.bg_button_round_black
        }
    }
}
