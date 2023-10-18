package com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

interface GetAmityConfigUseCase {
    suspend fun execute(): Boolean
}

class GetAmityConfigUseCaseImpl @Inject constructor(
    private val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetAmityConfigUseCase {

    companion object {
        const val AMITY_SERVICE_KEY = "amity_service"
        const val AMITY_SERVICE_ENABLE = "enable"
        const val AMITY_SERVICE_ANDROID = "android"
    }

    override suspend fun execute(): Boolean {
        val config = initialAppConfigRepository.getConfigByKey(
            key = AMITY_SERVICE_KEY,
            countryCode = localizationRepository.getAppCountryCode()
        ) as? Map<*, *>
        return config?.let { _config ->
            val enable = _config[AMITY_SERVICE_ENABLE] as? Map<*, *>
            enable?.get(AMITY_SERVICE_ANDROID) as? Boolean ?: false
        } ?: false
    }
}
