package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import javax.inject.Inject

interface GetUploadTaskUseCase {
    suspend fun execute(id: Int): TaskUploadModel?
}

class GetUploadTaskUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : GetUploadTaskUseCase {
    override suspend fun execute(id: Int): TaskUploadModel? {
        return cacheUploadTaskRepository.getTask(id)
    }
}
