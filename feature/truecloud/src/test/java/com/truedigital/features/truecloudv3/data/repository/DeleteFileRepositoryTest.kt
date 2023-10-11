package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.DeleteStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.DeleteFileRepositoryImpl.Companion.ERROR_DELETE_FILE
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

class DeleteFileRepositoryTest {
    private lateinit var deleteFileRepository: DeleteFileRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk<UserRepository>(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteFileRepository = DeleteFileRepositoryImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository
        )
    }

    @Test
    fun `test delete id return success`() = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        val mockResponse = DeleteStorageResponse(
            data = trueCloudV3StorageData,
            error = null
        )
        coEvery {
            trueCloudV3Interface.deleteObject(
                ssoid = any(),
                objectId = any()
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = deleteFileRepository.delete("id_test")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test delete file error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.deleteObject(
                ssoid = any(),
                objectId = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = deleteFileRepository.delete("id_test")

        // assert
        flow.catch { response ->
            assertEquals(ERROR_DELETE_FILE, response.message)
        }.collect()
    }
}
