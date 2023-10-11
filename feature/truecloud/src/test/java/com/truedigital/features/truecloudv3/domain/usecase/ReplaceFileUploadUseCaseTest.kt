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

class ReplaceFileUploadUseCaseTest {
    private val uploadFileRepository: UploadFileRepository = mockk()
    private lateinit var replaceFileUploadUseCase: ReplaceFileUploadUseCase

    @BeforeEach
    fun setUp() {
        replaceFileUploadUseCase = ReplaceFileUploadUseCaseImpl(
            uploadFileRepository = uploadFileRepository,
        )
    }

    @Test
    fun `test replaceFileUpload`() = runTest {
        // arrange
        val transferObserver: TransferObserver = mockk()

        coEvery {
            uploadFileRepository.replaceFileWithPath(any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }

        // act
        val flow = replaceFileUploadUseCase.execute("path", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }
}
