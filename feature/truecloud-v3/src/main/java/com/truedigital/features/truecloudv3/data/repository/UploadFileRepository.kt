package com.truedigital.features.truecloudv3.data.repository

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.util.Mimetypes
import com.google.ads.interactivemedia.v3.internal.it
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3UploadInterface
import com.truedigital.features.truecloudv3.data.model.InitUploadRequest
import com.truedigital.features.truecloudv3.data.model.InitialUploadResponse
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.extension.formatBinarySize
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.util.BitmapUtil
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import javax.inject.Inject

interface UploadFileRepository {
    fun uploadMultiFileWithPath(
        paths: List<String>,
        folderId: String,
        uploadType: TaskActionType
    ): Flow<TransferObserver>

    fun uploadMultiFileWithTask(
        tasks: List<TaskUploadModel>
    ): Flow<TransferObserver>

    fun uploadFileWithPath(path: String, folderId: String?): Flow<TransferObserver>
    fun uploadContactWithPath(path: String, folderId: String?, key: String): Flow<TransferObserver>
    fun retryTask(objectId: String): Flow<TransferObserver>
    fun replaceFileWithPath(path: String, objectId: String): Flow<TransferObserver>
}

class UploadFileRepositoryImpl @Inject constructor(
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider,
    private val trueCloudV3UploadInterface: TrueCloudV3UploadInterface,
    private val userRepository: UserRepository,
    private val contextDataProvider: ContextDataProvider,
    private val sTSProvider: SecureTokenServiceProvider,
    private val cacheUploadTaskRepository: CacheUploadTaskRepository,
    private val trueCloudV3FileUtil: TrueCloudV3FileUtil,
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase,
    private val bitmapUtil: BitmapUtil
) : UploadFileRepository {

    companion object {
        const val KEY_COVER_IMAGE_SUFFIX = "-cover-image"
        private const val COVER_SIZE_ZERO = 0
        private const val DEFAULT_TASK_ID = 0
        private const val THUMBNAIL_QUALITY = 100
        private const val MINIMUM_SIZE = 200000
        private const val EMPTY_STRING = ""
        private const val ERROR_CODE_EXCEED_LIMIT = 403
        private const val DEFAULT_UPLOAD_DELAY = 300L
        private const val ERROR_INIT_UPLOAD_DATA = "InitUpload error"
        const val ERROR_INIT_UPLOAD_DATA_EXCEED_LIMIT = "Exceed limit error"
    }

    override fun uploadMultiFileWithPath(
        paths: List<String>,
        folderId: String,
        uploadType: TaskActionType
    ): Flow<TransferObserver> {
        return flow<TransferObserver> {
            val taskList = mutableListOf<TaskUploadModel>()
            for (path in paths) {
                val file = File(path)
                val mimeTypeStr = trueCloudV3FileUtil.getMimeType(
                    file.toUri(),
                    contextDataProvider.getContentResolver()
                ).orEmpty()
                val mimeType = FileMimeTypeManager.getMimeType(mimeTypeStr)
                val task = TaskUploadModel(
                    id = DEFAULT_TASK_ID,
                    path = path,
                    status = TaskStatusType.IN_QUEUE,
                    name = file.name,
                    size = file.length()
                        .formatBinarySize(contextDataProvider.getDataContext()),
                    coverImageSize = COVER_SIZE_ZERO,
                    type = mimeType,
                    objectId = "${UUID.randomUUID()}+UUID",
                    updateAt = getCurrentDateTimeUseCase.execute().firstOrNull() ?: 0,
                    folderId = folderId,
                    actionType = uploadType
                )
                taskList.add(task)
            }
            cacheUploadTaskRepository.addUploadTaskList(taskList)
            emitAll(uploadMultiFileWithTask(taskList))
        }
    }

    override fun uploadFileWithPath(path: String, folderId: String?): Flow<TransferObserver> {
        return initUpload(folderId, path)
            .combine(sTSProvider.getSTS()) { initData, stsdata ->
                Pair(initData, stsdata)
            }.flatMapConcat { pair ->
                processUpload(pair.first, pair.second, path)
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun retryTask(objectId: String): Flow<TransferObserver> {
        return processRetryUpload(objectId)
    }

    override fun replaceFileWithPath(path: String, objectId: String): Flow<TransferObserver> {
        return sTSProvider.getSTS().flatMapConcat { _stsResponse ->
            processReplaceUpload(objectId, _stsResponse, path)
        }
    }

    private fun processUpload(
        initData: InitialUploadResponse,
        stsResponse: SecureTokenServiceDataResponse,
        path: String
    ): Flow<TransferObserver> {
        return flow {
            val uploadResponse = uploadFile(
                objectId = initData.data?.id,
                stsResponse = stsResponse,
                path = path
            )
            val uploadObserver = uploadResponse.first
            val taskUploadModel = uploadResponse.second
            cacheUploadTaskRepository.addUploadTask(taskUploadModel)
            emit(uploadObserver)
        }.flowOn(Dispatchers.IO)
    }

    private fun processRetryUpload(
        objectId: String,
    ): Flow<TransferObserver> {
        return flow {
            cacheUploadTaskRepository.getTaskByObjectId(objectId)
                ?.let { task ->
                    task.status = TaskStatusType.WAITING
                    testLoopUpload(task)?.let { emit(it) }
                }
        }.flowOn(Dispatchers.IO)
    }

    private fun processReplaceUpload(
        objectId: String,
        stsResponse: SecureTokenServiceDataResponse,
        path: String
    ): Flow<TransferObserver> {
        return flow {
            val uploadResponse = uploadFile(
                objectId = objectId,
                stsResponse = stsResponse,
                path = path,
            )
            val uploadObserver = uploadResponse.first
            val taskUploadModel = uploadResponse.second
            cacheUploadTaskRepository.addUploadTask(taskUploadModel)
            emit(uploadObserver)
        }
    }

    private suspend fun uploadFile(
        objectId: String?,
        stsResponse: SecureTokenServiceDataResponse,
        path: String
    ): Pair<TransferObserver, TaskUploadModel> {
        val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
            stsResponse,
            contextDataProvider.getDataContext()
        )
        val file = File(path)
        val mimeTypeStr = trueCloudV3FileUtil.getMimeType(
            file.toUri(),
            contextDataProvider.getContentResolver()
        ).orEmpty()
        val mimeType = FileMimeTypeManager.getMimeType(mimeTypeStr)
        val coverImageSize = uploadThumbnail(file, objectId, transferUtility, mimeType)
        val meta = ObjectMetadata()
        meta.contentType = Mimetypes.getInstance().getMimetype(file)
        val uploadObserver = transferUtility.upload(objectId, file, meta)
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Timber.i("upload onStateChanged  id : $id  , state : ${state?.name}")
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Timber.i("upload onProgressChanged  id : $id  , bytesCurrent : $bytesCurrent")
            }

            override fun onError(id: Int, ex: Exception?) {
                Timber.i("upload onError  id : $id  , message : ${ex?.message}")
            }
        })
        val task = TaskUploadModel(
            uploadObserver.id,
            path,
            TaskStatusType.IN_PROGRESS,
            file.name,
            file.length().formatBinarySize(contextDataProvider.getDataContext()),
            coverImageSize,
            mimeType,
            objectId,
            updateAt = getCurrentDateTimeUseCase.execute().firstOrNull() ?: 0
        )
        return Pair(uploadObserver, task)
    }

    override fun uploadContactWithPath(
        path: String,
        folderId: String?,
        key: String
    ): Flow<TransferObserver> {
        val file = File(path)
        return sTSProvider.getSTS().map {
            val stsResponse = it
            val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
                stsResponse,
                contextDataProvider.getDataContext()
            )
            val uploadObserver = transferUtility.upload(key, file)
            uploadObserver
        }
    }

    @VisibleForTesting
    fun uploadThumbnail(
        file: File,
        id: String?,
        transferUtility: TransferUtility?,
        mimeType: FileMimeType
    ): Int? {
        if (id == null || transferUtility == null) return null
        val coverBitmap: Bitmap?
        val fileSize = file.length()
        val isCreateCover = fileSize >= MINIMUM_SIZE
        var coverImageSize: Int? = null
        if (isCreateCover) {
            when (mimeType) {
                FileMimeType.IMAGE -> {
                    coverBitmap = bitmapUtil.createImageThumbnail(file)
                    coverImageSize = processUploadCoverImage(id, coverBitmap, transferUtility)
                }

                FileMimeType.VIDEO -> {
                    coverBitmap = bitmapUtil.createVideoThumbnail(file)
                    coverBitmap?.let { _coverBitmap ->
                        coverImageSize = processUploadCoverImage(id, _coverBitmap, transferUtility)
                    }
                }

                else -> {
                    coverImageSize = null
                }
            }
        } else {
            when (mimeType) {
                FileMimeType.IMAGE,
                FileMimeType.VIDEO -> {
                    coverImageSize = COVER_SIZE_ZERO
                }

                else -> {
                    coverImageSize = null
                }
            }
        }
        return coverImageSize
    }

    private fun processUploadCoverImage(
        id: String?,
        coverBitmap: Bitmap,
        transferUtility: TransferUtility?
    ): Int {
        val bytes = ByteArrayOutputStream()
        coverBitmap.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_QUALITY, bytes)
        val bitmapdata = bytes.toByteArray()
        val bs = ByteArrayInputStream(bitmapdata)
        transferUtility?.upload(id + KEY_COVER_IMAGE_SUFFIX, bs)
        return coverBitmap.byteCount
    }

    private fun initUpload(folderId: String?, path: String) = flow {
        val file = File(path)
        val request = InitUploadRequest(
            parentObjectId = folderId,
            name = file.name,
            mimeType = trueCloudV3FileUtil.getMimeType(
                file.toUri(), contextDataProvider.getContentResolver()
            ) ?: EMPTY_STRING,
            size = file.length().toInt()
        )
        trueCloudV3UploadInterface.initialUpload(
            ssoid = userRepository.getSsoId(),
            request = request
        ).run {
            val code = code()
            val responseBody = body()
            if (isSuccessful && responseBody != null) {
                emit(responseBody)
            } else if (code == ERROR_CODE_EXCEED_LIMIT) {
                error(ERROR_INIT_UPLOAD_DATA_EXCEED_LIMIT)
            } else {
                error(ERROR_INIT_UPLOAD_DATA)
            }
        }
    }

    override fun uploadMultiFileWithTask(
        tasks: List<TaskUploadModel>
    ): Flow<TransferObserver> {
        return channelFlow {
            for (task in tasks) {
                task.objectId?.let { objId ->
                    cacheUploadTaskRepository.getTaskByObjectId(objId)
                        ?.let { task ->
                            try {
                                testLoopUpload(task)?.let { send(it) }
                            } catch (e: Exception) {
                                channel.close(e)
                            }
                        }
                }
            }
        }
    }

    private suspend fun testLoopUpload(task: TaskUploadModel): TransferObserver? {
        if (task.status == TaskStatusType.PAUSE) return null
        task.status = TaskStatusType.WAITING
        cacheUploadTaskRepository.updateTaskIdWithObjectId(task, task.objectId!!)
        val stsResponse = sTSProvider.getSTS().firstOrNull()
        val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
            stsResponse,
            contextDataProvider.getDataContext()
        )
        val taskUpdate = getTaskFromPath(task)
        val file = File(task.path)
        val mimeTypeStr = trueCloudV3FileUtil.getMimeType(
            file.toUri(),
            contextDataProvider.getContentResolver()
        ).orEmpty()
        val mimeType = FileMimeTypeManager.getMimeType(mimeTypeStr)
        val coverImageSize = uploadThumbnail(file, taskUpdate.data?.id, transferUtility, mimeType)
        val meta = ObjectMetadata()
        meta.contentType = Mimetypes.getInstance().getMimetype(file)
        val transferObserver = transferUtility.upload(taskUpdate.data?.id, file, meta)
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Timber.i("onStateChanged  id : $id  , state : ${state?.name}")
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Timber.i("onStateChanged id : $id, bytesCurrent : $bytesCurrent , bytesTotal : $bytesTotal")
            }

            override fun onError(id: Int, ex: Exception?) {
                Timber.i("onError  id : $id  , message : ${ex?.message}")
            }
        })
        task.status = TaskStatusType.IN_PROGRESS
        task.id = transferObserver.id
        task.coverImageSize = coverImageSize
        cacheUploadTaskRepository.updateTaskIdWithObjectId(task, taskUpdate.data?.id!!)
        delay(DEFAULT_UPLOAD_DELAY)
        return transferObserver
    }

    private suspend fun getTaskFromPath(
        task: TaskUploadModel
    ): InitialUploadResponse {
        var response: InitialUploadResponse? = null
        initUpload(task.folderId, task.path)
            .map { _initData ->
                response = _initData
            }
            .catch {
                throw it
            }
            .collect()
        return response!!
    }
}
