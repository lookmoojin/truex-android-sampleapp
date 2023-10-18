package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.api.WeMallShelfApiInterface
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseData
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseItems
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeMallShelfRepositoryTest {
    private val api: WeMallShelfApiInterface = mock()
    private lateinit var weMallShelfRepository: WeMallShelfRepository
    private lateinit var request: WeMallShelfRequestModel
    private val token = "authorization"
    private val deviceId = "1234"
    private val ssoId = "5678"
    private val lang = "en"
    private val categoryName = "categoryName"
    private val limit = "20"

    @BeforeEach
    fun setup() {
        weMallShelfRepository = WeMallShelfRepositoryImpl(api)
        request = WeMallShelfRequestModel(
            deviceID = deviceId,
            ssoID = ssoId,
            lang = lang,
            categoryName = categoryName,
            limit = limit
        )
    }

    @Test
    fun `when getWeMallShelfComponent success and code is 10001 Then should return data`() =
        runTest {
            val mockResponse = WeMallShelfResponseModel(
                code = "10001",
                data = listOf(
                    WeMallShelfResponseData(
                        items = listOf(
                            WeMallShelfResponseItems()
                        )
                    )
                )
            )
            whenever(
                api.getWeMallShelfComponent(
                    token,
                    categoryName,
                    deviceId,
                    ssoId,
                    lang,
                    limit
                )
            ).thenReturn(mockResponse)
            weMallShelfRepository.getWeMallShelfComponent(token, request).collect { response ->
                assertEquals(response, mockResponse)
            }
        }

    @Test
    fun `when getWeMallShelfComponent success and code is not 10001 Then should return null`() =
        runTest {
            val mockResponse = WeMallShelfResponseModel(
                code = "9999",
                data = listOf(
                    WeMallShelfResponseData(
                        items = listOf(
                            WeMallShelfResponseItems()
                        )
                    )
                )
            )
            whenever(
                api.getWeMallShelfComponent(
                    token,
                    categoryName,
                    deviceId,
                    ssoId,
                    lang,
                    limit
                )
            ).thenReturn(mockResponse)
            weMallShelfRepository.getWeMallShelfComponent(token, request).collect { response ->
                assertNull(response)
            }
        }

    @Test
    fun `when getWeMallShelfComponent success and code is null Then should return null`() =
        runTest {
            val mockResponse = WeMallShelfResponseModel(
                code = null,
                data = listOf(
                    WeMallShelfResponseData(
                        items = listOf(
                            WeMallShelfResponseItems()
                        )
                    )
                )
            )
            whenever(
                api.getWeMallShelfComponent(
                    token,
                    categoryName,
                    deviceId,
                    ssoId,
                    lang,
                    limit
                )
            ).thenReturn(mockResponse)
            weMallShelfRepository.getWeMallShelfComponent(token, request).collect { response ->
                assertNull(response)
            }
        }

    @Test
    fun `when getWeMallShelfComponent success and code is 10001 but items are empty Then should return null`() =
        runTest {
            val mockResponse = WeMallShelfResponseModel(
                code = "10001",
                data = listOf(
                    WeMallShelfResponseData(
                        items = listOf()
                    )
                )
            )
            whenever(
                api.getWeMallShelfComponent(
                    token,
                    categoryName,
                    deviceId,
                    ssoId,
                    lang,
                    limit
                )
            ).thenReturn(mockResponse)
            weMallShelfRepository.getWeMallShelfComponent(token, request).collect { response ->
                assertNull(response)
            }
        }

    @Test
    fun `when getWeMallShelfComponent success and code is 10001 but items are null Then should return null`() =
        runTest {
            val mockResponse = WeMallShelfResponseModel(
                code = "10001",
                data = listOf(
                    WeMallShelfResponseData(
                        items = null
                    )
                )
            )
            whenever(
                api.getWeMallShelfComponent(
                    token,
                    categoryName,
                    deviceId,
                    ssoId,
                    lang,
                    limit
                )
            ).thenReturn(mockResponse)
            weMallShelfRepository.getWeMallShelfComponent(token, request).collect { response ->
                assertNull(response)
            }
        }
}
