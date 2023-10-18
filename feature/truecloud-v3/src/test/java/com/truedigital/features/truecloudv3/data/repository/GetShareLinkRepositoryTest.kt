package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ShareConfigResponse
import com.truedigital.features.truecloudv3.data.model.ShareData
import com.truedigital.features.truecloudv3.data.model.ShareResponse
import com.truedigital.features.truecloudv3.data.model.SharedFile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GetShareLinkRepositoryTest {
    private lateinit var getShareLinkRepository: GetShareLinkRepository
    private var userRepository = mockk<UserRepository>()
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()

    @BeforeEach
    fun setUp() {
        getShareLinkRepository = GetShareLinkRepositoryImpl(
            trueCloudV3Interface,
            userRepository
        )
        coEvery { userRepository.getSsoId() }.returns("ssoid")
    }

    @Test
    fun `test getShareLink isSuccessful`() = runTest {

        val shareData = ShareData(
            sharedUrl = "url_1"
        )
        val mockResponse = ShareResponse(
            data = shareData,
            error = null,
        )
        mockResponse.data = shareData
        coEvery {
            trueCloudV3Interface.getShareLink(
                ssoid = "ssoid",
                fileid = "fileId"
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getShareLinkRepository.getShareLink("fileId")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getShareLink error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getShareLink(
                ssoid = "ssoid",
                fileid = "id"
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getShareLinkRepository.getShareLink("")

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
    @Test
    fun `test getShareConfig isSuccessful`() = runTest {
        val sharedFile = SharedFile(
            id = "id"
        )
        val mockResponse = ShareConfigResponse(
            data = sharedFile,
            error = null,
        )
        coEvery {
            trueCloudV3Interface.getShareConfig(
                ssoid = "ssoid",
                fileid = "fileId"
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getShareLinkRepository.getShareConfig("fileId")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getShareConfig error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getShareConfig(
                ssoid = "ssoid",
                fileid = "id"
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getShareLinkRepository.getShareLink("")

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
    @Test
    fun `test updateShareConfig isSuccessful`() = runTest {

        val sharedFile = SharedFile(
            id = "id"
        )
        val mockResponse = ShareConfigResponse(
            data = sharedFile,
            error = null,
        )
        coEvery {
            trueCloudV3Interface.updateShareConfig(
                ssoid = "ssoid",
                fileid = "fileId",
                obj = any()
            )
        } returns Response.success(
            mockResponse
        )
        // act
        val flow = getShareLinkRepository.updateShareConfig("fileId", true, "xxx", "ccc", true)

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test updateShareConfig error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3Interface.updateShareConfig(
                ssoid = "ssoid",
                fileid = "id",
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getShareLinkRepository.updateShareConfig("", true, "xxx", "ccc", true)

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
}
