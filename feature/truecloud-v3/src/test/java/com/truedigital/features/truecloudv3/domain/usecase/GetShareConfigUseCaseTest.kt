package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.ShareConfigResponse
import com.truedigital.features.truecloudv3.data.model.SharedFile
import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetShareConfigUseCaseTest {

    private var getShareLinkRepository: GetShareLinkRepository = mockk()
    private lateinit var updateShareConfigUseCase: UpdateShareConfigUseCase

    @BeforeEach
    fun setUp() {
        getShareLinkRepository = mockk()
        updateShareConfigUseCase = UpdateShareConfigUseCaseImpl(getShareLinkRepository)
    }

    @Test
    fun `execute should return ShareConfigModel`() = runTest {
        // Mock the repository's behavior
        val fileId = "fileId"
        val isPrivate = true
        val password = "password"
        val expireAt = "2023-06-30"
        val isNewPass = true
        val shareConfigResponse = ShareConfigResponse(
            data = SharedFile(
                id = "123",
                isPrivate = true,
                createdAt = "2023-06-01",
                password = "",
                expireAt = ""
            )
        )
        coEvery {
            getShareLinkRepository.updateShareConfig(
                fileid = fileId,
                isPrivate = isPrivate,
                password = password,
                expireAt = expireAt,
                isNewPass = isNewPass
            )
        } returns flowOf(shareConfigResponse)

        // Call the execute method and collect the result
        val result = updateShareConfigUseCase.execute(
            fileId,
            isPrivate,
            password,
            expireAt,
            isNewPass
        )

        // Assert the result
        result.collectSafe { shareConfigModel ->
            assertEquals("123", shareConfigModel.sharedFile.id)
        }
    }
}
