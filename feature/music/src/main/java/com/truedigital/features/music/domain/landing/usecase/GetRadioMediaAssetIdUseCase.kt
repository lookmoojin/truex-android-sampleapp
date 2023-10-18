package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import javax.inject.Inject

interface GetRadioMediaAssetIdUseCase {
    fun execute(radioId: String): Int
}

class GetRadioMediaAssetIdUseCaseImpl @Inject constructor(
    private val musicPlayerCacheRepository: MusicPlayerCacheRepository
) : GetRadioMediaAssetIdUseCase {

    override fun execute(radioId: String): Int {
        val radioMediaAssetList = musicPlayerCacheRepository.getRadioMediaAssetIdList()
        val indexOfRadioId = radioMediaAssetList.indexOfFirst { it == radioId }

        return if (indexOfRadioId == MusicConstant.Index.NOT_FOUND_INDEX) {
            musicPlayerCacheRepository.setRadioMediaAssetId(radioId)
            musicPlayerCacheRepository.getRadioMediaAssetIdList().lastIndex
        } else {
            indexOfRadioId
        }
    }
}
