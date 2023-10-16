package com.truedigital.common.share.amityserviceconfig.domain.usecase

import com.truedigital.common.share.amityserviceconfig.domain.repository.CommunityGetRegexRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CommunityGetRegexUseCase {
    fun execute(): Flow<String>
}

class CommunityGetRegexUseCaseImpl @Inject constructor(
    private val communityGetRegexRepository: CommunityGetRegexRepository,
    private val localizationRepository: LocalizationRepository
) : CommunityGetRegexUseCase {

    override fun execute(): Flow<String> {
        return communityGetRegexRepository.getFeatureConfigRegex(
            localizationRepository.getAppCountryCode()
        )
    }
}
