package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.SearchFileRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SearchWithCategoryUseCase {
    fun execute(
        searchString: String,
        sortType: SortType,
        category: String,
        skip: Int
    ): Flow<List<TrueCloudV3Model>>
}

class SearchWithCategoryUseCaseImpl @Inject constructor(
    private val searchFileRepository: SearchFileRepository
) : SearchWithCategoryUseCase {
    companion object {
        private const val LIMIT = 20
    }

    override fun execute(
        searchString: String,
        sortType: SortType,
        category: String,
        skip: Int
    ): Flow<List<TrueCloudV3Model>> {
        return searchFileRepository.searchWithCategory(
            name = searchString,
            sortType = sortType,
            skip = skip * LIMIT,
            category = category,
            take = LIMIT
        ).map { response ->
            response.data?.uploaded?.convertToListTrueCloudV3Model() ?: error(
                TrueCloudV3ErrorMessage.ERROR_SEARCH_FILE
            )
        }
    }
}
