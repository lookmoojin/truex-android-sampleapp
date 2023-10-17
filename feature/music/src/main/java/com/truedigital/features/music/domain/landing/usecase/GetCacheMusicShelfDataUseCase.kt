package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import javax.inject.Inject

interface GetCacheMusicShelfDataUseCase {
    fun execute(baseShelfId: String?): Pair<String, MutableList<MusicForYouShelfModel>>?
}

class GetCacheMusicShelfDataUseCaseImpl @Inject constructor(
    private val cacheMusicLandingRepository: CacheMusicLandingRepository
) : GetCacheMusicShelfDataUseCase {

    override fun execute(baseShelfId: String?): Pair<String, MutableList<MusicForYouShelfModel>>? {
        return baseShelfId?.let {
            if (it.isNotEmpty()) {
                cacheMusicLandingRepository.loadMusicShelfData(it)
            } else {
                null
            }
        }
    }
}
