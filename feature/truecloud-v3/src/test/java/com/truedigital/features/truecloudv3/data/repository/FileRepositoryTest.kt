package com.truedigital.features.truecloudv3.data.repository

import com.google.gson.JsonObject
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ObjectsRequestModel
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
import kotlin.test.assertNotNull

class FileRepositoryTest {
    private var userRepository: UserRepository = mockk()
    private lateinit var fileRepository: FileRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()

    @BeforeEach
    fun setUp() {
        fileRepository = FileRepositoryImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository
        )
    }

    @Test
    fun `test locateFile copy isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 201
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.moveObject(
                ssoid = any(),
                type = any(),
                obj = any()
            )
        } returns Response.success(201, JsonObject())

        // act
        val flow = fileRepository.locateFile("fileId", arrayListOf("data"), "copy")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }
    @Test
    fun `test locateFile move isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 200
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.moveObject(
                ssoid = any(),
                type = any(),
                obj = any()
            )
        } returns Response.success(
            responseBody
        )
        // act
        val flow = fileRepository.locateFile("fileId", arrayListOf("data"), "move")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }

    @Test
    fun `test getShareLink error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        val data = ObjectsRequestModel(
            parentObjectId = "1111",
            objectIds = arrayListOf("data")
        )
        coEvery {
            trueCloudV3Interface.moveObject(
                ssoid = "ssoid",
                type = "move",
                obj = data
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = fileRepository.locateFile("fileId", arrayListOf("data"), "")

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }

    @Test
    fun `test moveToTrash isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 200
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.moveToTrash(
                ssoid = any(),
                obj = any()
            )
        } returns Response.success(
            responseBody
        )
        // act
        val flow = fileRepository.moveToTrash(arrayListOf("data"))

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }
    @Test
    fun `test moveToTrash error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.moveToTrash(
                ssoid = any(),
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = fileRepository.moveToTrash(arrayListOf("data"))

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
}
