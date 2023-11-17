package com.truedigital.features.truecloudv3.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.ObjectMetadata
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3UploadInterface
import com.truedigital.features.truecloudv3.data.model.InitUploadRequest
import com.truedigital.features.truecloudv3.data.model.InitialDataResponse
import com.truedigital.features.truecloudv3.data.model.InitialUploadResponse
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.util.BitmapUtil
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UploadFileRepositoryTest {
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider = mockk()
    private val trueCloudV3UploadInterface: TrueCloudV3UploadInterface = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val contextDataProvider: ContextDataProvider = mockk()
    private val sTSProvider: SecureTokenServiceProvider = mockk()
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private val trueCloudV3FileUtil: TrueCloudV3FileUtil = mockk()
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase = mockk()
    private val bitmapUtil: BitmapUtil = mockk()
    private val context: Context = mock()
    private val uri: Uri = mockk(relaxed = true)
    private val mockPathCache = "pathCache"
    private val mockPathFile = "pathFile"
    private val THUMBNAIL_QUALITY = 100
    private lateinit var uploadFileRepository: UploadFileRepository
    private lateinit var uploadFileRepositoryImpl: UploadFileRepositoryImpl

    @BeforeEach
    fun setup() {
        whenever(context.cacheDir).thenReturn(File(mockPathCache))
        whenever(context.filesDir).thenReturn(File(mockPathFile))
        every { getCurrentDateTimeUseCase.execute() } returns flowOf(10L)
        uploadFileRepository = UploadFileRepositoryImpl(
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            trueCloudV3UploadInterface = trueCloudV3UploadInterface,
            userRepository = userRepository,
            contextDataProvider = contextDataProvider,
            sTSProvider = sTSProvider,
            cacheUploadTaskRepository = cacheUploadTaskRepository,
            trueCloudV3FileUtil = trueCloudV3FileUtil,
            getCurrentDateTimeUseCase = getCurrentDateTimeUseCase,
            bitmapUtil = bitmapUtil
        )
        uploadFileRepositoryImpl = UploadFileRepositoryImpl(
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            trueCloudV3UploadInterface = trueCloudV3UploadInterface,
            userRepository = userRepository,
            contextDataProvider = contextDataProvider,
            sTSProvider = sTSProvider,
            cacheUploadTaskRepository = cacheUploadTaskRepository,
            trueCloudV3FileUtil = trueCloudV3FileUtil,
            getCurrentDateTimeUseCase = getCurrentDateTimeUseCase,
            bitmapUtil = bitmapUtil
        )
    }

    @Test
    fun `test uploadFileWithPath initUpload fail`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val mockFile = mockk<File>()
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        val request = InitUploadRequest(
            parentObjectId = "parentObjectId",
            name = "name",
            mimeType = "mimeType",
            size = 120
        )
        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.error(401, responseBody)
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"
        // act
        val uploadFile = uploadFileRepository.uploadFileWithPath(
            path = mockFile.path,
            folderId = "folder_id"
        )

        // assert
        uploadFile.catch { response ->
            assertNotNull(request.size)
            assertNotNull(request.name)
            assertNotNull(request.mimeType)
            assertNotNull(request.parentObjectId)
            assertEquals("InitUpload error", response.message)
        }.collect()
    }

    @Test
    fun `test uploadFileWithPath initUpload success`() = runTest {
        // arrange
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val dataResponse = InitialDataResponse()
        val initialUploadResponse = InitialUploadResponse(
            data = dataResponse,
            error = null
        )
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"

        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.success(initialUploadResponse)

        // act
        val uploadFile = uploadFileRepository.uploadFileWithPath(
            path = mockFile.path,
            folderId = "folder_id"
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.addUploadTask(any())
        }
    }

    @Test
    fun `test retryUpload success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_QUEUE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L,
            objectId = "1"
        )
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val dataResponse = InitialDataResponse()
        val transferObserver = mockk<TransferObserver>()
        val initialUploadResponse = InitialUploadResponse(
            data = dataResponse,
            error = null
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        coEvery {
            cacheUploadTaskRepository.getTaskByObjectId(any())
        } returns taskUploadModel
        every { contextDataProvider.getDataContext() } returns context

        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.updateTaskIdWithObjectId(any(), any())
        } returns Unit
        coEvery {
            transferUtility.upload(any(), any(), any<ObjectMetadata>())
        } returns transferObserver
        coEvery {
            transferObserver.id
        } returns 0
        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.success(initialUploadResponse)
        val slotListener = slot<TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TransferState.COMPLETED)
            slotListener.captured.onError(0, Exception())
            slotListener.captured.onProgressChanged(0, 0L, 100L)
            slotListener.captured.onStateChanged(0, null)
            slotListener.captured.onError(0, null)
        }
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"

        // act
        val uploadFile = uploadFileRepository.retryTask(
            objectId = "objectId"
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test replaceUpload success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val transferObserver = mockk<TransferObserver>()
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        every { contextDataProvider.getDataContext() } returns context

        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit
        coEvery {
            transferUtility.upload(any(), any(), any<ObjectMetadata>())
        } returns transferObserver
        coEvery {
            transferObserver.id
        } returns 0
        val slotListener = slot<TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TransferState.COMPLETED)
            slotListener.captured.onError(0, Exception())
            slotListener.captured.onProgressChanged(0, 0L, 100L)
            slotListener.captured.onStateChanged(0, null)
            slotListener.captured.onError(0, null)
        }
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"

        // act
        val uploadFile = uploadFileRepository.replaceFileWithPath(
            path = mockFile.path,
            objectId = "objectId"
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test uploadContactWithPath success`() = runTest {
        // arrange
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        every { contextDataProvider.getDataContext() } returns context
        every { MimeTypeMap.getFileExtensionFromUrl(uri.toString()) } returns "mock MimeType"
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"

        // act
        val uploadFile = uploadFileRepository.uploadContactWithPath(
            path = mockFile.path,
            folderId = "objectId",
            key = "key"
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
    }

    @Test
    fun `test thumbnail image`() = runTest {
        // arrange
        val transferUtility: TransferUtility = mockk(relaxed = true)

        val mockFile = mockk<File>()
        every { mockFile.length() } returns 500000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        val mockBitmap = mockk<Bitmap>()
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { bitmapUtil.createImageThumbnail(any()) } returns mockBitmap
        every { mockBitmap.compress(any(), any(), any()) } returns true
        every { mockBitmap.byteCount } returns 100
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        val fileMimeTypeManager: FileMimeTypeManager = mockk()
        every { fileMimeTypeManager.getMimeType(any()) } returns FileMimeType.IMAGE

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit

        // act
        val coverImageSize = uploadFileRepositoryImpl.uploadThumbnail(
            file = mockFile,
            id = "123",
            transferUtility = transferUtility,
            mimeType = FileMimeType.IMAGE
        )

        // assert
        assertEquals(100, coverImageSize)
    }

    @Test
    fun `test thumbnail video`() = runTest {
        // arrange
        val transferUtility: TransferUtility = mockk(relaxed = true)

        val mockFile = mockk<File>()
        every { mockFile.length() } returns 500000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        val mockBitmap = mockk<Bitmap>()
        mockkStatic(Uri::class)
        mockkStatic(BitmapUtil::class)
        every { bitmapUtil.createVideoThumbnail(any()) } returns mockBitmap
        every { mockBitmap.compress(any(), any(), any()) } returns true
        every { mockBitmap.byteCount } returns 100
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        val fileMimeTypeManager: FileMimeTypeManager = mockk()
        every { fileMimeTypeManager.getMimeType(any()) } returns FileMimeType.VIDEO

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit

        // act
        val coverImageSize = uploadFileRepositoryImpl.uploadThumbnail(
            file = mockFile,
            id = "123",
            transferUtility = transferUtility,
            mimeType = FileMimeType.VIDEO
        )

        // assert
        assertEquals(100, coverImageSize)
    }

    @Test
    fun `test thumbnail other`() = runTest {
        // arrange
        val transferUtility: TransferUtility = mockk(relaxed = true)

        val mockFile = mockk<File>()
        every { mockFile.length() } returns 500000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        // act
        val coverImageSize = uploadFileRepositoryImpl.uploadThumbnail(
            file = mockFile,
            id = "123",
            transferUtility = transferUtility,
            mimeType = FileMimeType.OTHER
        )

        // assert
        assertEquals(null, coverImageSize)
    }

    @Test
    fun `test thumbnail size 200k`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.WAITING,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L,
            objectId = "1"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)

        val mockFile = mockk<File>()
        every { mockFile.length() } returns 1000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        every { contextDataProvider.getDataContext() } returns context
        coEvery {
            cacheUploadTaskRepository.getTaskByObjectId(any())
        } returns taskUploadModel
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"

        val fileMimeTypeManager: FileMimeTypeManager = mockk()
        every { fileMimeTypeManager.getMimeType(any()) } returns FileMimeType.IMAGE

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit
        val bitmap = mockkClass(Bitmap::class)
        mockkStatic(ThumbnailUtils::class)
        coEvery {
            ThumbnailUtils.createImageThumbnail(
                mockFile,
                any(),
                any()
            )
        } returns bitmap
        coEvery {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                THUMBNAIL_QUALITY, any()
            )
        } returns true
        coEvery {
            bitmap.byteCount
        } returns 100

        // act
        val coverImageSize = uploadFileRepositoryImpl.uploadThumbnail(
            file = mockFile,
            id = "123",
            transferUtility = transferUtility,
            mimeType = FileMimeType.VIDEO
        )

        // assert
        assertEquals(0, coverImageSize)
    }

    @Test
    fun `test uploadContactWithPath success1`() = runTest {
        // arrange
        val transferUtility: TransferUtility = mockk(relaxed = true)

        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val mockFile = mockk<File>()
        every { mockFile.length() } returns 1000L
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        val fileMimeTypeManager: FileMimeTypeManager = mockk()
        every { fileMimeTypeManager.getMimeType(any()) } returns FileMimeType.IMAGE

        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit

        // act
        val flow = uploadFileRepositoryImpl.uploadContactWithPath(
            path = "path",
            folderId = "123",
            key = "key"
        )

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
        coVerify(exactly = 1) {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        }
    }

    @Test
    fun `test uploadMultiFileWithPath success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_QUEUE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L,
            objectId = "1"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val dataResponse = InitialDataResponse()
        val transferObserver = mockk<TransferObserver>()
        val initialUploadResponse = InitialUploadResponse(
            data = dataResponse,
            error = null
        )
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"
        coEvery {
            trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(contextDataProvider.getDataContext())
        } returns transferUtility
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.getTaskByObjectId(any())
        } returns taskUploadModel
        coEvery {
            cacheUploadTaskRepository.addUploadTask(taskUploadModel)
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.addUploadTaskList(any())
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.updateTaskIdWithObjectId(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"
        coEvery {
            transferUtility.upload(any(), any(), any<ObjectMetadata>())
        } returns transferObserver
        coEvery {
            transferObserver.id
        } returns 0
        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.success(initialUploadResponse)

        val slotListener = slot<TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TransferState.COMPLETED)
            slotListener.captured.onError(0, Exception())
            slotListener.captured.onProgressChanged(0, 0L, 100L)
        }

        // act
        val uploadFile = uploadFileRepository.uploadMultiFileWithPath(
            paths = listOf(mockFile.path),
            folderId = "folder_id",
            uploadType = TaskActionType.UPLOAD
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.addUploadTaskList(any())
        }
    }

    @Test
    fun `test uploadMultiFileWithPath getMimeType is null`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_QUEUE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L,
            objectId = "1"
        )
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val dataResponse = InitialDataResponse()
        val initialUploadResponse = InitialUploadResponse(
            data = dataResponse,
            error = null
        )
        val stsData = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            expiresAt = "expiresAt",
            endpoint = "endpoint"
        )
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            sTSProvider.getSTS()
        } returns flowOf(stsData)
        every { contextDataProvider.getDataContext() } returns context
        mockkStatic(MimeTypeMap::class)
        every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "mock MimeType"
        every { MimeTypeMap.getSingleton().getMimeTypeFromExtension(any()) } returns "mockMimeType"
        coEvery {
            trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(contextDataProvider.getDataContext())
        } returns transferUtility
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility
        coEvery {
            cacheUploadTaskRepository.getTaskByObjectId(any())
        } returns taskUploadModel
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.addUploadTaskList(any())
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.updateTaskIdWithObjectId(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns null

        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.success(initialUploadResponse)

        // act
        val uploadFile = uploadFileRepository.uploadMultiFileWithPath(
            paths = listOf(mockFile.path),
            folderId = "folder_id",
            uploadType = TaskActionType.UPLOAD
        )

        // assert
        uploadFile.collectSafe { response ->
            assertNotNull(response)
        }
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.addUploadTaskList(any())
        }
    }

    @Test
    fun `test uploadMultiFileWithPath case exceed limit`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_QUEUE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L,
            objectId = "1"
        )
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"
        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every { contextDataProvider.getContentResolver().getType(any()) } returns "image/jpg"
        coEvery {
            cacheUploadTaskRepository.getTaskByObjectId(any())
        } returns taskUploadModel
        every { contextDataProvider.getDataContext() } returns context
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit
        coEvery {
            trueCloudV3FileUtil.getMimeType(any(), any())
        } returns "MimeType"
        coEvery {
            trueCloudV3UploadInterface.initialUpload(
                ssoid = any(),
                request = any()
            )
        } returns Response.error(403, responseBody)

        // act
        val uploadFile = uploadFileRepository.uploadMultiFileWithPath(
            paths = listOf(mockFile.path),
            folderId = "folder_id",
            uploadType = TaskActionType.UPLOAD
        )

        // assert
        uploadFile.catch { e ->
            assertNotNull(e.message)
        }.collect()
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.addUploadTaskList(any())
        }
    }
}
