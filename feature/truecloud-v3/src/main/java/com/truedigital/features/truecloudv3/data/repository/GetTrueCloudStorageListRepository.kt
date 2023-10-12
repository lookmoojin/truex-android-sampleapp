package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetTrueCloudStorageListRepository {
    fun getListWithCategory(
        category: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<ListStorageResponse>

    fun getList(
        rootFolderId: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<ListStorageResponse>

    fun getTrashList(): Flow<ListStorageResponse>
}

class GetTrueCloudStorageListRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : GetTrueCloudStorageListRepository {

    companion object {
        const val ACTION = "get-object-list"
    }

    override fun getListWithCategory(
        category: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<ListStorageResponse> {
        return flow {
            trueCloudV3Interface.getCategoryFileList(
                ssoid = userRepository.getSsoId(),
                category = category,
                order = sortType.type,
                skip = skip,
                take = take
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(code())
                }
            }
        }
    }

    override fun getList(
        rootFolderId: String,
        sortType: SortType,
        skip: Int,
        take: Int
    ): Flow<ListStorageResponse> {
        return flow {
            trueCloudV3Interface.getFileList(
                ssoid = userRepository.getSsoId(),
                rootFolderId = rootFolderId,
                order = sortType.type,
                skip = skip,
                take = take
            ).run {
                val code = code()
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(code)
                }
            }
        }
    }

    override fun getTrashList(): Flow<ListStorageResponse> {
        return flow {
            trueCloudV3Interface.getTrashList(
                ssoid = userRepository.getSsoId()
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(code())
                }
            }
        }
    }
}
