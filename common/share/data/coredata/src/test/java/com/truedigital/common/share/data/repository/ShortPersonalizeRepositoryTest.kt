package com.truedigital.common.share.data.repository

import com.truedigital.common.share.data.coredata.data.api.SimilarApiInterface
import com.truedigital.common.share.data.coredata.data.exception.EmptyListError
import com.truedigital.common.share.data.coredata.data.model.PersonalizeSimilarResponse
import com.truedigital.common.share.data.coredata.data.repository.ShortPersonalizeRepository
import com.truedigital.common.share.data.coredata.data.repository.ShortPersonalizeRepositoryImpl
import com.truedigital.common.share.data.coredata.domain.model.SimilarRequestModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.extensions.collectSafe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals

class ShortPersonalizeRepositoryTest {
    private lateinit var shortPersonalizeRepository: ShortPersonalizeRepository
    private var deviceRepository: DeviceRepository = mockk()
    private var similarApiInterface: SimilarApiInterface = mockk()
    private var localizationRepository: LocalizationRepository = mockk()

    @BeforeEach
    fun setup() {
        shortPersonalizeRepository = ShortPersonalizeRepositoryImpl(
            deviceRepository = deviceRepository,
            similarApiInterface = similarApiInterface,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun test_getSimilarPersonalize_Success() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val mockResponse = Response.success(
            PersonalizeSimilarResponse().apply {
                this.responseData = PersonalizeSimilarResponse.Data().apply {
                    this.short = listOf(
                        PersonalizeSimilarResponse.SimilarResponse().apply {
                            this.id = "id"
                            this.navigate = "navigate"
                            this.countViews = 99
                            this.title = "title"
                            this.thumb = "thumb"
                        }
                    )
                }
            }
        )
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns mockResponse
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .collectSafe { shortPersonalizeData ->
                assertEquals(true, shortPersonalizeData.short?.isNotEmpty())
                assertEquals("id", shortPersonalizeData.short?.firstOrNull()?.id)
                assertEquals("navigate", shortPersonalizeData.short?.firstOrNull()?.navigate)
                assertEquals(99, shortPersonalizeData.short?.firstOrNull()?.countViews)
                assertEquals("title", shortPersonalizeData.short?.firstOrNull()?.title)
                assertEquals("thumb", shortPersonalizeData.short?.firstOrNull()?.thumb)
            }
    }

    @Test
    fun test_getSimilarPersonalize_similar_is_null() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val mockResponse = Response.success(
            PersonalizeSimilarResponse().apply {
                this.responseData = PersonalizeSimilarResponse.Data().apply {
                    this.short = null
                }
            }
        )
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns mockResponse
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .catch { exception ->
                assertEquals(EmptyListError().message, exception.message)
            }
    }

    @Test
    fun test_getSimilarPersonalize_similar_is_empty() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val mockResponse = Response.success(
            PersonalizeSimilarResponse().apply {
                this.responseData = PersonalizeSimilarResponse.Data().apply {
                    this.short = null
                }
            }
        )
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns mockResponse
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .catch { exception ->
                assertEquals(EmptyListError(), exception)
                assertEquals(EmptyListError().message, exception.message)
            }
    }

    @Test
    fun test_getSimilarPersonalize_similar_is_empty_but_have_schemaId() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val mockResponse = Response.success(
            PersonalizeSimilarResponse().apply {
                this.responseData = PersonalizeSimilarResponse.Data().apply {
                    this.short = null
                    this.schemaId = "schemaId"
                }
            }
        )
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns mockResponse
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .catch { exception ->
                assertEquals(EmptyListError(), exception)
                assertEquals(EmptyListError().message, exception.message)
            }
    }

    @Test
    fun test_getSimilarPersonalize_failure_404() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val mockResponse = "Not Found"
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns Response.error(
            404, mockResponse.toResponseBody()
        )
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .catch { exception ->
                assertEquals(EmptyListError(), exception)
                assertEquals(EmptyListError().message, exception.message)
            }
    }

    @Test
    fun test_getSimilarPersonalize_failure() = runTest {
        val similarRequestModel = SimilarRequestModel(
            limit = "10",
            placementId = "placementId"
        )
        val responseBody =
            "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCodeForEnTh() } returns "th"
        every { deviceRepository.getAndroidId() } returns "id"
        coEvery { similarApiInterface.getSimilarData(any()) } returns Response.error(
            400, responseBody
        )
        shortPersonalizeRepository.getShortData(similarRequestModel)
            .catch { exception ->
                assertEquals(EmptyListError(), exception)
                assertEquals(EmptyListError().message, exception.message)
            }
    }
}
