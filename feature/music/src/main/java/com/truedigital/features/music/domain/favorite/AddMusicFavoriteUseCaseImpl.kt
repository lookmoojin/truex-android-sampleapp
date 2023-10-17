package com.truedigital.features.music.domain.favorite

import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import javax.inject.Inject

interface AddMusicFavoriteUseCase {
    fun execute(id: Int, favType: FavoriteType): Single<Any>
}

class AddMusicFavoriteUseCaseImpl @Inject constructor(
    private val trackRepository: TrackRepository
) : AddMusicFavoriteUseCase {

    override fun execute(id: Int, favType: FavoriteType): Single<Any> {
        return when (favType) {
            FavoriteType.TRACK -> trackRepository.addFavourite(id)
            else -> Single.error(Throwable("Unknown favorite type"))
        }
    }
}
