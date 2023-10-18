package com.truedigital.core.extensions

import android.app.Dialog
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.newrelic.agent.android.NewRelic

fun DialogFragment.dismissSafe() {
    Handler(Looper.getMainLooper()).post {
        try {
            dismiss()
        } catch (error: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "DialogFragment.dismissSafe()",
                "Value" to "Can't dismiss dialog because: " + error.localizedMessage
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
        }
    }
}

fun DialogFragment.dismissAllowingStateLossSafe() {
    Handler(Looper.getMainLooper()).post {
        try {
            dismissAllowingStateLoss()
        } catch (error: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "DialogFragment.dismissAllowingStateLossSafe()",
                "Value" to "Can't dismissAllowingStateLossSafe dialog because: " + error.localizedMessage
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
        }
    }
}

fun DialogFragment.showSafe(manager: FragmentManager, tag: String?) {
    try {
        show(manager, tag)
    } catch (error: Exception) {
        val handlingExceptionMap = mapOf(
            "Key" to "DialogFragment.showSafe()",
            "Value" to "FragmentManager is invalid caused by: " + error.localizedMessage
        )
        NewRelic.recordHandledException(error, handlingExceptionMap)
    }
}

fun Dialog.dismissSafe() {
    Handler(Looper.getMainLooper()).post {
        try {
            dismiss()
        } catch (error: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "DialogFragment.dismissSafe()",
                "Value" to "Can't dismiss dialog because: " + error.localizedMessage
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
        }
    }
}

fun DialogInterface.dismissSafe() {
    Handler(Looper.getMainLooper()).post {
        try {
            dismiss()
        } catch (error: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "DialogFragment.dismissSafe()",
                "Value" to "Can't dismiss dialog because: " + error.localizedMessage
            )
            NewRelic.recordHandledException(error, handlingExceptionMap)
        }
    }
}
