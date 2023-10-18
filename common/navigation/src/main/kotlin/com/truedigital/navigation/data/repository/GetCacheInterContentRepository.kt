package com.truedigital.navigation.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.truedigital.common.share.security.util.crypt.Cryptography
import com.truedigital.core.utils.DataStoreUtil
import javax.inject.Inject

interface GetCacheInterContentRepository {
    suspend fun getContentFeed(url: String): JsonArray?
    suspend fun updateContentFeedCache(url: String, content: JsonArray)
}

class GetCacheInterContentRepositoryImpl @Inject constructor(
    private val dataStoreUtil: DataStoreUtil
) : GetCacheInterContentRepository {

    companion object {
        private const val KEY_CONTENT_FEED_CACHE = "cache_content_feed"
    }

    private fun setPreferencesKey(): Preferences.Key<String> =
        stringPreferencesKey(KEY_CONTENT_FEED_CACHE)

    private val cache = hashMapOf<String, JsonArray>()

    override suspend fun getContentFeed(url: String): JsonArray? {
        return cache[url.hash()]
            ?: runCatching { getContentFeedFromPreference(url) }.getOrNull()
    }

    override suspend fun updateContentFeedCache(url: String, content: JsonArray) {
        cache[url.hash()] = content
        updateContentFeedPreference(url, content)
    }

    private suspend fun updateContentFeedPreference(url: String, content: JsonArray) {
        runCatching {
            val rawCache = dataStoreUtil.getSinglePreference(setPreferencesKey(), "[]")

            val map = hashMapOf(
                url.hash() to content
            )
            if (rawCache.isNotEmpty()) {
                saveCachePreference(
                    // update prefs
                    getCacheFromJson(rawCache).apply {
                        putAll(map)
                    }
                )
            } else {
                saveCachePreference(map)
            }
        }
    }

    private suspend fun getContentFeedFromPreference(url: String): JsonArray? {
        val rawCache = dataStoreUtil.getSinglePreference(setPreferencesKey(), "[]")
        rawCache.let { json ->
            return getCacheFromJson(json)[url.hash()]
        }
    }

    private suspend fun saveCachePreference(cacheMap: HashMap<String, JsonArray>?) {
        runCatching {
            dataStoreUtil.putPreference(setPreferencesKey(), Gson().toJson(cacheMap))
        }
    }

    @Suppress("UnstableApiUsage")
    private fun getCacheFromJson(json: String): HashMap<String, JsonArray> {
        return Gson().fromJson(
            json, object : TypeToken<HashMap<String, JsonArray>>() {}.type
        ) ?: hashMapOf()
    }

    private fun String.hash(): String {
        return Cryptography.hash(this)
    }
}
