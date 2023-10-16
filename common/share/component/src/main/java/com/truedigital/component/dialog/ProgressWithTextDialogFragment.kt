package com.truedigital.component.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.truedigital.component.R
import com.truedigital.component.databinding.DialogProgressWithTextBinding

class ProgressWithTextDialogFragment : DialogFragment() {

    private val dialogProgressWithTextBinding: DialogProgressWithTextBinding by lazy {
        DialogProgressWithTextBinding.inflate(LayoutInflater.from(context))
    }
    private var message = ""

    companion object {
        val KEY_MESSAGE = "key_message"
        const val TAG = "TAG_ProgressWIthTextDialogFragment"

        @JvmStatic
        fun newInstance(msg: String = ""): ProgressWithTextDialogFragment {
            return ProgressWithTextDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MESSAGE, msg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        return dialogProgressWithTextBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        this.arguments?.apply {
            message = getString(KEY_MESSAGE, "")
        }
    }

    private fun initView() {
        if (message.isEmpty()) {
            dialogProgressWithTextBinding.dialogProgressWithTextTextView.text =
                getString(R.string.dialog_in_progress_please_wait)
        } else {
            dialogProgressWithTextBinding.dialogProgressWithTextTextView.text = message
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}
