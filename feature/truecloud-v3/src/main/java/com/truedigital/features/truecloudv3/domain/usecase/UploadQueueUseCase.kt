package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UploadQueueUseCase {
    fun execute(
        task: List<TaskUploadModel>
    ): Flow<TrueCloudV3TransferObserver>
}

class UploadQueueUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository,
) : UploadQueueUseCase {
    override fun execute(
        task: List<TaskUploadModel>
    ): Flow<TrueCloudV3TransferObserver> {
        return uploadFileRepository.uploadMultiFileWithTask(task)
            .map { transferObserver ->
                TrueCloudV3TransferObserver(transferObserver)
            }
    }
}
