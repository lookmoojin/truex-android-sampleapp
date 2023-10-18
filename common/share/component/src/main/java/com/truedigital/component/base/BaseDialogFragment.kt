package com.truedigital.component.base

import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.newrelic.agent.android.NewRelic
import com.truedigital.component.dialog.MIAlertDialogFragment

open class BaseDialogFragment : DialogFragment, FragmentInterface {

    private var alertDialog: MIAlertDialogFragment? = null

    constructor() : super()
    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    init {
        activity?.let {
            if (it.isFinishing || it.isDestroyed) return@let
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (this.isAdded) return
        try {
            super.show(manager, tag)
        } catch (exception: WindowManager.BadTokenException) {
            val exceptionAttributes = mapOf(
                "Key" to "BaseDialogFragment.show",
                "Value" to exception.message
            )
            NewRelic.recordHandledException(exception, exceptionAttributes)
        }
    }

    protected fun showAlertDialog(
        title: String,
        message: String,
        positiveTitle: String,
        negativeTitle: String,
        listener: MIAlertDialogFragment.DialogListener
    ) {
        hideAlertDialog()
        alertDialog =
            MIAlertDialogFragment.newInstance(title, message, positiveTitle, negativeTitle)
                .apply {
                    setDialogListener(listener)
                    show(parentFragmentManager, MIAlertDialogFragment::class.java.canonicalName)
                }
    }

    private fun hideAlertDialog() {
        alertDialog?.dismiss()
    }
}
