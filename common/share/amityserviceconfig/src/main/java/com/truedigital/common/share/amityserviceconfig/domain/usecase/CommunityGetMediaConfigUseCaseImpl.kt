package com.truedigital.common.share.amityserviceconfig.domain.usecase

import com.truedigital.common.share.amityserviceconfig.domain.repository.MediaConfigRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CommunityGetMediaConfigUseCase {
    fun execute(): Flow<Boolean>
}

class CommunityGetMediaConfigUseCaseImpl @Inject constructor(
    private val mediaConfigRepository: MediaConfigRepository,
    private val localizationRepository: LocalizationRepository
) : CommunityGetMediaConfigUseCase {
    override fun execute(): Flow<Boolean> {
        return mediaConfigRepository.getFeatureConfigMediaConfig(
            localizationRepository.getAppCountryCode()
        )
    }
}
