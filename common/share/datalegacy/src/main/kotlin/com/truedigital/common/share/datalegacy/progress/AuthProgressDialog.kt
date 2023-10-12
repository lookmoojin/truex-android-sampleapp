package com.truedigital.common.share.datalegacy.progress

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.truedigital.common.share.datalegacy.R
import com.truedigital.common.share.datalegacy.databinding.AuthProgressDialogBinding
import com.truedigital.foundation.extension.visible

class AuthProgressDialog(context: Context) : Dialog(context, R.style.ProgressDialog) {

    private val authProgressDialogBinding: AuthProgressDialogBinding by lazy {
        AuthProgressDialogBinding.inflate(LayoutInflater.from(context))
    }

    init {
        initView()
    }

    private fun initView() {
        setContentView(authProgressDialogBinding.root)
        setCancelable(true)
    }

    fun show(message: String) {
        authProgressDialogBinding.authProgressTextView.apply {
            visible()
            text = message
        }
        super.show()
    }
}
