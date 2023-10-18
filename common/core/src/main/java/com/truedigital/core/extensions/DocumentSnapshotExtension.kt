package com.truedigital.core.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

inline fun <reified T> DocumentSnapshot.toObject(): T? {
    return runCatching {
        val json = Gson().toJson(data)
        return Gson().fromJson<T>(json)
    }.getOrDefault(null)
}
