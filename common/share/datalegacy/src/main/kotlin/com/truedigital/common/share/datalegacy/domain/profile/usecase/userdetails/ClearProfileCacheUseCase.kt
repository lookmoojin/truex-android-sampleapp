package com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails

import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import javax.inject.Inject

interface ClearProfileCacheUseCase {
    fun execute()
}

class ClearProfileCacheUseCaseImpl @Inject constructor(private val profileRepository: ProfileRepository) :
    ClearProfileCacheUseCase {
    override fun execute() {
        return profileRepository.clearCache()
    }
}
