package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.RenameRequest
import com.truedigital.features.truecloudv3.data.model.RenameStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RenameFileRepositoryTest {
    private lateinit var renameFileRepository: RenameFileRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        renameFileRepository = RenameFileRepositoryImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository
        )
    }
    @Test
    fun `test remane id return success`() = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        val mockResponse = RenameStorageResponse(
            data = trueCloudV3StorageData,
            error = null
        )
        coEvery {
            trueCloudV3Interface.renameObject(
                ssoid = any(),
                fileId = any(),
                name = RenameRequest("name")
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = renameFileRepository.rename("id_test", "name")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }
}
