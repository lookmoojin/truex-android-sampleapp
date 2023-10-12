package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.ShareData
import com.truedigital.features.truecloudv3.data.model.ShareResponse
import com.truedigital.features.truecloudv3.data.repository.GetShareLinkRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetShareLinkUseCaseTest {
    private var getShareLinkRepository: GetShareLinkRepository = mockk(relaxed = true)
    private lateinit var getShareLinkUseCase: GetShareLinkUseCase

    @BeforeEach
    fun setup() {
        getShareLinkUseCase = GetShareLinkUseCaseImpl(
            getShareLinkRepository = getShareLinkRepository
        )
    }

    @Test
    fun `test GetShareLink usecase execute success`() = runTest {
        // arrange

        val shareData = ShareData(
            sharedUrl = "url_1"
        )
        val mockResponse = ShareResponse(
            data = shareData,
            error = null
        )
        coEvery {
            getShareLinkRepository.getShareLink(any())
        } returns flow {
            mockResponse
        }

        // act
        val flow = getShareLinkUseCase.execute(
            fileId = "fileId"
        )

        // assert
        flow.collectSafe { response ->
            assertEquals("url_1", response)
        }
    }
}
