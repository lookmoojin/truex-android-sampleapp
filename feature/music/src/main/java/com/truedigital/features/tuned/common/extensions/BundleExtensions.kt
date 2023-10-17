package com.truedigital.features.tuned.common.extensions

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
fun Bundle.put(vararg params: Pair<String, Any?>): Bundle {
    params.forEach {
        when (val value = it.second) {
            null -> remove(it.first)
            is Int -> putInt(it.first, value)
            is Long -> putLong(it.first, value)
            is String -> putString(it.first, value)
            is CharSequence -> putCharSequence(it.first, value)
            is Float -> putFloat(it.first, value)
            is Double -> putDouble(it.first, value)
            is Char -> putChar(it.first, value)
            is Short -> putShort(it.first, value)
            is Boolean -> putBoolean(it.first, value)
            is Parcelable -> putParcelable(it.first, value)
            is Serializable -> putSerializable(it.first, value)
            else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
    }
    return this
}
