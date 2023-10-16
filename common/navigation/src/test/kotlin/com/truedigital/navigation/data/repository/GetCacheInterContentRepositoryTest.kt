package com.truedigital.navigation.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.truedigital.common.share.security.util.crypt.Cryptography
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.navigation.TestData
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
internal class GetCacheInterContentRepositoryTest {

    private val dataStoreUtils = mockk<DataStoreUtil>()
    private val preferenceKey = "cache_content_feed"

    private lateinit var repository: GetCacheInterContentRepository

    @BeforeEach
    fun setUp() {
        repository = GetCacheInterContentRepositoryImpl(
            dataStoreUtils
        )
    }

    @Test
    fun `get content feed successfully, expect return json array`() = runTest {
        val url = "test"
        val expected = TestData.getShelfSkeletonJsonArray(2)
        val hashMap = hashMapOf(
            Cryptography.hash(url) to expected
        )
        coEvery {
            dataStoreUtils.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(hashMap)

        val result = repository.getContentFeed(url)

        assertTrue(result == expected)
        coVerify(exactly = 1) {
            dataStoreUtils.getSinglePreference(
                key = stringPreferencesKey(preferenceKey),
                defaultValue = "[]"
            )
        }
    }

    @Test
    fun `get content feed unsuccessfully, expect return null`() = runTest {
        coEvery {
            dataStoreUtils.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns null.toString()

        val result = repository.getContentFeed("")
        coVerify(exactly = 1) {
            dataStoreUtils.getSinglePreference(
                key = stringPreferencesKey(preferenceKey),
                defaultValue = "[]"
            )
        }
        kotlin.test.assertNull(result)
    }

    @Test
    fun `set new fresh content feed, expect save into preference`() = runTest {
        val url = "test"
        val content = JsonArray()
        val hashMap = hashMapOf(Cryptography.hash(url) to content)
        coEvery {
            dataStoreUtils.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns mockk()
        coEvery {
            dataStoreUtils.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(hashMap)

        repository.updateContentFeedCache(url, content)
        coVerify(exactly = 1) {
            dataStoreUtils.putPreference(
                key = stringPreferencesKey(preferenceKey),
                value = Gson().toJson(hashMap)
            )
        }
    }

    @Test
    fun `update content feed, expect save into preference by updating a old data`() = runTest {
        val url = "new"
        val content = JsonArray()
        val oldHashMap = hashMapOf("old" to JsonArray())
        coEvery {
            dataStoreUtils.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns mockk()
        coEvery {
            dataStoreUtils.getSinglePreference(
                key = any<Preferences.Key<String>>(),
                defaultValue = any()
            )
        } returns Gson().toJson(oldHashMap)

        repository.updateContentFeedCache(url, content)
        coVerify(exactly = 1) {
            dataStoreUtils.getSinglePreference(
                key = stringPreferencesKey(preferenceKey),
                defaultValue = "[]"
            )
        }
        coVerify(exactly = 1) {
            dataStoreUtils.putPreference(
                key = stringPreferencesKey(preferenceKey),
                value = any()
            )
        }
    }
}
