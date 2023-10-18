package com.truedigital.common.share.datalegacy.domain.verifydevice.usecase

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import javax.inject.Inject

interface VerifyTrustDeviceOwnerUseCase {
    fun execute(): String
}

class VerifyTrustDeviceOwnerUseCaseImpl @Inject constructor(private val userRepository: UserRepository) :
    VerifyTrustDeviceOwnerUseCase {
    override fun execute(): String {
        return userRepository.getTrustedOwner().ifEmpty { "0" }
    }
}
