package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.CompleteUploadRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CompleteUploadContactUseCase {
    fun execute(key: String): Flow<TrueCloudV3Model>
}

class CompleteUploadContactUseCaseImpl @Inject constructor(
    private val completeUploadRepository: CompleteUploadRepository
) : CompleteUploadContactUseCase {
    override fun execute(key: String): Flow<TrueCloudV3Model> {
        return completeUploadRepository.callComplete(key, null).map { response ->
            response.data?.convertToTrueCloudV3Model() ?: error(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD)
        }
    }
}
