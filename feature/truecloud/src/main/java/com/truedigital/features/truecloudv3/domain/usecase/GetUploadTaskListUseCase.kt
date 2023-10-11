package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetUploadTaskListUseCase {
    fun execute(): Flow<MutableList<TaskUploadModel>?>
}

class GetUploadTaskListUseCaseImpl @Inject constructor(
    private val cacheUploadTaskRepository: CacheUploadTaskRepository
) : GetUploadTaskListUseCase {

    override fun execute(): Flow<MutableList<TaskUploadModel>?> {
        return cacheUploadTaskRepository.getRefreshTasks()
    }
}
