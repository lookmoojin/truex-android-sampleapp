package com.truedigital.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardUtils {
    companion object {
        fun forceKeyboard(view: View?, show: Boolean) {
            view?.let {
                val c = view.context
                if (show) {
                    (c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .showSoftInput(view, InputMethodManager.SHOW_FORCED)
                } else {
                    (c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }
}
