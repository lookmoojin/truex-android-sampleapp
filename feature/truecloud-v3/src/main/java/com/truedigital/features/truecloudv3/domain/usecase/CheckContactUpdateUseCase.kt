package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.domain.model.ContactDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CheckContactUpdateUseCase {
    fun execute(): Flow<ContactDataModel>
}

class CheckContactUpdateUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository
) : CheckContactUpdateUseCase {

    override fun execute(): Flow<ContactDataModel> {
        return contactRepository.getContactFromNetwork().map { response ->
            val contactData =
                response.data ?: error(TrueCloudV3ErrorMessage.ERROR_CONTACT_NOT_FOUND)
            if (!contactData.isUploaded) {
                error(TrueCloudV3ErrorMessage.ERROR_CONTACT_NOT_FOUND)
            } else {
                ContactDataModel(
                    id = contactData.id,
                    userId = contactData.userId,
                    objectType = contactData.objectType,
                    parentObjectId = contactData.parentObjectId,
                    name = contactData.name,
                    mimeType = contactData.mimeType,
                    coverImageKey = contactData.coverImageKey,
                    coverImageSize = contactData.coverImageSize,
                    category = contactData.category,
                    size = contactData.size,
                    checksum = contactData.checksum,
                    isUploaded = contactData.isUploaded,
                    deviceId = contactData.deviceId,
                    updatedAt = contactData.updatedAt,
                    createdAt = contactData.createdAt,
                    lastModified = contactData.lastModified
                )
            }
        }
    }
}
