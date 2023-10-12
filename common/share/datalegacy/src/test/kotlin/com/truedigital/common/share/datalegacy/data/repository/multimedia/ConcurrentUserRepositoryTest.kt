package com.truedigital.common.share.datalegacy.data.repository.multimedia

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.api.ccu.CcuApiInterface
import com.truedigital.common.share.datalegacy.data.multimedia.model.CcuResponse
import com.truedigital.core.extensions.collectSafe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConcurrentUserRepositoryTest {
    private val ccuApi: CcuApiInterface = mock()

    private lateinit var concurrentUserRepository: ConcurrentUserRepository

    @BeforeEach
    fun setup() {
        concurrentUserRepository = ConcurrentUserRepositoryImpl(ccuApi)
    }

    @Test
    fun `Test - getConcurrentUserFlow concurrentMap is empty and isFetchFromRemote is true should get data form api`() =
        runTest {
            val mockResponse = Response.success(
                CcuResponse().apply {
                    response = CcuResponse.Response().apply {
                        itemMap = mapOf(
                            "c03" to CcuResponse.Stats().apply { total = 10 }
                        )
                    }
                }
            )

            whenever(ccuApi.getCcuFlow()).thenReturn(mockResponse)

            val result = concurrentUserRepository.getConcurrentUserFlow(true)
            result.collectSafe { ccuMap ->
                assertTrue(ccuMap.isNotEmpty())
                assertEquals(10, ccuMap["c03"])
                verify(ccuApi, times(1)).getCcuFlow()
            }
        }

    @Test
    fun `Test - getConcurrentUserFlow concurrentMap is empty and isFetchFromRemote is false should get data form api`() =
        runTest {
            val mockResponse = Response.success(
                CcuResponse().apply {
                    response = CcuResponse.Response().apply {
                        itemMap = mapOf(
                            "c03" to CcuResponse.Stats().apply { total = 10 }
                        )
                    }
                }
            )

            whenever(ccuApi.getCcuFlow()).thenReturn(mockResponse)

            val result = concurrentUserRepository.getConcurrentUserFlow(false)
            result.collectSafe { ccuMap ->
                assertTrue(ccuMap.isNotEmpty())
                assertEquals(10, ccuMap["c03"])
            }
        }

    @Test
    fun `Test - getConcurrentUserFlow concurrentMap is not empty and isFetchFromRemote is false should get data form cache`() =
        runTest {
            val mockResponse = Response.success(
                CcuResponse().apply {
                    response = CcuResponse.Response().apply {
                        itemMap = mapOf(
                            "c03" to CcuResponse.Stats().apply { total = 10 }
                        )
                    }
                }
            )
            val mockCcuCache = mapOf("c03" to 55)

            whenever(ccuApi.getCcuFlow()).thenReturn(mockResponse)
            (concurrentUserRepository as ConcurrentUserRepositoryImpl).setConcurrentMapData(
                mockCcuCache
            )

            val result = concurrentUserRepository.getConcurrentUserFlow(false)
            result.collectSafe { ccuMap ->
                assertTrue(ccuMap.isNotEmpty())
                assertEquals(55, ccuMap["c03"])
                verify(ccuApi, never()).getCcuFlow()
            }
        }

    @Test
    fun `Test - getConcurrentUserFlow concurrentMap is not empty and isFetchFromRemote is true should get data form api`() =
        runTest {
            val mockResponse = Response.success(
                CcuResponse().apply {
                    response = CcuResponse.Response().apply {
                        itemMap = mapOf(
                            "c03" to CcuResponse.Stats().apply { total = 10 }
                        )
                    }
                }
            )
            val mockCcuCache = mapOf("c03" to 55)

            whenever(ccuApi.getCcuFlow()).thenReturn(mockResponse)
            (concurrentUserRepository as ConcurrentUserRepositoryImpl).setConcurrentMapData(
                mockCcuCache
            )

            val result = concurrentUserRepository.getConcurrentUserFlow(true)
            result.collectSafe { ccuMap ->
                assertTrue(ccuMap.isNotEmpty())
                assertEquals(10, ccuMap["c03"])
                verify(ccuApi, times(1)).getCcuFlow()
            }
        }
}
