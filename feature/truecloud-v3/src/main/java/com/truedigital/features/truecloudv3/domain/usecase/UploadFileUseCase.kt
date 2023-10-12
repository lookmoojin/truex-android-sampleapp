package com.truedigital.features.truecloudv3.domain.usecase

import android.net.Uri
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.extension.isComplete
import com.truedigital.features.truecloudv3.util.TrueCloudV3FileUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

interface UploadFileUseCase {
    fun execute(
        uris: List<Uri>,
        folderId: String,
        uploadType: TaskActionType = TaskActionType.UPLOAD
    ): Flow<TrueCloudV3TransferObserver>
}

class UploadFileUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository,
    private val contextDataProvider: ContextDataProvider,
    private val trueCloudV3FileUtil: TrueCloudV3FileUtil
) : UploadFileUseCase {
    override fun execute(
        uris: List<Uri>,
        folderId: String,
        uploadType: TaskActionType
    ): Flow<TrueCloudV3TransferObserver> {
        return flow {
            val pathList = uris.map { _uri ->
                var path = trueCloudV3FileUtil.getPathFromUri(contextDataProvider, _uri)
                if (path == null || !File(path).isComplete()) {
                    path = trueCloudV3FileUtil.readContentToFile(
                        _uri, contextDataProvider
                    )?.path.orEmpty()
                }
                path
            }
            emit(pathList)
        }.flatMapLatest { _pathList ->
            uploadFileRepository.uploadMultiFileWithPath(_pathList, folderId, uploadType)
                .map { transferObserver ->
                    TrueCloudV3TransferObserver(transferObserver)
                }
        }
    }
}
