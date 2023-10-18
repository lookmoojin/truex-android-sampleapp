package com.truedigital.core.extensions

import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response

inline fun <reified T> HttpException.toObject(): T? {
    if (response()?.errorBody() != null) {
        val json = response()?.errorBody()?.string()

        return try {
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
    return null
}

inline fun <reified T> Response<T>.toErrorObject(): T? {
    if (this.errorBody() != null) {
        val json = this.errorBody()?.string()
        return try {
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
    return null
}
