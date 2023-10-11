package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetPrivateSharedFileUseCase {
    fun execute(
        encryptedSharedObjectId: String,
        sharedObjectAccessToken: String
    ): Flow<SharedFileModel>
}
class GetPrivateSharedFileUseCaseImpl @Inject constructor(
    private val getSharedFileRepository: GetSharedFileRepository
) : GetPrivateSharedFileUseCase {

    override fun execute(
        encryptedSharedObjectId: String,
        sharedObjectAccessToken: String
    ): Flow<SharedFileModel> {
        return getSharedFileRepository.getPrivateSharedObject(
            encryptedSharedObjectId,
            sharedObjectAccessToken
        ).map { response ->
            SharedFileModel(
                id = response.data?.fileObject?.id ?: "",
                fileUrl = response.data?.fileObject?.fileUrl,
                name = response.data?.fileObject?.name ?: "",
                category = response.data?.fileObject?.category,
                objectType = response.data?.fileObject?.objectType,
                mimeType = response.data?.fileObject?.mimeType
            )
        }
    }
}
