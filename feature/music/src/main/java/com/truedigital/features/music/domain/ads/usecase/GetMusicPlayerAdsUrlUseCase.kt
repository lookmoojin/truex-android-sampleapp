package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMusicPlayerAdsUrlUseCase {
    fun execute(): Flow<String>
}

class GetMusicPlayerAdsUrlUseCaseImpl @Inject constructor(
    private val musicConfigRepository: MusicConfigRepository
) : GetMusicPlayerAdsUrlUseCase {

    override fun execute(): Flow<String> {
        return musicConfigRepository.getAdsBannerPlayerConfig()
            .map { adsBannerPlayer ->
                if (adsBannerPlayer?.enable?.android == true) {
                    adsBannerPlayer.urlAds
                } else {
                    ""
                }
            }
    }
}
