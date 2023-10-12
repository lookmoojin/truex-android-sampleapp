package com.truedigital.features.truecloudv3.domain.usecase

import android.content.Intent
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.wafflecopter.multicontactpicker.MultiContactPicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetContactDataFromSelectorUseCase {
    fun execute(intent: Intent?): Flow<List<ContactTrueCloudModel>>
}

class GetContactDataFromSelectorUseCaseImpl @Inject constructor() :
    GetContactDataFromSelectorUseCase {

    override fun execute(intent: Intent?): Flow<List<ContactTrueCloudModel>> {
        return flow {
            val data = MultiContactPicker.obtainResult(intent)?.map { _contactResult ->
                ContactTrueCloudModel(
                    firstName = _contactResult.displayName,
                    email = _contactResult.emails.firstOrNull().orEmpty(),
                    tel = _contactResult.phoneNumbers.map { _phoneNumber ->
                        ContactPhoneNumberModel(
                            number = _phoneNumber.number,
                            type = _phoneNumber.typeLabel
                        )
                    }
                )
            } ?: listOf()
            emit(data)
        }
    }
}
