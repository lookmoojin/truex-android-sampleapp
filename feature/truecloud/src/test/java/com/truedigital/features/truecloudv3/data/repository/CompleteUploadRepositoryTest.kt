package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3UploadInterface
import com.truedigital.features.truecloudv3.data.model.CompleteUploadRequest
import com.truedigital.features.truecloudv3.data.model.CompleteUploadResponse
import com.truedigital.features.truecloudv3.data.model.ReplaceUploadRequest
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal interface CompleteUploadRepositoryTestCase {
    fun `test callComplete return success`(value: Int)
    fun `test callReplaceComplete return success`(value: Int)
}

internal class CompleteUploadRepositoryTest : CompleteUploadRepositoryTestCase {
    private lateinit var completeUploadRepository: CompleteUploadRepository
    private val trueCloudV3UploadInterface: TrueCloudV3UploadInterface = mockk()
    private val userRepository: UserRepository = mockk<UserRepository>(relaxed = true)

    @BeforeEach
    fun setup() {
        completeUploadRepository = CompleteUploadRepositoryImpl(
            trueCloudV3UploadInterface,
            userRepository
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    override fun `test callComplete return success`(value: Int) = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        trueCloudV3StorageData.id = "id"
        trueCloudV3StorageData.name = "name"
        trueCloudV3StorageData.parentObjectId = "parentObjectId"
        trueCloudV3StorageData.objectType = "objectType"
        trueCloudV3StorageData.category = "category"
        trueCloudV3StorageData.coverImageKey = "coverImageKey"
        trueCloudV3StorageData.updatedAt = "updatedAt"
        trueCloudV3StorageData.mimeType = "mimeType"
        trueCloudV3StorageData.createdAt = "createdAt"
        trueCloudV3StorageData.size = "size"
        trueCloudV3StorageData.coverImageSize = "coverImageSize"

        val mockResponse = CompleteUploadResponse(
            data = trueCloudV3StorageData,
            error = null,
            code = 12345,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )
        val request = CompleteUploadRequest(
            status = "SUCCESS",
            coverImageKey = "-cover-image",
            coverImageSize = 0
        )
        coEvery {
            trueCloudV3UploadInterface.completeUpload(
                ssoid = any(),
                id = any(),
                request = any()
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = completeUploadRepository.callComplete(
            id = "id",
            coverImageSize = value
        )

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
            assertEquals(mockResponse.code, response.code)
            assertEquals(mockResponse.platformModule, response.platformModule)
            assertEquals(mockResponse.message, response.message)
            assertEquals(mockResponse.reportDashboard, response.reportDashboard)
            assertNotNull(request.status)
            assertNotNull(request.coverImageKey)
            assertNotNull(request.coverImageSize)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `test callComplete return error`(value: Int) = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3UploadInterface.completeUpload(
                ssoid = any(),
                id = any(),
                request = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = completeUploadRepository.callComplete(
            id = "id",
            coverImageSize = value
        )

        // assert
        flow.catch { exception ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD, exception.message)
        }.collect()
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    override fun `test callReplaceComplete return success`(value: Int) = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        trueCloudV3StorageData.id = "id"
        trueCloudV3StorageData.name = "name"
        trueCloudV3StorageData.parentObjectId = "parentObjectId"
        trueCloudV3StorageData.objectType = "objectType"
        trueCloudV3StorageData.category = "category"
        trueCloudV3StorageData.coverImageKey = "coverImageKey"
        trueCloudV3StorageData.updatedAt = "updatedAt"
        trueCloudV3StorageData.mimeType = "mimeType"
        trueCloudV3StorageData.createdAt = "createdAt"
        trueCloudV3StorageData.size = "size"
        trueCloudV3StorageData.coverImageSize = "coverImageSize"

        val mockResponse = CompleteUploadResponse(
            data = trueCloudV3StorageData,
            error = null,
            code = 12345,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )
        val request = ReplaceUploadRequest(
            status = "SUCCESS",
            coverImageKey = "-cover-image",
            coverImageSize = 0
        )
        coEvery {
            trueCloudV3UploadInterface.completeReplaceUpload(
                ssoid = any(),
                id = any(),
                request = any()
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = completeUploadRepository.callReplaceComplete(
            id = "id",
            coverImageSize = value
        )

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
            assertEquals(mockResponse.code, response.code)
            assertEquals(mockResponse.platformModule, response.platformModule)
            assertEquals(mockResponse.message, response.message)
            assertEquals(mockResponse.reportDashboard, response.reportDashboard)
            assertNotNull(request.status)
            assertNotNull(request.coverImageKey)
            assertNotNull(request.coverImageSize)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `test callReplaceComplete return error`(value: Int) = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3UploadInterface.completeReplaceUpload(
                ssoid = any(),
                id = any(),
                request = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = completeUploadRepository.callReplaceComplete(
            id = "id",
            coverImageSize = value
        )

        // assert
        flow.catch { exception ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD, exception.message)
        }.collect()
    }
}
