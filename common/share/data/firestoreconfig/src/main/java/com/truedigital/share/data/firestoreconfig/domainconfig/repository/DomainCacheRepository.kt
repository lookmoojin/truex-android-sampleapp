package com.truedigital.share.data.firestoreconfig.domainconfig.repository

import com.google.gson.Gson
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.fromJson
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.share.data.firestoreconfig.domainconfig.model.ApiServiceData
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

interface DomainCacheRepository {
    fun getCache(): List<ApiServiceData>?
    fun saveCache(data: List<ApiServiceData>)
}

class DomainCacheRepositoryImpl @Inject constructor(
    private val sharedPrefs: SharedPrefsUtils,
    private val coroutineDispatcher: CoroutineDispatcherProvider
) : DomainCacheRepository {

    companion object {
        const val DOMAIN_CACHE_KEY = "KEY_SERVICE"
    }

    override fun getCache(): List<ApiServiceData>? {
        val stringJson = sharedPrefs.get(DOMAIN_CACHE_KEY, "[]")

        return when (stringJson != "[]") {
            true -> Gson().fromJson<List<ApiServiceData>>(stringJson)
            false -> emptyList()
        }
    }

    override fun saveCache(data: List<ApiServiceData>) {
        CoroutineScope(coroutineDispatcher.io()).launchSafe {
            val jsonString = Gson().toJson(data).toString()
            sharedPrefs.put(DOMAIN_CACHE_KEY, jsonString)
        }
    }
}
