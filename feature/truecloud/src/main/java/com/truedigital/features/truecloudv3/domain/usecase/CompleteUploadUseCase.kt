package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.data.repository.CompleteUploadRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CompleteUploadUseCase {
    fun execute(id: Int): Flow<TrueCloudV3Model>?
}

class CompleteUploadUseCaseImpl @Inject constructor(
    private val completeUploadRepository: CompleteUploadRepository,
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : CompleteUploadUseCase {

    override fun execute(id: Int): Flow<TrueCloudV3Model>? {
        return flow {
            cacheUploadTaskRepository.updateTaskStatus(id, TaskStatusType.COMPLETE)
            emit(cacheUploadTaskRepository.getTask(id))
        }.flatMapConcat { taskUploadModel ->
            val objectId = taskUploadModel?.objectId ?: ""
            completeUploadRepository.callComplete(objectId, taskUploadModel?.coverImageSize)
        }.map { response ->
            response.data?.convertToTrueCloudV3Model() ?: error(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD)
        }
    }
}
