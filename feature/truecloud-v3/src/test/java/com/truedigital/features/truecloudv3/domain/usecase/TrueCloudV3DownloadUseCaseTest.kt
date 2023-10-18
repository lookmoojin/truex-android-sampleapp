package com.truedigital.features.truecloudv3.domain.usecase

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.nhaarman.mockitokotlin2.any
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.data.model.InitialDownloadResponse
import com.truedigital.features.truecloudv3.data.repository.DownloadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TrueCloudV3DownloadUseCaseTest {
    private val downloadFileRepository: DownloadFileRepository = mockk()
    lateinit var downloadUseCase: DownloadUseCase
    lateinit var downloadContactUseCase: DownloadContactUseCase
    lateinit var downloadCoverImageUseCase: DownloadCoverImageUseCase
    @BeforeEach
    fun setup() {
        downloadUseCase = DownloadUseCaseImpl(
            downloadFileRepository = downloadFileRepository
        )
        downloadContactUseCase = DownloadContactUseCaseImpl(
            downloadFileRepository = downloadFileRepository
        )
        downloadCoverImageUseCase = DownloadCoverImageUseCaseImpl(
            downloadFileRepository = downloadFileRepository
        )
    }

    @Test
    fun `test executeCoverImage success`() = runTest {
        // arrange
        val initialDownloadResponse = InitialDownloadResponse(
            data = any(),
            error = null
        )
        coEvery {
            downloadFileRepository.downloadCoverImage(any(), any())
        } answers {
            flow {
                initialDownloadResponse
            }
        }

        // act
        val flow = downloadCoverImageUseCase.execute("", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeContact success`() = runTest {
        // arrange
        val initialDownloadResponse = InitialDownloadResponse(
            data = any(),
            error = null
        )
        coEvery {
            downloadFileRepository.downloadContact(any(), any())
        } answers {
            flow {
                initialDownloadResponse
            }
        }

        // act
        val flow = downloadContactUseCase.execute("", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeForShare success`() = runTest {
        // arrange
        val mockTransferObserver = mockk<TransferObserver>()
        coEvery {
            downloadFileRepository.downloadForShare(any(), any())
        } returns flowOf(mockTransferObserver)

        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(mockTransferObserver)

        every {
            trueCloudV3TransferObserver.getId()
        } returns 1
        every {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        // act
        val flow = downloadUseCase.execute("", "", DownloadType.SHARE)

        // assert
        flow.collectSafe { response ->
            assertEquals(trueCloudV3TransferObserver.getId(), response.getId())
        }
    }

    @Test
    fun `test executeForNormal success`() = runTest {
        // arrange
        val mockTransferObserver = mockk<TransferObserver>()
        coEvery {
            downloadFileRepository.download(any(), any())
        } returns flowOf(mockTransferObserver)

        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(mockTransferObserver)

        every {
            trueCloudV3TransferObserver.getId()
        } returns 1
        every {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        // act
        val flow = downloadUseCase.execute("", "", DownloadType.NORMAL)

        // assert
        flow.collectSafe { response ->
            assertEquals(trueCloudV3TransferObserver.getId(), response.getId())
        }
    }
}
