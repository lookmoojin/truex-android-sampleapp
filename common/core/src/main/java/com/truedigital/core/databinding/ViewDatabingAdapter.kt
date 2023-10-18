package com.truedigital.core.databinding

import android.view.View
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun setVisible(view: View, visible: Boolean) {
    view.isVisible = visible
}

@BindingAdapter("isGone")
fun setViewGone(view: View, gone: Boolean) {
    view.isGone = gone
}

@BindingAdapter("html")
fun setTextHtml(view: TextView, text: String) {
    view.text = text.parseAsHtml()
}
