package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import javax.inject.Inject

interface SaveCacheMusicShelfDataUseCase {
    fun execute(baseShelfId: String, shelves: MutableList<MusicForYouShelfModel>)
}

class SaveCacheMusicShelfDataUseCaseImpl @Inject constructor(
    private val cacheMusicLandingRepository: CacheMusicLandingRepository
) : SaveCacheMusicShelfDataUseCase {

    override fun execute(baseShelfId: String, shelves: MutableList<MusicForYouShelfModel>) {
        cacheMusicLandingRepository.saveMusicShelfData(baseShelfId, shelves)
    }
}
