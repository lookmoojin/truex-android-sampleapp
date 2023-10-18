package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.CreateFolderRequest
import com.truedigital.features.truecloudv3.data.model.CreateFolderResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CreateFolderRepository {
    fun create(parentObjectId: String, name: String): Flow<CreateFolderResponse>
}

class CreateFolderRepositoryImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository
) : CreateFolderRepository {

    companion object {
        private const val ERROR_CREATE_FOLDER = "Folder create failed"
    }

    override fun create(parentObjectId: String, name: String): Flow<CreateFolderResponse> {
        val request = CreateFolderRequest(
            parentObjectId = parentObjectId,
            name = name
        )
        return flow {
            trueCloudV3Interface.createFolder(
                ssoid = userRepository.getSsoId(),
                request = request
            ).run {
                val responseBody = body()
                if (isSuccessful && responseBody != null) {
                    emit(responseBody)
                } else {
                    error(ERROR_CREATE_FOLDER)
                }
            }
        }
    }
}
