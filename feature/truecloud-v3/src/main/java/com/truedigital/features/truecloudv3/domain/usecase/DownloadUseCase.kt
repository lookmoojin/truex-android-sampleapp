package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.DownloadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class DownloadType(val type: String) {
    NORMAL("Normal"),
    PREVIEW("Preview"),
    SHARE("Share")
}

interface DownloadUseCase {
    fun execute(
        key: String,
        path: String,
        downloadType: DownloadType
    ): Flow<TrueCloudV3TransferObserver>
}

class DownloadUseCaseImpl @Inject constructor(
    private val downloadFileRepository: DownloadFileRepository
) : DownloadUseCase {

    override fun execute(
        key: String,
        path: String,
        downloadType: DownloadType
    ): Flow<TrueCloudV3TransferObserver> {
        return when (downloadType) {
            DownloadType.NORMAL -> downloadFileRepository.download(key, path)
                .map { TrueCloudV3TransferObserver(it) }
            DownloadType.PREVIEW -> downloadFileRepository.downloadForPreview(key, path)
                .map { TrueCloudV3TransferObserver(it) }
            DownloadType.SHARE -> downloadFileRepository.downloadForShare(key, path)
                .map { TrueCloudV3TransferObserver(it) }
        }
    }
}
