package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface RetryUploadUseCase {
    fun execute(id: String): Flow<TrueCloudV3TransferObserver>
}

class RetryUploadUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository
) : RetryUploadUseCase {
    override fun execute(id: String): Flow<TrueCloudV3TransferObserver> {
        return uploadFileRepository.retryTask(id).map { TrueCloudV3TransferObserver(it) }
    }
}
