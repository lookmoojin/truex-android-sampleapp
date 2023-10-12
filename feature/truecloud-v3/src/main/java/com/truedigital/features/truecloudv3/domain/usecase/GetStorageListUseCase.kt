package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetStorageListUseCase {
    fun execute(
        rootFolderId: String,
        sortType: SortType,
        skip: Int
    ): Flow<List<TrueCloudV3Model>>
}

class GetStorageListUseCaseImpl @Inject constructor(
    private val getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository
) : GetStorageListUseCase {

    companion object {
        private const val LIMIT = 20
    }

    override fun execute(
        rootFolderId: String,
        sortType: SortType,
        skip: Int
    ): Flow<List<TrueCloudV3Model>> {
        return getTrueCloudStorageListRepository.getList(
            rootFolderId,
            sortType,
            skip * LIMIT,
            LIMIT
        ).map { response ->
            response.data?.uploaded?.convertToListTrueCloudV3Model() ?: error(
                TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL
            )
        }
    }
}
