package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import javax.inject.Inject

interface RemoveTaskUseCase {
    suspend fun execute(id: Int)
}

class RemoveTaskUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : RemoveTaskUseCase {
    override suspend fun execute(id: Int) {
        cacheUploadTaskRepository.removeTask(id)
    }
}
