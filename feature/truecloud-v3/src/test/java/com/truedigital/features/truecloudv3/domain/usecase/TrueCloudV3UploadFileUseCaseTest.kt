package com.truedigital.features.truecloudv3.domain.usecase

import android.content.Context
import android.net.Uri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.nhaarman.mockitokotlin2.any
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtil
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull

class TrueCloudV3UploadFileUseCaseTest {
    private val uploadFileRepository: UploadFileRepository = mockk()
    private val contextDataProvider: ContextDataProvider = mockk()
    private val trueCloudV3FileUtil: TrueCloudV3FileUtil = mockk()
    lateinit var uploadFileUseCase: UploadFileUseCase
    lateinit var uploadContactUseCase: UploadContactUseCase
    lateinit var uploadFileWithPathUseCase: UploadFileWithPathUseCase

    @BeforeEach
    fun setup() {
        uploadFileUseCase = UploadFileUseCaseImpl(
            uploadFileRepository = uploadFileRepository,
            contextDataProvider = contextDataProvider,
            trueCloudV3FileUtil = trueCloudV3FileUtil
        )
        uploadContactUseCase = UploadContactUseCaseImpl(
            uploadFileRepository = uploadFileRepository,
            contextDataProvider = contextDataProvider
        )
        uploadFileWithPathUseCase = UploadFileWithPathUseCaseImpl(
            uploadFileRepository = uploadFileRepository
        )
    }

    @Test
    fun `test executeContactWithPath success`() = runTest {
        // arrange
        val model1 = ContactTrueCloudModel(
            firstName = "123"
        )
        val model2 = ContactTrueCloudModel(
            firstName = "abc"
        )
        val context = mockk<Context>()
        val mockFile = mockk<File>()
        coEvery {
            uploadFileRepository.uploadContactWithPath(any(), any(), any())
        } answers {
            flow {
                any()
            }
        }

        every { contextDataProvider.getDataContext() } returns context
        every { context.cacheDir } returns mockFile
        every { mockFile.absolutePath } returns ""

        // act
        val flow = uploadContactUseCase.execute(listOf(model1, model2), "", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeUploadFileUseCase success`() = runTest {
        // arrange
        val uri = mockk<Uri>(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        val path = "FilePath"
        val transferObserver: TransferObserver = mockk()

        coEvery {
            trueCloudV3FileUtil.getPathFromUri(any(), any())
        } returns path

        coEvery {
            trueCloudV3FileUtil.readContentToFile(any(), any())
        } returns mockFile

        coEvery {
            uploadFileRepository.uploadMultiFileWithPath(any(), any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadFileUseCase.execute(mutableListOf(uri), "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeContactWithPath success path == null`() = runTest {
        // arrange
        val uri = mockk<Uri>(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        val path = null
        val transferObserver: TransferObserver = mockk()

        coEvery {
            trueCloudV3FileUtil.getPathFromUri(any(), any())
        } returns path

        coEvery {
            trueCloudV3FileUtil.readContentToFile(any(), any())
        } returns mockFile

        coEvery {
            uploadFileRepository.uploadMultiFileWithPath(any(), any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadFileUseCase.execute(mutableListOf(uri), "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeContactWithPath success length == 0`() = runTest {
        // arrange
        val uri = mockk<Uri>(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        val path = ""
        val transferObserver: TransferObserver = mockk()

        coEvery {
            trueCloudV3FileUtil.getPathFromUri(any(), any())
        } returns path

        coEvery {
            trueCloudV3FileUtil.readContentToFile(any(), any())
        } returns mockFile

        every {
            mockFile.length()
        } returns 0

        coEvery {
            uploadFileRepository.uploadMultiFileWithPath(any(), any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadFileUseCase.execute(mutableListOf(uri), "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeContactWithPath readContentToFile null`() = runTest {
        // arrange
        val uri = mockk<Uri>(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        val path = ""
        val transferObserver: TransferObserver = mockk()

        coEvery {
            trueCloudV3FileUtil.getPathFromUri(any(), any())
        } returns path

        coEvery {
            trueCloudV3FileUtil.readContentToFile(any(), any())
        } returns null

        coEvery {
            uploadFileRepository.uploadMultiFileWithPath(any(), any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadFileUseCase.execute(mutableListOf(uri), "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test executeUploadFileWithPathUseCase `() = runTest {
        // arrange
        val transferObserver: TransferObserver = mockk()

        coEvery {
            uploadFileRepository.uploadFileWithPath(any(), any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadFileWithPathUseCase.execute("path", "")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }
}
