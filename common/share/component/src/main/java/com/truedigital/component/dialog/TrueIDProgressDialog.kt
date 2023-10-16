package com.truedigital.component.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.truedigital.component.R
import com.truedigital.component.databinding.TrueidProgressDialogBinding
import com.truedigital.foundation.extension.visible

class TrueIDProgressDialog(context: Context) : Dialog(context, R.style.ProgressDialog) {

    private val trueidProgressDialogBinding: TrueidProgressDialogBinding by lazy {
        TrueidProgressDialogBinding.inflate(LayoutInflater.from(context))
    }

    init {
        initView()
    }

    private fun initView() {
        setContentView(trueidProgressDialogBinding.root)
        setCancelable(true)
    }

    fun show(message: String) {
        trueidProgressDialogBinding.trueidProgressDialogTitleTextView.apply {
            visible()
            text = message
        }
        super.show()
    }
}
