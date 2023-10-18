package com.truedigital.core.extensions

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * Created by phongsathon on 5/10/2018 AD.
 */
inline fun <reified T> Gson.fromJson(json: String) =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)

fun String.isJSONValid(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        Timber.e(ex.message)
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            Timber.e(ex1.message)
            return false
        }
    }
    return true
}
