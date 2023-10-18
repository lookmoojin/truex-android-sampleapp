package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudObjectRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3ObjectInfoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetObjectInfoUseCase {
    fun execute(
        key: String,
    ): Flow<TrueCloudV3ObjectInfoModel>
}

class GetObjectInfoUseCaseImpl @Inject constructor(
    private val getTrueCloudObjectRepository: GetTrueCloudObjectRepository,
) : GetObjectInfoUseCase {

    override fun execute(key: String): Flow<TrueCloudV3ObjectInfoModel> {
        return getTrueCloudObjectRepository.getObjectInfo(key).map { response ->
            response.data?.let {
                TrueCloudV3ObjectInfoModel(
                    id = it.id,
                    parentObjectId = it.parentObjectId,
                    objectType = it.objectType,
                    name = it.name,
                    mimeType = it.mimeType ?: "",
                    category = it.category,
                    fileUrl = it.fileUrl,
                )
            } ?: error(
                TrueCloudV3ErrorMessage.ERROR_GET_OBJECT_INFO
            )
        }
    }
}
