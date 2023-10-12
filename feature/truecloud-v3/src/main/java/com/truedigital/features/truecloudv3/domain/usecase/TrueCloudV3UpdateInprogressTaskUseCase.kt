package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import javax.inject.Inject

interface TrueCloudV3UpdateInprogressTaskUseCase {
    suspend fun execute(id: Int): TaskUploadModel?
}

class TrueCloudV3UpdateInprogressTaskUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : TrueCloudV3UpdateInprogressTaskUseCase {
    override suspend fun execute(id: Int): TaskUploadModel? {
        return cacheUploadTaskRepository.getTask(id)
    }
}
