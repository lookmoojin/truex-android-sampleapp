package com.truedigital.features.music.domain.favorite

import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import javax.inject.Inject

interface FetchMusicFavoriteUseCase {
    fun execute(id: Int, favType: FavoriteType): Single<Boolean>
}

class FetchMusicFavoriteUseCaseImpl @Inject constructor(
    private val trackRepository: TrackRepository
) : FetchMusicFavoriteUseCase {

    override fun execute(id: Int, favType: FavoriteType): Single<Boolean> {
        return when (favType) {
            FavoriteType.TRACK -> trackRepository.isFavourited(id)
            else -> Single.error(Throwable("Unknown favorite type"))
        }
    }
}
