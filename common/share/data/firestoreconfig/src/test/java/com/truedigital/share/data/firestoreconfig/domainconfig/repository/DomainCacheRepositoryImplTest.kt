package com.truedigital.share.data.firestoreconfig.domainconfig.repository

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.share.data.firestoreconfig.domainconfig.model.ApiServiceData
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class DomainCacheRepositoryImplTest {

    private lateinit var domainCacheRepository: DomainCacheRepository
    private val sharedPrefs: SharedPrefsUtils = mock()

    @BeforeEach
    fun setUp() {
        domainCacheRepository = DomainCacheRepositoryImpl(
            sharedPrefs,
            TestCoroutineDispatcherProvider(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun `test save data from cacheRepository`() = runTest {
        val list = listOf(ApiServiceData())

        domainCacheRepository.saveCache(list)

        val data = Gson().toJson(list).toString()

        verify(sharedPrefs, times(1)).put(DomainCacheRepositoryImpl.DOMAIN_CACHE_KEY, data)
    }

    @Test
    fun `test get data from cacheRepository`() {
        val list = domainCacheRepository.getCache()

        verify(sharedPrefs, times(1)).get(DomainCacheRepositoryImpl.DOMAIN_CACHE_KEY, "[]")
        assertNotNull(list)
        assertEquals(list.size, 0)
    }
}
