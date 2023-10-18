package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.CreateFolderRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CreateFolderUserCase {
    fun execute(parentObjectId: String, name: String): Flow<TrueCloudV3Model>
}

class CreateFolderUserCaseImpl @Inject constructor(
    private val createFolderRepository: CreateFolderRepository
) : CreateFolderUserCase {
    override fun execute(parentObjectId: String, name: String): Flow<TrueCloudV3Model> {
        return createFolderRepository.create(parentObjectId, name).map { response ->
            response.data?.convertToTrueCloudV3Model() ?: error(TrueCloudV3ErrorMessage.ERROR_CREATE_FOLDER)
        }
    }
}
