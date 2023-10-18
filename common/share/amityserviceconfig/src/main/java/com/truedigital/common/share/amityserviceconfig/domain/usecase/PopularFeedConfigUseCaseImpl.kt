package com.truedigital.common.share.amityserviceconfig.domain.usecase

import com.truedigital.common.share.amityserviceconfig.domain.repository.PopularFeedConfigRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PopularFeedConfigUseCase {
    fun execute(): Flow<Boolean>
}

class PopularFeedConfigUseCaseImpl @Inject constructor(
    private val popularFeedConfigRepository: PopularFeedConfigRepository,
    private val localizationRepository: LocalizationRepository
) : PopularFeedConfigUseCase {

    override fun execute(): Flow<Boolean> {
        return popularFeedConfigRepository.getFeatureConfigPopularFeed(
            localizationRepository.getAppCountryCode()
        )
    }
}
