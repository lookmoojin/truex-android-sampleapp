package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.RenameFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface RenameFileUseCase {
    fun execute(id: String, name: String): Flow<TrueCloudV3Model>
}

class RenameFileUseCaseImpl @Inject constructor(
    private val renameFileRepository: RenameFileRepository
) : RenameFileUseCase {
    override fun execute(id: String, name: String): Flow<TrueCloudV3Model> {
        return renameFileRepository.rename(id, name).map { response ->
            response.data?.convertToTrueCloudV3Model()
                ?: error(TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL)
        }
    }
}
