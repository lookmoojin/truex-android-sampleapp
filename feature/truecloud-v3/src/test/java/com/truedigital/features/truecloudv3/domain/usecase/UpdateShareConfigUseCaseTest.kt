package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.ShareData
import com.truedigital.features.truecloudv3.data.model.ShareResponse
import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateShareConfigUseCaseTest {
    private var getShareLinkRepository: GetShareLinkRepository = mockk(relaxed = true)
    private lateinit var getShareConfigUseCase: GetShareConfigUseCase

    @BeforeEach
    fun setUp() {
        getShareConfigUseCase = GetShareConfigUseCaseImpl(getShareLinkRepository)
    }

    @Test
    fun `execute should return ShareConfigModel`() = runTest {
        // Mock the repository's behavior
        val fileId = "fileId"
        val shareResponse = ShareResponse(
            data = ShareData(
                sharedUrl = "https://example.com/shared",
            )
        )
        coEvery { getShareLinkRepository.getShareLink(fileId) } returns flowOf(shareResponse)

        // Call the execute method and collect the result
        val result = getShareConfigUseCase.execute(fileId)

        // Assert the result
        result.collectSafe { shareConfigModel ->
            assertEquals("123", shareConfigModel.sharedFile.id)
            assertEquals(true, shareConfigModel.sharedFile.isPrivate)
            assertEquals("2023-06-01", shareConfigModel.sharedFile.createdAt)
            assertEquals("", shareConfigModel.sharedFile.password)
            assertEquals("", shareConfigModel.sharedFile.expireAt)
            assertEquals("", shareConfigModel.sharedFile.updatedAt)
        }
    }
}
