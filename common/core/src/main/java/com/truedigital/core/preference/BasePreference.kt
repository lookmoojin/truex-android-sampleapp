package com.truedigital.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.truedigital.core.provider.ContextDataProvider

interface BasePreference {
    fun save(keyName: String, text: String?)
    fun save(keyName: String, value: Int?)
    fun save(keyName: String, status: Boolean?)
    fun getValueString(keyName: String): String?
    fun getValueInt(keyName: String): Int
    fun getValueBoolean(keyName: String, defaultValue: Boolean): Boolean
    fun clear()
    fun remove(keyName: String)
}

class BasePreferenceImpl(contextDataProvider: ContextDataProvider) : BasePreference {

    private val preferenceName = "com.truedigital.core.preference.preferenceName"

    private val sharePref: SharedPreferences =
        contextDataProvider.getDataContext()
            .getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    override fun save(keyName: String, text: String?) {
        sharePref.edit().let {
            it.putString(keyName, text)
            it.commit()
        }
    }

    override fun save(keyName: String, value: Int?) {
        sharePref.edit().let {
            it.putInt(keyName, value ?: 0)
            it.commit()
        }
    }

    override fun save(keyName: String, status: Boolean?) {
        sharePref.edit().let {
            it.putBoolean(keyName, status ?: false)
            it.commit()
        }
    }

    override fun getValueString(keyName: String): String? {
        return sharePref.getString(keyName, null)
    }

    override fun getValueInt(keyName: String): Int {
        return sharePref.getInt(keyName, 0)
    }

    override fun getValueBoolean(keyName: String, defaultValue: Boolean): Boolean {
        return sharePref.getBoolean(keyName, defaultValue)
    }

    override fun clear() {
        sharePref.edit().let {
            it.clear()
            it.commit()
        }
    }

    override fun remove(keyName: String) {
        sharePref.edit().let {
            it.remove(keyName)
            it.commit()
        }
    }
}
