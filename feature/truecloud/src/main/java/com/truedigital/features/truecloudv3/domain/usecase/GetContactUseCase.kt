package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.model.TrueCloudV3ContactData
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetContactUseCase {
    fun execute(): Flow<List<ContactTrueCloudModel>>
}

class GetContactUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository
) : GetContactUseCase {
    override fun execute(): Flow<List<ContactTrueCloudModel>> {
        return contactRepository.getContact().map {
            mapTrueCloudV3ContactDataToContactTrueCloudModel(it)
        }
    }

    private fun mapTrueCloudV3ContactDataToContactTrueCloudModel(
        contactData: MutableList<TrueCloudV3ContactData>
    ): List<ContactTrueCloudModel> {
        return contactData.map { _contactData ->
            val phoneNumbers = _contactData.tel.map { _tel ->
                ContactPhoneNumberModel(
                    type = _tel.type,
                    number = _tel.number
                )
            }
            ContactTrueCloudModel(
                firstName = _contactData.firstName,
                lastName = _contactData.lastName,
                email = _contactData.email,
                tel = phoneNumbers
            )
        }
    }
}
