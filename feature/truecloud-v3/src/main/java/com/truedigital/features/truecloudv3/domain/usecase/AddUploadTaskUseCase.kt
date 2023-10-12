package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import javax.inject.Inject

interface AddUploadTaskUseCase {
    suspend fun execute(taskUploadModel: TaskUploadModel)
}

class AddUploadTaskUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : AddUploadTaskUseCase {
    override suspend fun execute(taskUploadModel: TaskUploadModel) {
        cacheUploadTaskRepository.addUploadTask(taskUploadModel)
    }
}
