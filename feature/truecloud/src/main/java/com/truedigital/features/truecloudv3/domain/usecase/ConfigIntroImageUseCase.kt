package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.data.device.model.LocalizationModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_GET_FIREBASE_CONFIG
import com.truedigital.features.truecloudv3.data.repository.ConfigIntroImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ConfigIntroImageUseCase {
    fun execute(isTablet: Boolean): Flow<String>
}

class ConfigIntroImageUseCaseImpl @Inject constructor(
    private val configIntroImageRepository: ConfigIntroImageRepository,
    private val localizationRepository: LocalizationRepository
) : ConfigIntroImageUseCase {

    override fun execute(isTablet: Boolean): Flow<String> {
        return flow {
            configIntroImageRepository.getConfig(
                countryCode = localizationRepository.getAppCountryCode(),
                isTablet = isTablet
            )?.let { introConfig ->
                val introImage = localizationRepository.localize(
                    LocalizationModel().apply {
                        thWord = introConfig.th
                        enWord = introConfig.en
                        myWord = introConfig.my
                    }
                )
                emit(introImage)
            } ?: error(ERROR_GET_FIREBASE_CONFIG)
        }
    }
}
