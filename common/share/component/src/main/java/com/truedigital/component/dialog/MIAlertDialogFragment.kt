package com.truedigital.component.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.truedigital.component.R
import com.truedigital.component.view.AppTextView
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import java.util.EventListener

class MIAlertDialogFragment : DialogFragment() {

    interface DialogListener : EventListener {
        fun onPositiveClick()
        fun onNegativeClick()
    }

    companion object {
        val TAG = MIAlertDialogFragment::class.java.canonicalName as String
        private const val MODE_SINGLE = 1
        private const val MODE_TWIN = 2

        fun newInstance(
            title: String,
            message: String,
            positiveTitle: String
        ): MIAlertDialogFragment {
            return MIAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt("mode", MODE_SINGLE)
                    putString("title", title)
                    putString("message", message)
                    putString("positiveTitle", positiveTitle)
                }
            }
        }

        fun newInstance(
            title: String,
            message: String,
            positiveTitle: String,
            negativeTitle: String
        ): MIAlertDialogFragment {
            return MIAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt("mode", MODE_TWIN)
                    putString("title", title)
                    putString("message", message)
                    putString("positiveTitle", positiveTitle)
                    putString("negativeTitle", negativeTitle)
                }
            }
        }
    }

    private var dialogListener: DialogListener? = null
    private var okBtn: AppTextView? = null
    private var cancelBtn: AppTextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = if (arguments?.getInt("mode") == MODE_TWIN) {
            inflater.inflate(R.layout.mi_alert_dialog_fragment, container, false)
        } else {
            inflater.inflate(R.layout.mi_alert_dialog_fragment_single, container, false)
        }

        val mTitle: AppTextView
        val mSubTitle: AppTextView
        if (arguments?.getInt("mode") == MODE_SINGLE) {
            mTitle = view.findViewById(R.id.miAlertTitleTextView) as AppTextView
            mSubTitle = view.findViewById(R.id.miAlertSubTitleTextView) as AppTextView
            okBtn = view.findViewById(R.id.miAlertOkTextView) as AppTextView

            val title = arguments?.getString("title")
            val message = arguments?.getString("message")
            if (title.isNullOrEmpty()) {
                mTitle.gone()
            }
            mTitle.text = title
            mSubTitle.text = message

            okBtn?.text = arguments?.getString("positiveTitle") ?: ""
            setClickListener(false)
        } else if (arguments?.getInt("mode") == MODE_TWIN) {
            val negativeTitle = arguments?.getString("negativeTitle")
            mTitle = view.findViewById(R.id.miAlertTitleTextView) as AppTextView
            mSubTitle = view.findViewById(R.id.miAlertSubTitleTextView) as AppTextView
            okBtn = view.findViewById(R.id.miAlertOkTextView) as AppTextView
            cancelBtn = view.findViewById(R.id.miAlertCancelTextView) as AppTextView

            val title = arguments?.getString("title")
            val message = arguments?.getString("message")
            if (title.isNullOrEmpty()) {
                mTitle.gone()
            }
            mTitle.text = title
            mSubTitle.text = message

            okBtn?.text = arguments?.getString("positiveTitle") ?: ""
            cancelBtn?.text = negativeTitle
            cancelBtn?.visible()
            setClickListener(true)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val width = resources.getDimensionPixelSize(R.dimen.dialog_std_width)
        val height = resources.getDimensionPixelSize(R.dimen.dialog_std_height)
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroyView() {
        dialog?.setDismissMessage(null)
        super.onDestroyView()
    }

    fun setDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    private fun removeDialogListener() {
        this.dialogListener = null
    }

    private fun setClickListener(checkCancleBtnIsUse: Boolean) {
        if (checkCancleBtnIsUse) {
            cancelBtn?.onClick {
                dialogListener?.onNegativeClick()
                removeDialogListener()
                dismiss()
            }
        }

        okBtn?.onClick {
            dialogListener?.onPositiveClick()
            dismiss()
        }
    }
}
