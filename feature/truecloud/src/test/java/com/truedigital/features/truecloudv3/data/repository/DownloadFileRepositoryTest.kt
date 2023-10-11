package com.truedigital.features.truecloudv3.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.data.model.InitialDataResponse
import com.truedigital.features.truecloudv3.data.model.InitialDownloadResponse
import com.truedigital.features.truecloudv3.data.model.S3Model
import com.truedigital.features.truecloudv3.data.model.SecureTokenService
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DownloadFileRepositoryTest {
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider = mockk()
    private val trueCloudV3DownloadInterface: TrueCloudV3DownloadInterface = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk(relaxed = true)
    private val sTSProvider: SecureTokenServiceProvider = mockk(relaxed = true)
    private val contextDataProvider: ContextDataProvider = mockk()
    private lateinit var downloadFileRepository: DownloadFileRepository
    private lateinit var downloadFileRepositoryImpl: DownloadFileRepositoryImpl
    private val context: Context = mockk()
    private val file: File = mockk(relaxed = true)
    private val uri: Uri = mockk(relaxed = true)
    private val mockPathCache = "pathCache"
    private val mockPathFile = "pathFile"

    @BeforeEach
    fun setup() {
        every { context.cacheDir } returns File(mockPathCache)
        every { context.filesDir } returns File(mockPathFile)
        every { contextDataProvider.getDataContext() } returns context
        every {
            contextDataProvider.getContentResolver().takePersistableUriPermission(any(), any())
        } just runs
        downloadFileRepository = DownloadFileRepositoryImpl(
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            trueCloudV3DownloadInterface = trueCloudV3DownloadInterface,
            userRepository = userRepository,
            contextDataProvider = contextDataProvider,
            sTSProvider = sTSProvider,
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
        downloadFileRepositoryImpl = DownloadFileRepositoryImpl(
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            trueCloudV3DownloadInterface = trueCloudV3DownloadInterface,
            userRepository = userRepository,
            contextDataProvider = contextDataProvider,
            sTSProvider = sTSProvider,
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test download initDownload fail`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.error(401, responseBody)

        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        // act
        val response = downloadFileRepository.download("", mockFile.path)

        // assert
        response.catch { error ->
            assertEquals("Init download error", error.message)
        }.collect()
    }

    @Test
    fun `test downloadContact case success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val data = InitialDataResponse()
        val initDownloadRes = InitialDownloadResponse(
            data = data
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.success(initDownloadRes)

        coEvery {
            sTSProvider.getSTS()
        } returns flow {
            stsData
        }
        val transferUtility: TransferUtility = mockk()
        coEvery {
            trueCloudV3TransferUtilityProvider.getTransferUtility(
                any(),
                contextDataProvider.getDataContext()
            )
        } returns transferUtility

        val transferObserver: TransferObserver = mockk()
        coEvery {
            transferObserver.setTransferListener(any())
        } returns Unit
        // act
        val response = downloadFileRepository.downloadContact("test_key", mockFile.path)

        // assert
        response.collectSafe {
            assertNotNull(it)
        }
    }

    @Test
    fun `test downloadForPreview case fail`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.error(401, responseBody)

        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        // act
        val response = downloadFileRepository.downloadForPreview("", mockFile.path)

        // assert
        response.catch { exception ->
            assertEquals("Init download error", exception.message)
        }.collect()
    }

    @Test
    fun `test downloadForPreview case success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val data = InitialDataResponse()
        val initDownloadRes = InitialDownloadResponse(
            data = data
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.success(initDownloadRes)

        coEvery {
            sTSProvider.getSTS()
        } returns flow {
            stsData
        }
        val transferUtility: TransferUtility = mockk()
        coEvery {
            trueCloudV3TransferUtilityProvider.getTransferUtility(
                any(),
                contextDataProvider.getDataContext()
            )
        } returns transferUtility

        val transferObserver: TransferObserver = mockk()
        coEvery {
            transferObserver.setTransferListener(any())
        } returns Unit

        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        // act
        val response = downloadFileRepository.downloadForPreview("", mockFile.path)

        // assert
        response.collectSafe { it ->
            verify(exactly = 1) {
                trueCloudV3TransferUtilityProvider.getTransferUtility(
                    any(),
                    contextDataProvider.getDataContext()
                )
            }
            assertNotNull(it)
        }
    }
    @Test
    fun `test downloadForShare case success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val data = InitialDataResponse()
        val initDownloadRes = InitialDownloadResponse(
            data = data
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.success(initDownloadRes)

        coEvery {
            sTSProvider.getSTS()
        } returns flow {
            stsData
        }
        val transferUtility: TransferUtility = mockk()
        coEvery {
            trueCloudV3TransferUtilityProvider.getTransferUtility(
                any(),
                contextDataProvider.getDataContext()
            )
        } returns transferUtility

        val transferObserver: TransferObserver = mockk()
        coEvery {
            transferObserver.setTransferListener(any())
        } returns Unit

        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        // act
        val response = downloadFileRepository.downloadForShare("", mockFile.path)

        // assert
        response.collectSafe { it ->
            verify(exactly = 1) {
                trueCloudV3TransferUtilityProvider.getTransferUtility(
                    any(),
                    contextDataProvider.getDataContext()
                )
            }
            assertNotNull(it)
        }
    }

    @Test
    fun `test downloadCoverImage case success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val secureTokenService = SecureTokenService(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt"
        )
        val data = InitialDataResponse(
            s3 = S3Model(
                bucket = "bucket",
                endpoint = "endpoint",
                secureTokenService = secureTokenService
            )
        )
        val initDownloadRes = InitialDownloadResponse(
            data = data
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"

        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any()
            )
        } returns Response.success(initDownloadRes)

        coEvery {
            sTSProvider.getSTS()
        } returns flow {
            mockk()
        }
        val transferUtility: TransferUtility = mockk()
        coEvery {
            trueCloudV3TransferUtilityProvider.getTransferUtility(
                any(),
                contextDataProvider.getDataContext()
            )
        } returns transferUtility

        val transferObserver: TransferObserver = mockk()
        coEvery {
            transferObserver.setTransferListener(any())
        } returns Unit
        coEvery {
            transferObserver.state == TransferState.COMPLETED
        } returns true

        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"

        // act
        val response = downloadFileRepository.downloadCoverImage("", mockFile.path)

        // assert
        assertNotNull(response)
        response.collectSafe {
            verify(exactly = 1) {
                trueCloudV3TransferUtilityProvider.getTransferUtility(
                    any(),
                    contextDataProvider.getDataContext()
                )
            }
        }
        assertEquals(data.s3?.bucket, "bucket")
        assertEquals(data.s3?.endpoint, "endpoint")
        assertEquals(data.s3?.secureTokenService?.accessToken, "accessToken")
        assertEquals(data.s3?.secureTokenService?.secretKey, "secretKey")
        assertEquals(data.s3?.secureTokenService?.expiresAt, "expiresAt")
        assertEquals(data.s3?.secureTokenService?.sessionKey, "sessionKey")
    }
}
