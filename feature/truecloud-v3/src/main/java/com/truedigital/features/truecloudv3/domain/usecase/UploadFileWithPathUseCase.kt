package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UploadFileWithPathUseCase {
    fun execute(path: String, folderId: String?): Flow<TrueCloudV3TransferObserver>
}

class UploadFileWithPathUseCaseImpl @Inject constructor(
    private val uploadFileRepository: UploadFileRepository
) : UploadFileWithPathUseCase {
    override fun execute(path: String, folderId: String?): Flow<TrueCloudV3TransferObserver> {
        return uploadFileRepository.uploadFileWithPath(path, folderId).map { TrueCloudV3TransferObserver(it) }
    }
}
