package com.truedigital.foundation.extension

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

const val VERSION_CODE_TIRAMISU = 33

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= VERSION_CODE_TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= VERSION_CODE_TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}
