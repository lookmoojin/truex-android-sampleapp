package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.DownloadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DownloadCoverImageUseCase {
    fun execute(key: String, path: String): Flow<TrueCloudV3TransferObserver>
}

class DownloadCoverImageUseCaseImpl @Inject constructor(
    private val downloadFileRepository: DownloadFileRepository
) : DownloadCoverImageUseCase {
    override fun execute(key: String, path: String): Flow<TrueCloudV3TransferObserver> {
        return downloadFileRepository.downloadCoverImage(key, path)
            .map { TrueCloudV3TransferObserver(it) }
    }
}
