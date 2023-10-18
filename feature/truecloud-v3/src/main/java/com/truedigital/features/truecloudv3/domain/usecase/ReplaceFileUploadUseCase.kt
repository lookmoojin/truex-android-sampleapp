package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ReplaceFileUploadUseCase {
    fun execute(path: String, id: String): Flow<TrueCloudV3TransferObserver>
}

class ReplaceFileUploadUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository
) : ReplaceFileUploadUseCase {
    override fun execute(path: String, id: String): Flow<TrueCloudV3TransferObserver> {
        return uploadFileRepository.replaceFileWithPath(path, id)
            .map { TrueCloudV3TransferObserver(it) }
    }
}
