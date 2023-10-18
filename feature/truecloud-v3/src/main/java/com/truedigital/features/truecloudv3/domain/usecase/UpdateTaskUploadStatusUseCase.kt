package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import javax.inject.Inject

interface UpdateTaskUploadStatusUseCase {
    suspend fun execute(id: Int, statusType: TaskStatusType)
}

class UpdateTaskUploadStatusUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : UpdateTaskUploadStatusUseCase {
    override suspend fun execute(id: Int, statusType: TaskStatusType) {
        cacheUploadTaskRepository.updateTaskStatus(id, statusType)
    }
}
