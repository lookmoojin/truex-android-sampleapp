package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetNewUploadTaskListUseCase {
    fun execute(): Flow<MutableList<TaskUploadModel>>
}

class GetNewUploadTaskListUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : GetNewUploadTaskListUseCase {
    override fun execute(): Flow<MutableList<TaskUploadModel>> {
        return cacheUploadTaskRepository.getRefreshTasks()
    }
}
