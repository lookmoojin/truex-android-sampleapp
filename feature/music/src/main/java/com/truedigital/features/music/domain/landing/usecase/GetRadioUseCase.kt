package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.extensions.toSettingModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetRadioUseCase {
    fun execute(id: String): Flow<MusicForYouItemModel.RadioShelfItem?>
}

class GetRadioUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository,
    private val musicLandingRepository: MusicLandingRepository,
    private val mapRadioUseCase: MapRadioUseCase
) : GetRadioUseCase {

    companion object {
        private const val FIELDS = "setting"
    }

    override fun execute(id: String): Flow<MusicForYouItemModel.RadioShelfItem?> {
        return musicLandingRepository.getCmsContentDetails(
            cmsId = id,
            country = localizationRepository.getAppCountryCode(),
            lang = localizationRepository.getAppLanguageCode(),
            fields = FIELDS
        ).map {
            val setting = it?.data?.setting?.toSettingModel()

            if (setting != null && setting.streamUrl.isNullOrEmpty().not()) {
                mapRadioUseCase.execute(
                    id = it.data?.id.orEmpty(),
                    index = 0,
                    setting = setting
                )
            } else {
                null
            }
        }
    }
}
