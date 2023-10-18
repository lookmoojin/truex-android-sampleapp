package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.FAMusicLandingShelfModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import javax.inject.Inject

interface GetDataForTrackFAMusicLandingPageUseCase {
    fun execute(
        baseShelfId: String,
        musicForYouShelf: MusicForYouShelfModel,
        isFirstLoad: Boolean
    ): FAMusicLandingShelfModel?
}

class GetDataForTrackFAMusicLandingPageUseCaseImpl @Inject constructor(
    private val cacheMusicLandingRepository: CacheMusicLandingRepository,
    private val localizationRepository: LocalizationRepository
) : GetDataForTrackFAMusicLandingPageUseCase {

    override fun execute(
        baseShelfId: String,
        musicForYouShelf: MusicForYouShelfModel,
        isFirstLoad: Boolean
    ): FAMusicLandingShelfModel? {

        if (isFirstLoad) {
            cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange(
                countryCode = localizationRepository.getAppCountryCode(),
                languageCode = localizationRepository.getAppLanguageCode()
            )
        }

        val faShelfList = cacheMusicLandingRepository.getFAShelfList(baseShelfId)
        return if (faShelfList.contains(musicForYouShelf.index).not()) {
            cacheMusicLandingRepository.saveFAShelf(baseShelfId, musicForYouShelf.index)
            FAMusicLandingShelfModel(
                shelfName = musicForYouShelf.titleFA,
                shelfIndex = musicForYouShelf.shelfIndex
            )
        } else {
            null
        }
    }
}
