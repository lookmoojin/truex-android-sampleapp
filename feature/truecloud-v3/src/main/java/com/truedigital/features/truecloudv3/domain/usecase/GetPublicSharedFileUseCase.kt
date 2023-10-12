package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetPublicSharedFileUseCase {
    fun execute(encryptedSharedObjectId: String): Flow<SharedFileModel>
}

class GetPublicSharedFileUseCaseImpl @Inject constructor(
    private val getSharedFileRepository: GetSharedFileRepository
) : GetPublicSharedFileUseCase {

    override fun execute(encryptedSharedObjectId: String): Flow<SharedFileModel> {
        return getSharedFileRepository.getPublicSharedObject(encryptedSharedObjectId)
            .map { response ->
                val fileObject = response.data?.fileObject
                SharedFileModel(
                    id = fileObject?.id ?: "",
                    fileUrl = fileObject?.fileUrl,
                    name = fileObject?.name ?: "",
                    category = fileObject?.category,
                    objectType = fileObject?.objectType,
                    mimeType = fileObject?.mimeType,
                    status = response.statusCode,
                    statusMessage = response.statusMessage
                )
            }
    }
}
