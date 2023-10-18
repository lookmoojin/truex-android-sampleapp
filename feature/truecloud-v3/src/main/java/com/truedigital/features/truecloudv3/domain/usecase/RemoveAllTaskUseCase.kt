package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import javax.inject.Inject

interface RemoveAllTaskUseCase {
    suspend fun execute()
}

class RemoveAllTaskUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : RemoveAllTaskUseCase {

    override suspend fun execute() {
        cacheUploadTaskRepository.clearAllTask()
    }
}
