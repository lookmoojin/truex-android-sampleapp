package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface HasContactSyncedUseCase {
    fun execute(): Flow<Boolean>
}

class HasContactSyncedUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository
) : HasContactSyncedUseCase {
    override fun execute(): Flow<Boolean> {
        return flow {
            emit(contactRepository.hasContactSynced())
        }
    }
}
