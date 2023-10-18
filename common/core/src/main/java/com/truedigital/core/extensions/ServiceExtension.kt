package com.truedigital.core.extensions

import android.app.ActivityManager
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.view.WindowManager

fun Context.activityManager(): ActivityManager {
    return getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
}

fun Context.audioManager(): AudioManager {
    return getSystemService(Context.AUDIO_SERVICE) as AudioManager
}

fun Context.connectivityManager(): ConnectivityManager {
    return getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

fun Context.telephonyManager(): TelephonyManager {
    return getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}

fun Context.windowManager(): WindowManager {
    return getSystemService(Context.WINDOW_SERVICE) as WindowManager
}
