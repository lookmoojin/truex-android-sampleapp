package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.RenameRequest
import com.truedigital.features.truecloudv3.data.model.RenameStorageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RenameFileRepository {
    fun rename(id: String, name: String): Flow<RenameStorageResponse>
}

class RenameFileRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : RenameFileRepository {

    override fun rename(id: String, name: String): Flow<RenameStorageResponse> {
        return flow {
            trueCloudV3Interface.renameObject(
                ssoid = userRepository.getSsoId(),
                fileId = id,
                name = RenameRequest(name)
            )
                .run {
                    val responseBody = body()
                    if (isSuccessful && responseBody != null) {
                        emit(responseBody)
                    } else {
                        error("${message()} (${code()})")
                    }
                }
        }
    }
}
