package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.DeleteStorageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface DeleteFileRepository {
    fun delete(id: String): Flow<DeleteStorageResponse>
}

class DeleteFileRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : DeleteFileRepository {
    companion object {
        const val ERROR_DELETE_FILE = "Delete file error"
    }

    override fun delete(id: String): Flow<DeleteStorageResponse> {
        return flow {
            trueCloudV3Interface.deleteObject(
                ssoid = userRepository.getSsoId(),
                objectId = id
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error(ERROR_DELETE_FILE)
                    }
                }
        }
    }
}
