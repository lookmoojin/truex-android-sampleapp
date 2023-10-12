package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GetProfileSettingsUseCaseTest {

    private val mockRepository: ProfileRepository = mockk(relaxed = true)

    private val useCase: GetProfileSettingsUseCase = GetProfileSettingsUseCaseImpl(mockRepository)

    @Test
    fun `execute() returns expected data`() {
        // Given
        val expectedData = ProfileData()
        val responseSuccess = Success(expectedData)
        coEvery { mockRepository.getProfile() } returns flow {
            emit(responseSuccess)
        }

        // When
        val result = runBlocking(Dispatchers.IO) { useCase.execute().single() }

        // Then
        assertEquals(responseSuccess, result)
        coVerify { mockRepository.getProfile() }
    }

    @Test
    fun `execute() returns expected failure`() {
        // Given
        val errorMessage = "failed to get profile setting"
        val throwsException = Exception(errorMessage)
        coEvery { mockRepository.getProfile() } throws throwsException

        // When
        val result = runBlocking(Dispatchers.IO) { useCase.execute().single() }

        // Then
        assertEquals(errorMessage, (result as Failure).exception.message)
        coVerify { mockRepository.getProfile() }
    }
}
