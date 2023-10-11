package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.Contact
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetGroupAlphabetContactUseCase {
    fun execute(contacts: List<Contact>): Flow<List<AlphabetItemModel>>
}

class GetGroupAlphabetContactUseCaseImpl @Inject constructor() : GetGroupAlphabetContactUseCase {

    companion object {
        private const val MINIMUM_OF_GROUP_ALPHABET = 1
    }

    override fun execute(contacts: List<Contact>) = flow {
        val alphabets = mutableListOf<AlphabetItemModel>()
        contacts.mapIndexed { index, contact ->
            if (contact is HeaderSelectionModel) {
                alphabets.add(
                    AlphabetItemModel(
                        alphabet = contact.key,
                        index = alphabets.size,
                        position = index,
                        isActive = false,
                        size = contact.size
                    )
                )
            }
        }
        if (alphabets.size > MINIMUM_OF_GROUP_ALPHABET) {
            emit(alphabets)
        } else {
            emit(listOf())
        }
    }
}
