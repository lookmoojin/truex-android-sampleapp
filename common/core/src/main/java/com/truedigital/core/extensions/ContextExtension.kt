package com.truedigital.core.extensions

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.truedigital.core.BuildConfig.BUILD_TYPE
import java.util.Locale

fun Context.createIntentFromString(packageName: String, flags: Int): Intent? {
    val classCreate: Class<*>? = Class.forName(packageName)
    return classCreate?.let {
        Intent(this, classCreate).apply {
            this.flags = flags
        }
    } ?: run {
        null
    }
}

fun Context.getStringByLocale(
    @StringRes stringRes: Int,
    locale: Locale,
    vararg formatArgs: Any
): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
}

fun Context?.getLifeCycleOwner(): AppCompatActivity? = when (this) {
    is ContextWrapper -> if (this is AppCompatActivity) this else this.baseContext.getLifeCycleOwner()
    else -> null
}

fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper()) f() else ContextHelper.handler.post { f() }
}

fun Context.isWifiAdbEnabled(): Boolean {
    return if (BUILD_TYPE == "release") {
        Settings.Global.getInt(contentResolver, "adb_wifi_enabled", 0) != 0
    } else {
        false
    }
}

fun Context.isDeveloperOptionsEnabled(): Boolean {
    return if (BUILD_TYPE == "release") {
        val devOptions = Settings.Global.getInt(
            contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        )
        devOptions == 1
    } else {
        false
    }
}

fun Context.isUSBDebuggingEnabled(): Boolean {
    return if (BUILD_TYPE == "release") {
        val adbEnabled = Settings.Global.getInt(
            contentResolver,
            Settings.Global.ADB_ENABLED, 0
        )
        return adbEnabled == 1
    } else {
        false
    }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
}
