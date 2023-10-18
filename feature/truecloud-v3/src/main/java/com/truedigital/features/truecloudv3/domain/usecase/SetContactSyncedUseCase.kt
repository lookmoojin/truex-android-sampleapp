package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import javax.inject.Inject

interface SetContactSyncedUseCase {
    suspend fun execute()
}

class SetContactSyncedUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository
) : SetContactSyncedUseCase {
    override suspend fun execute() {
        return contactRepository.setContactSynced(true)
    }
}
