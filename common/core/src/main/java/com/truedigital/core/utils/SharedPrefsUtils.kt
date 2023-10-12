package com.truedigital.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.extensions.launchSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by nut.tang on 12/20/2017 AD.
 *
 * Modified by Kittisak on 5/11/2020 AD.
 */

private const val STORAGE = "shared_pref"

class SharedPrefsUtils @Inject constructor(private val context: Context) : SharedPrefsInterface {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            STORAGE,
            Context.MODE_PRIVATE
        )
    }
    private val editor: SharedPreferences.Editor by lazy {
        CoroutineScope(Dispatchers.IO).run {
            prefs.edit()
        }
    }

    fun remove(key: String) {
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    inline fun <reified T> get(key: String): T? {
        return when (T::class) {
            String::class -> getString(key) as T?
            Boolean::class -> getString(key)?.toBoolean() as T?
            Short::class -> getString(key)?.toShort() as T?
            Int::class -> getString(key)?.toInt() as T?
            Long::class -> getString(key)?.toLong() as T?
            Double::class -> getString(key)?.toDouble() as T?
            Float::class -> getString(key)?.toFloat() as T?
            List::class -> {
                val typeOfT: Type = object : TypeToken<T>() {}.type
                Gson().fromJson(getString(key), typeOfT)
            }
            else -> getString(key)?.let { Gson().fromJson(it, T::class.java) }
        }
    }

    inline fun <reified T> get(key: String, defaultValue: T): T = get(key) ?: defaultValue

    fun <T> put(key: String, value: T): SharedPrefsUtils {
        val editor = prefs.edit()
        when (value) {
            null -> putString(key, null)
            is String -> putString(key, value)
            is Boolean -> putString(key, value.toString())
            is Short -> putString(key, value.toString())
            is Int -> putString(key, value.toString())
            is Long -> putString(key, value.toString())
            is Double -> putString(key, value.toString())
            is Float -> putString(key, value.toString())
            is Byte -> putString(key, value.toString())
            else -> putString(key, Gson().toJson(value))
        }
        editor.apply()

        return this
    }

    @JvmSynthetic
    @PublishedApi
    internal fun putString(key: String, value: String?) {
        CoroutineScope(Dispatchers.IO).launchSafe {
            editor.putString(key, value).commit()
        }
    }

    @JvmSynthetic
    @PublishedApi
    internal fun getString(key: String): String? {
        return try {
            prefs.getString(key, null)
        } catch (e: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "SharedPrefsUtils",
                "Value" to "Unexpected error caused by $e while trying to get $key to SharedPreference"
            )
            NewRelic.recordHandledException(Exception(e), handlingExceptionMap)
            null
        }
    }

    // Waif for remove due to we won't use Java anymore
    @Suppress("UNCHECKED_CAST")
    fun <T> getForJava(key: String): T {
        return when (object : TypeToken<T>() {}.type) {
            String::class -> getString(key) as T
            Boolean::class -> getString(key)?.toBoolean() as T
            Short::class -> getString(key)?.toShort() as T
            Int::class -> getString(key)?.toInt() as T
            Long::class -> getString(key)?.toLong() as T
            Double::class -> getString(key)?.toDouble() as T
            Float::class -> getString(key)?.toFloat() as T
            else -> {
                val typeOfT: Type = object : TypeToken<T>() {}.type
                Gson().fromJson(getString(key), typeOfT)
            }
        }
    }

    fun <T> getForJava(key: String, defaultValue: T): T = getForJava(key) ?: defaultValue

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String, ofClass: KClass<T>): T? {
        return when (ofClass) {
            String::class -> getString(key) as T?
            Boolean::class -> getString(key)?.toBoolean() as T?
            Short::class -> getString(key)?.toShort() as T?
            Int::class -> getString(key)?.toInt() as T?
            Long::class -> getString(key)?.toLong() as T?
            Double::class -> getString(key)?.toDouble() as T?
            Float::class -> getString(key)?.toFloat() as T?
            List::class -> {
                val typeOfT: Type = object : TypeToken<T>() {}.type
                Gson().fromJson(getString(key), typeOfT)
            }
            else -> {
                Gson().fromJson(getString(key), ofClass.java)
            }
        }
    }

    override fun <T : Any> putValue(key: String, value: T?): SharedPrefsUtils {
        val editor = prefs.edit()
        when (value) {
            null -> putString(key, null)
            is String -> putString(key, value)
            is Boolean -> putString(key, value.toString())
            is Short -> putString(key, value.toString())
            is Int -> putString(key, value.toString())
            is Long -> putString(key, value.toString())
            is Double -> putString(key, value.toString())
            is Float -> putString(key, value.toString())
            is Byte -> putString(key, value.toString())
            else -> putString(key, Gson().toJson(value))
        }
        editor.apply()

        return this
    }
}

interface SharedPrefsInterface {
    fun <T : Any> get(key: String, ofClass: KClass<T>): T?
    fun <T : Any> putValue(key: String, value: T?): SharedPrefsUtils
}

inline fun <reified T : Any> SharedPrefsInterface.get(key: String): T? {
    return get(key, T::class)
}

inline fun <reified T : Any> SharedPrefsInterface.get(key: String, defaultValue: T): T {
    return get(key, T::class) ?: defaultValue
}

inline fun <reified T : Any> SharedPrefsInterface.put(key: String, value: T?): SharedPrefsUtils {
    return putValue(key, value)
}
