package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.domain.model.Contact
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel
import com.truedigital.features.truecloudv3.util.TrueCloudV3GroupAlphabetUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetGroupContactUseCase {
    fun execute(contacts: List<Contact>): Flow<List<Contact>>
}

class GetGroupContactUseCaseImpl @Inject constructor() : GetGroupContactUseCase {
    override fun execute(contacts: List<Contact>): Flow<List<Contact>> {
        return flow {
            runCatching {
                contacts.groupBy {
                    val contact = it as ContactTrueCloudModel
                    TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(contact.firstName)
                }.toSortedMap()
                    .map { contact ->
                        listOf(
                            HeaderSelectionModel(
                                key = contact.key,
                                size = contact.value.size.inc()
                            )
                        ).plus(contact.value)
                    }.flatten()
                    .let { _contacts ->
                        emit(_contacts)
                    }
            }.getOrElse {
                emit(contacts)
            }
        }
    }
}
