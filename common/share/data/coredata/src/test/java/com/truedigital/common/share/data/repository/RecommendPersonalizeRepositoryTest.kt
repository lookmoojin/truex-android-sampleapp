package com.truedigital.common.share.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.api.PersonalizeApiInterface
import com.truedigital.common.share.data.coredata.data.repository.RecommendPersonalizeRepository
import com.truedigital.common.share.data.coredata.data.repository.RecommendPersonalizeRepositoryImpl
import com.truedigital.common.share.datalegacy.data.recommend.model.request.RecommendedDataRequest
import com.truedigital.common.share.datalegacy.data.recommend.model.response.RecommendedItems
import com.truedigital.common.share.datalegacy.data.recommend.model.response.RecommendedResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals

class RecommendPersonalizeRepositoryTest {
    private val api: PersonalizeApiInterface = mock()
    private lateinit var recommendPersonalizeRepository: RecommendPersonalizeRepository

    @BeforeEach
    fun setup() {
        recommendPersonalizeRepository = RecommendPersonalizeRepositoryImpl(api)
    }

    @Test
    fun `when getShelfRecommendList success Then should return data`() = runTest {
        val contentType = "movie"
        val request = RecommendedDataRequest().apply {
            deviceId = "1234"
            ssoId = "4567"
            maxItems = "15"
            language = "th"
        }

        val mockResponseData = Response.success(
            RecommendedResponse().apply {
                items = listOf(
                    RecommendedItems().apply {
                        name = "Title Movie"
                    }
                )
            }
        )
        whenever(
            api.getRecommendedShelf(
                contentType = contentType,
                deviceId = request.deviceId ?: "",
                ssoId = request.ssoId ?: "",
                maxItems = request.maxItems ?: "",
                language = request.language ?: "",
                isVodLayer = request.isVodLayer
            )
        ).thenReturn(mockResponseData)

        recommendPersonalizeRepository.getShelfRecommendList(request, contentType)
            .collect { response ->
                assertEquals(response, mockResponseData.body())
            }
    }

    @Test
    fun `when getShelfRecommendList item is empty Then should throw error`() = runTest {
        val contentType = "movie"
        val request = RecommendedDataRequest().apply {
            deviceId = "1234"
            ssoId = "4567"
            maxItems = "15"
            language = "th"
        }

        val mockResponseData = Response.success(
            RecommendedResponse().apply {
                message = "success"
                items = listOf()
            }
        )
        whenever(
            api.getRecommendedShelf(
                contentType = contentType,
                deviceId = request.deviceId ?: "",
                ssoId = request.ssoId ?: "",
                maxItems = request.maxItems ?: "",
                language = request.language ?: "",
                isVodLayer = request.isVodLayer
            )
        ).thenReturn(mockResponseData)

        recommendPersonalizeRepository.getShelfRecommendList(request, contentType)
            .catch { exception ->
                assertEquals(exception.message, "failed to load shelf list")
            }
            .collect()
    }
}
