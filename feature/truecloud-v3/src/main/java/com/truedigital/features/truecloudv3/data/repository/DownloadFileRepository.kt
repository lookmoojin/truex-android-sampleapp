package com.truedigital.features.truecloudv3.data.repository

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_INIT_DOWNLOAD_DATA
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

interface DownloadFileRepository {
    fun download(key: String, path: String): Flow<TransferObserver>
    fun downloadCoverImage(key: String, path: String): Flow<TransferObserver>
    fun downloadContact(key: String, path: String): Flow<TransferObserver>
    fun downloadForPreview(key: String, path: String): Flow<TransferObserver>
    fun downloadForShare(key: String, path: String): Flow<TransferObserver>
}

class DownloadFileRepositoryImpl @Inject constructor(
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider,
    private val trueCloudV3DownloadInterface: TrueCloudV3DownloadInterface,
    private val userRepository: UserRepository,
    private val cacheUploadTaskRepository: CacheUploadTaskRepository,
    private val sTSProvider: SecureTokenServiceProvider,
    private val contextDataProvider: ContextDataProvider
) : DownloadFileRepository {

    companion object {
        private const val EMPTY_STRING = ""
    }

    override fun download(key: String, path: String): Flow<TransferObserver> {
        val file = File(path)
        return flow {
            trueCloudV3DownloadInterface.initialDownload(
                ssoid = userRepository.getSsoId(),
                objectId = key
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_INIT_DOWNLOAD_DATA)
                }
            }
        }.combine(sTSProvider.getSTS()) { initData, stsdata ->
            Pair(initData, stsdata)
        }.map {
            val data = it.first.data
            val stsData = it.second
            val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
                stsData,
                contextDataProvider.getDataContext()
            )
            val mimeType = FileMimeTypeManager.getMimeType(data?.mimeType ?: EMPTY_STRING)
            val downloadObserver = transferUtility.download(key, file)
            val task = TaskUploadModel(
                downloadObserver.id,
                path,
                TaskStatusType.IN_PROGRESS,
                file.name,
                data?.size?.toString() ?: EMPTY_STRING,
                0,
                mimeType,
                data?.id,
                TaskActionType.DOWNLOAD
            )
            cacheUploadTaskRepository.addUploadTask(task)
            downloadObserver
        }
    }

    override fun downloadForShare(key: String, path: String): Flow<TransferObserver> {
        return downloadForPreview(key, path)
    }
    override fun downloadForPreview(key: String, path: String): Flow<TransferObserver> {
        val file = File(path)
        return flow {
            trueCloudV3DownloadInterface.initialDownload(
                ssoid = userRepository.getSsoId(),
                objectId = key
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_INIT_DOWNLOAD_DATA)
                }
            }
        }.combine(sTSProvider.getSTS()) { initData, stsdata ->
            Pair(initData, stsdata)
        }.map {
            val stsData = it.second
            val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
                stsData,
                contextDataProvider.getDataContext()
            )
            transferUtility.download(key, file)
        }
    }

    override fun downloadCoverImage(
        key: String,
        path: String
    ): Flow<TransferObserver> {
        val file = File(path)
        return sTSProvider.getSTS().map {
            val data = it
            val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
                data,
                contextDataProvider.getDataContext()
            )
            transferUtility.download(key, file)
        }
    }

    override fun downloadContact(
        key: String,
        path: String
    ): Flow<TransferObserver> {
        return sTSProvider.getSTS().map {
            val data = it
            val file = File(path)
            val transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
                data,
                contextDataProvider.getDataContext()
            )
            transferUtility.download(key, file)
        }
    }
}
