package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.GetSharedObjectAccessTokenRequestModel
import com.truedigital.features.truecloudv3.data.model.ShareObjectAccessToken
import com.truedigital.features.truecloudv3.data.model.SharedObjectAccessResponseModel
import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetSharedFileAccessTokenUseCaseTest {
    private var getSharedFileRepository: GetSharedFileRepository = mockk(relaxed = true)
    private lateinit var getSharedFileAccessTokenUseCase: GetSharedFileAccessTokenUseCase

    @BeforeEach
    fun setup() {
        getSharedFileAccessTokenUseCase = GetSharedFileAccessTokenUseCaseImpl(
            getSharedFileRepository = getSharedFileRepository
        )
    }

    @Test
    fun `test getSharedFileAccessToken success`() = runTest {
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
            getSharedFileRepository.getSharedObjectAccessToken(getSharedObjectAccessTokenRequestModel)
        } returns flowOf(mockResponse)

        // act
        val flow = getSharedFileAccessTokenUseCase
            .execute("id", "password")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse.accessToken?.sharedObjectAccessToken, response)
        }
    }
}
