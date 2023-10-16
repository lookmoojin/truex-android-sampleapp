package com.truedigital.component.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.truedigital.component.R
import com.truedigital.component.base.BaseDialogFragment
import com.truedigital.component.databinding.DialogQrPaymentErrorBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.onClick

class QrPaymentDialogError : BaseDialogFragment() {

    val binding: DialogQrPaymentErrorBinding by viewBinding(DialogQrPaymentErrorBinding::bind)

    private var buttonColor: Int = R.drawable.bg_qr_patment_error
    private var buttonMessage = ""
    private var message = ""

    private var onClickCallback: (() -> Unit)? = null

    companion object {
        const val KEY_MESSAGE = "KEY_MESSAGE"
        const val KEY_BUTTON_MESSAGE = "BUTTON_KEY_MESSAGE"

        fun newInstance(message: String, buttonMessage: String) = QrPaymentDialogError().apply {
            arguments = Bundle().apply {
                putString(KEY_MESSAGE, message)
                putString(KEY_BUTTON_MESSAGE, buttonMessage)
            }
        }
    }

    fun setOnClickCallback(onClickCallback: (() -> Unit)?) {
        this.onClickCallback = onClickCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_qr_payment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { arguments ->
            message = arguments.getString(KEY_MESSAGE, "")
            buttonMessage = arguments.getString(KEY_BUTTON_MESSAGE, "")
        }

        if (message.isNotEmpty()) {
            binding.titleErrorTextView.text = message
            binding.titleErrorTextView.visibility = View.VISIBLE
        } else {
            binding.titleErrorTextView.visibility = View.GONE
        }

        binding.closeButtonTextView.text = buttonMessage
        binding.closeButtonTextView.apply {
            background = ContextCompat.getDrawable(view.context, buttonColor)
            onClick {
                onClickCallback?.invoke()
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
    }
}
