package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.DeleteFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DeleteFileUseCase {
    fun execute(id: String): Flow<TrueCloudV3Model>
}

class DeleteFileUseCaseImpl @Inject constructor(
    private val deleteFileRepository: DeleteFileRepository
) : DeleteFileUseCase {
    override fun execute(id: String): Flow<TrueCloudV3Model> {
        return deleteFileRepository.delete(id).map { response ->
            response.data?.convertToTrueCloudV3Model()
                ?: error(TrueCloudV3ErrorMessage.ERROR_DELETE_OBJECT)
        }.flowOn(Dispatchers.IO)
    }
}
