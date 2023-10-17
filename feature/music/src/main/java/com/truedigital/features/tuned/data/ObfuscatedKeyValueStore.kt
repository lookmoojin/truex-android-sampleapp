package com.truedigital.features.tuned.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.reflect.KClass

/**
 * Obfuscated key-value storage, with support for most types
 * Offers a shared preferences implementation, to ease transition
 * Currently uses shared preferences as the backing data store, however this should probably be converted to
 */

class ObfuscatedKeyValueStore(context: Context, name: String) : ObfuscatedKeyValueStoreInterface {

    val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()

    private val sharedPreferences = context.getSharedPreferences(name, 0)

    @SuppressLint("CommitPrefEdits")
    private val editor = sharedPreferences.edit()

    inline fun <reified T> get(key: String): T? = when (T::class) {
        String::class -> getString(key) as T?
        Boolean::class -> getString(key)?.toBoolean() as T?
        Short::class -> getString(key)?.toShort() as T?
        Int::class -> getString(key)?.toInt() as T?
        Long::class -> getString(key)?.toLong() as T?
        Double::class -> getString(key)?.toDouble() as T?
        Float::class -> getString(key)?.toFloat() as T?
        else -> getString(key)?.let { gson.fromJson(it, T::class.java) }
    }

    inline fun <reified T> get(key: String, defValue: T): T = get(key) ?: defValue

    inline fun <reified T> put(key: String, value: T?): ObfuscatedKeyValueStore {
        @Suppress("UNCHECKED_CAST")
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
            else -> putString(key, gson.toJson(value))
        }

        return this
    }

    @JvmSynthetic
    @PublishedApi
    internal fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    @JvmSynthetic
    @PublishedApi
    internal fun putString(key: String, value: String?): ObfuscatedKeyValueStore {
        editor.putString(key, value).commit()
        return this
    }

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
            else -> getString(key)?.let { gson.fromJson(it, ofClass.java) }
        }
    }

    override fun <T : Any> putValue(key: String, value: T?): ObfuscatedKeyValueStore {
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
            else -> putString(key, gson.toJson(value))
        }
        return this
    }

    override fun remove(key: String): ObfuscatedKeyValueStore {
        editor.remove(key).commit()
        return this
    }

    override fun contains(key: String): Boolean = sharedPreferences.contains(key)
}

interface ObfuscatedKeyValueStoreInterface {
    fun <T : Any> get(key: String, ofClass: KClass<T>): T?
    fun <T : Any> putValue(key: String, value: T?): ObfuscatedKeyValueStore
    fun remove(key: String): ObfuscatedKeyValueStore
    fun contains(key: String): Boolean
}

inline fun <reified T : Any> ObfuscatedKeyValueStoreInterface.get(key: String): T? {
    return get(key, T::class)
}

inline fun <reified T : Any> ObfuscatedKeyValueStoreInterface.get(key: String, defaultValue: T): T {
    return get(key, T::class) ?: defaultValue
}

inline fun <reified T : Any> ObfuscatedKeyValueStoreInterface.put(
    key: String,
    value: T?
): ObfuscatedKeyValueStore {
    return putValue(key, value)
}
