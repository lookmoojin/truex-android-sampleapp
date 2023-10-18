package com.truedigital.features.tuned.presentation.popups.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.truedigital.features.tuned.R

object LoadingDialog {

    var pd: Dialog? = null

    fun show(context: Context) {
        if (pd == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.dialog_loading, null)
            pd = Dialog(context)
            pd?.let {
                it.requestWindowFeature(Window.FEATURE_NO_TITLE)
                it.window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.setContentView(view)
                it.setOnDismissListener { pd = null }
                it.show()
            }
        }
    }

    fun dismiss() {
        pd?.dismiss()
        pd = null
    }
}
