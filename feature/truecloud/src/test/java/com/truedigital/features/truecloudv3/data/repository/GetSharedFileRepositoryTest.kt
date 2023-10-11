package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3GetSharedFileInterface
import com.truedigital.features.truecloudv3.data.model.GetSharedFileResponseModel
import com.truedigital.features.truecloudv3.data.model.GetSharedObjectAccessTokenRequestModel
import com.truedigital.features.truecloudv3.data.model.ShareObjectAccessToken
import com.truedigital.features.truecloudv3.data.model.SharedFileData
import com.truedigital.features.truecloudv3.data.model.SharedFileObject
import com.truedigital.features.truecloudv3.data.model.SharedObjectAccessResponseModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetSharedFileRepositoryTest {
    private lateinit var getSharedFileRepository: GetSharedFileRepository
    private val trueCloudV3GetSharedFileInterface: TrueCloudV3GetSharedFileInterface = mockk()

    @BeforeEach
    fun setup() {
        getSharedFileRepository = GetSharedFileRepositoryImpl(
            trueCloudV3GetSharedFileInterface
        )
    }

    @Test
    fun `test getPublicSharedObject success`() = runTest {

        // arrange
        val sharedFileObject = SharedFileObject(
            id = "id"
        )

        val sharedFileData = SharedFileData(
            fileObject = sharedFileObject
        )

        val mockResponse = GetSharedFileResponseModel(
            data = sharedFileData,
        )
        mockResponse.data = sharedFileData
        coEvery {
            trueCloudV3GetSharedFileInterface.getPublicSharedFile(
                encryptedSharedObjectId = "id"
            )
        } returns Response.success(200, mockResponse)

        // act
        val flow = getSharedFileRepository.getPublicSharedObject("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getPublicSharedObject file is private`() = runTest {

        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3GetSharedFileInterface.getPublicSharedFile(
                encryptedSharedObjectId = "id"
            )
        } returns Response.error(401, responseBody)

        // act
        val flow = getSharedFileRepository.getPublicSharedObject("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(401, response.statusCode)
        }
    }

    @Test
    fun `test getPublicSharedObject file is expired`() = runTest {

        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3GetSharedFileInterface.getPublicSharedFile(
                encryptedSharedObjectId = "id"
            )
        } returns Response.error(403, responseBody)

        // act
        val flow = getSharedFileRepository.getPublicSharedObject("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(403, response.statusCode)
        }
    }

    @Test
    fun `test getPublicSharedObject file is error`() = runTest {

        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3GetSharedFileInterface.getPublicSharedFile(
                encryptedSharedObjectId = "id"
            )
        } returns Response.error(500, responseBody)

        // act
        val flow = getSharedFileRepository.getPublicSharedObject("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(500, response.statusCode)
        }
    }

    @Test
    fun `test getPrivateSharedObject success`() = runTest {

        // arrange
        val mockResponse = GetSharedFileResponseModel(
            data = SharedFileData(
                fileObject = SharedFileObject(
                    id = "id"
                )
            )
        )
        coEvery {
            trueCloudV3GetSharedFileInterface.getPrivateSharedFile(any(), any())
        } returns Response.success(200, mockResponse)

        // act
        val flow = getSharedFileRepository
            .getPrivateSharedObject("", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun `test getSharedObjectAccessToken isSuccessful`() = runTest {

        // arrange
        val getSharedObjectAccessTokenRequestModel = GetSharedObjectAccessTokenRequestModel(
            encryptedSharedObjectId = "id",
            password = "password"
        )
        val sharedObjectAccessToken = ShareObjectAccessToken(
            sharedObjectAccessToken = "token"
        )
        val mockResponse = SharedObjectAccessResponseModel(
            accessToken = sharedObjectAccessToken
        )
        coEvery {
            trueCloudV3GetSharedFileInterface.getSharedObjectAccessToken(
                getSharedObjectAccessTokenRequestModel
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow =
            getSharedFileRepository.getSharedObjectAccessToken(getSharedObjectAccessTokenRequestModel)
        // assert
        flow.collectSafe { response ->
            assertNotNull(response?.accessToken)
        }
    }
}
