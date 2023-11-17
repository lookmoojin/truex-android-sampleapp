package com.truedigital.features.truecloudv3.domain.usecase

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TrueCloudV3RetryUploadUseCaseTest {
    private var uploadFileRepository: UploadFileRepository = mockk(relaxed = true)
    private lateinit var retryUploadUseCase: RetryUploadUseCase

    @BeforeEach
    fun setup() {
        retryUploadUseCase = RetryUploadUseCaseImpl(
            uploadFileRepository = uploadFileRepository
        )
    }

    @Test
    fun `test retry usecase execute success`() = runTest {
        // arrange
        val transferObserver: TransferObserver = mockk()
        coEvery {
            uploadFileRepository.retryTask(any())
        } returns flow {
            emit(transferObserver)
        }
        // act
        val flow = retryUploadUseCase.execute("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }
}
