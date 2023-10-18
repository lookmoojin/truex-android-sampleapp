package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.CreateFolderRequest
import com.truedigital.features.truecloudv3.data.model.CreateFolderResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import io.mockk.coEvery
import io.mockk.every
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

class CreateFolderRepositoryTest {
    private lateinit var createFolderRepository: CreateFolderRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk()

    @BeforeEach
    fun setup() {
        createFolderRepository = CreateFolderRepositoryImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository
        )
    }

    @Test
    fun testCreate() = runTest {
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
        val mockResponse = CreateFolderResponse(
            data = trueCloudV3StorageData,
            error = null,
            code = 12345,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )
        mockResponse.data = trueCloudV3StorageData
        val request = CreateFolderRequest(
            parentObjectId = "parentObjectId",
            name = "name"
        )

        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.createFolder(any(), any())
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = createFolderRepository.create("parent_id", "name")

        // assert
        flow.collectSafe {
            assertEquals(mockResponse, it)
            assertEquals(mockResponse.error, null)
            assertEquals(mockResponse.code, it.code)
            assertEquals(mockResponse.platformModule, it.platformModule)
            assertEquals(mockResponse.message, it.message)
            assertEquals(mockResponse.reportDashboard, it.reportDashboard)
            assertEquals(trueCloudV3StorageData.id, it.data?.id)
            assertEquals(trueCloudV3StorageData.name, it.data?.name)
            assertEquals(trueCloudV3StorageData.parentObjectId, it.data?.parentObjectId)
            assertEquals(trueCloudV3StorageData.objectType, it.data?.objectType)
            assertEquals(trueCloudV3StorageData.category, it.data?.category)
            assertEquals(trueCloudV3StorageData.coverImageKey, it.data?.coverImageKey)
            assertEquals(trueCloudV3StorageData.updatedAt, it.data?.updatedAt)
            assertEquals(trueCloudV3StorageData.mimeType, it.data?.mimeType)
            assertEquals(trueCloudV3StorageData.createdAt, it.data?.createdAt)
            assertEquals(trueCloudV3StorageData.size, it.data?.size)
            assertEquals(trueCloudV3StorageData.coverImageSize, it.data?.coverImageSize)
            assertEquals(request.name, "name")
            assertEquals(request.parentObjectId, "parentObjectId")
        }
    }

    @Test
    fun `test createFloder return error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.createFolder(
                ssoid = any(),
                request = any()
            )
        } returns Response.error(
            404,
            responseBody
        )

        // act
        val flow = createFolderRepository.create("parent_id", "name")

        // assert
        flow.catch { response ->
            assertEquals("Folder create failed", response.message)
        }.collect()
    }
}
