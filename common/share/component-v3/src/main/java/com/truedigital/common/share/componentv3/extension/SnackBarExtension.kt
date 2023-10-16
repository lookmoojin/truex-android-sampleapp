package com.truedigital.common.share.componentv3.extension

import android.content.res.ColorStateList
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.core.extensions.dpToPx

fun View.showSnackBar(
    @StringRes messageResource: Int,
    snackBarType: SnackBarType
) {
    val customView = CompoundButton.inflate(context, R.layout.view_snackbar, null)
    customView.findViewById<TextView>(R.id.snackBarTextView).apply {
        text = context.getString(messageResource)
        setTextColor(ContextCompat.getColor(context, snackBarType.textColor))
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, snackBarType.color)
        )
        val icon = ContextCompat.getDrawable(this.rootView.context, snackBarType.icon)
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
    }

    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    (snackBar.view as? Snackbar.SnackbarLayout)?.apply {
        elevation = 14.dpToPx(context)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(customView, 0)
    }
    snackBar.show()
}

fun View.showSnackBar(
    messageText: String,
    snackBarType: SnackBarType,
) {
    val customView = CompoundButton.inflate(context, R.layout.view_snackbar, null)
    customView.findViewById<TextView>(R.id.snackBarTextView).apply {
        text = messageText
        setTextColor(ContextCompat.getColor(context, snackBarType.textColor))
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, snackBarType.color)
        )
        val icon = ContextCompat.getDrawable(this.rootView.context, snackBarType.icon)
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
    }

    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    (snackBar.view as? Snackbar.SnackbarLayout)?.apply {
        elevation = 14.dpToPx(context)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(customView, 0)
    }
    snackBar.show()
}

fun View.showSnackBarDark(
    @StringRes messageResource: Int,
    snackBarType: SnackBarType
) {
    val customView = CompoundButton.inflate(context, R.layout.view_snackbar_dark, null)
    customView.findViewById<TextView>(R.id.snackBarTextView).apply {
        text = context.getString(messageResource)
        setTextColor(ContextCompat.getColor(context, snackBarType.textColor))
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, snackBarType.color)
        )
        val icon = ContextCompat.getDrawable(this.rootView.context, snackBarType.icon)
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
    }

    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    (snackBar.view as? Snackbar.SnackbarLayout)?.apply {
        elevation = 14.dpToPx(context)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(customView, 0)
    }
    snackBar.show()
}

fun View.showSnackBarDark(
    messageText: String,
    snackBarType: SnackBarType,
) {
    val customView = CompoundButton.inflate(context, R.layout.view_snackbar_dark, null)
    customView.findViewById<TextView>(R.id.snackBarTextView).apply {
        text = messageText
        setTextColor(ContextCompat.getColor(context, snackBarType.textColor))
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, snackBarType.color)
        )
        val icon = ContextCompat.getDrawable(this.rootView.context, snackBarType.icon)
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
    }

    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    (snackBar.view as? Snackbar.SnackbarLayout)?.apply {
        elevation = 14.dpToPx(context)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(customView, 0)
    }
    snackBar.show()
}
