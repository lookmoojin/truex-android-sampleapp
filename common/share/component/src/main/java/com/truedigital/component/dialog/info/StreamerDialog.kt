package com.truedigital.component.dialog.info

import android.os.Bundle
import android.view.View
import com.truedigital.component.R

class StreamerDialog : AppInfoDialog() {

    private val codeQuotaServer = "420"

    companion object {
        val TAG = StreamerDialog::class.java.canonicalName as String
        private const val CODE_KEY = "CODE_KEY"

        fun newInstance(code: String): StreamerDialog {
            val bundle = Bundle().apply {
                putString(CODE_KEY, code)
            }
            return StreamerDialog().apply {
                arguments = bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titleDialog: String
        val messageDialog: String
        val errorCode: String
        if (arguments?.getString(CODE_KEY) == codeQuotaServer) {
            titleDialog = getString(R.string.error_reach_quota_server)
            messageDialog = getString(R.string.error_other_sub)
            errorCode = ""
        } else {
            titleDialog = getString(R.string.error_other)
            messageDialog = ""
            errorCode = arguments?.getString(CODE_KEY) ?: ""
        }

        super.setAppInfoData(
            OneButton().apply {
                icon = IconType.ERROR
                title = titleDialog
                message = messageDialog
                code = errorCode
                buttonMessage = getString(R.string.ok)
                buttonColor = ColorButton.GRAY
            }
        )
        super.onViewCreated(view, savedInstanceState)
    }
}
