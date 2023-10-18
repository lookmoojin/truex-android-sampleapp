package com.truedigital.features.truecloudv3.data.repository

import com.google.gson.JsonObject
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
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

class TrashRepositoryTest {
    private var userRepository: UserRepository = mockk()
    private lateinit var trashRepository: TrashRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()

    @BeforeEach
    fun setUp() {
        trashRepository = TrashRepositoryImpl(
            userRepository = userRepository,
            trueCloudV3Interface = trueCloudV3Interface
        )
    }

    @Test
    fun `test restoreFile isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 201
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.restoreTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.success(201, JsonObject())

        // act
        val flow = trashRepository.restoreFile(arrayListOf("id"))

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }

    @Test
    fun `test restoreFile error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.restoreTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = trashRepository.restoreFile(arrayListOf("data"))

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }

    @Test
    fun `test deleteTrashFile isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 201
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.deleteTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.success(201, JsonObject())

        // act
        val flow = trashRepository.deleteTrashFile(arrayListOf("id"))

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }

    @Test
    fun `test deleteTrashFile error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.deleteTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = trashRepository.deleteTrashFile(arrayListOf("data"))

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }

    @Test
    fun `test emptyTrashFile isSuccessful`() = runTest {
        val responseBody = JsonObject()
        responseBody.addProperty("id", 1)
        val status = 201
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.emptyTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.success(201, JsonObject())

        // act
        val flow = trashRepository.emptyTrash(arrayListOf("id"))

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(status, response)
        }
    }

    @Test
    fun `test emptyTrashFile error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        every { userRepository.getSsoId() } returns "ssoid"
        coEvery {
            trueCloudV3Interface.emptyTrashData(
                ssoid = any(),
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = trashRepository.emptyTrash(arrayListOf("data"))

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
}
