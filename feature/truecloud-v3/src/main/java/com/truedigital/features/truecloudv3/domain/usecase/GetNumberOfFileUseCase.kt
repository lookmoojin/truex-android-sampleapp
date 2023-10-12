package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileCategoryTypeManager
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetNumberOfFileUseCase {
    fun execute(
        rootFolderId: String,
        category: String,
        sortType: SortType
    ): Flow<Int>
}

class GetNumberOfFileUseCaseImpl @Inject constructor(
    private val getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository
) : GetNumberOfFileUseCase {

    companion object {
        private const val DEFAULT_LIMIT = 20
        private const val DEFAULT_SKIP = 0
        private const val DEFAULT_NUMBER_OF_FILES = 0
    }

    override fun execute(
        rootFolderId: String,
        category: String,
        sortType: SortType
    ): Flow<Int> {
        return getListWithType(
            rootFolderId,
            category,
            sortType
        ).map { _response ->
            _response.data?.info?.numberOfFiles ?: DEFAULT_NUMBER_OF_FILES
        }
    }

    private fun getListWithType(
        rootFolderId: String,
        category: String,
        sortType: SortType
    ): Flow<ListStorageResponse> {
        val categoryType = FileCategoryTypeManager.getCategoryType(category)
        return if (categoryType != FileCategoryType.UNSUPPORTED_FORMAT) {
            getTrueCloudStorageListRepository.getListWithCategory(
                category,
                sortType,
                DEFAULT_SKIP,
                DEFAULT_LIMIT
            )
        } else {
            getTrueCloudStorageListRepository.getList(
                rootFolderId,
                sortType,
                DEFAULT_SKIP,
                DEFAULT_LIMIT
            )
        }
    }
}
