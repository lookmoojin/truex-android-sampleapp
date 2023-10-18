package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTrashListUseCase {
    fun execute(): Flow<List<TrueCloudV3Model>>
}

class GetTrashListUseCaseImpl @Inject constructor(
    private val getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository,
    private val contextDataProviderWrapper: ContextDataProviderWrapper
) : GetTrashListUseCase {
    override fun execute(): Flow<List<TrueCloudV3Model>> {
        return getTrueCloudStorageListRepository.getTrashList()
            .map { response ->
                response.data?.uploaded?.convertToListTrueCloudV3Model(contextDataProviderWrapper) ?: error(
                    TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL
                )
            }
    }
}
