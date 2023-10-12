package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_SEARCH_FILE
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3SearchFileInterface
import com.truedigital.features.truecloudv3.data.model.SearchFileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface SearchFileRepository {
    fun searchFile(
        name: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<SearchFileResponse>

    fun searchWithCategory(
        name: String,
        sortType: SortType,
        category: String,
        skip: Int,
        take: Int
    ): Flow<SearchFileResponse>
}

class SearchFileRepositoryImpl @Inject constructor(
    private val searchFileInterface: TrueCloudV3SearchFileInterface,
    private val userRepository: UserRepository
) : SearchFileRepository {

    override fun searchFile(
        name: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<SearchFileResponse> {
        return flow {
            searchFileInterface.searchFile(
                ssoid = userRepository.getSsoId(),
                name = name,
                sort = sortType.type,
                skip = skip,
                take = take
            ).run {
                val responseBody = body()
                val code = code()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_SEARCH_FILE + code.toString())
                }
            }
        }
    }

    override fun searchWithCategory(
        name: String,
        sortType: SortType,
        category: String,
        skip: Int,
        take: Int
    ): Flow<SearchFileResponse> {
        return flow {
            searchFileInterface.searchFileWithCategory(
                ssoid = userRepository.getSsoId(),
                name = name,
                sort = sortType.type,
                category = category,
                skip = skip,
                take = take
            ).run {
                val responseBody = body()
                val code = code()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_SEARCH_FILE + code.toString())
                }
            }
        }
    }
}
