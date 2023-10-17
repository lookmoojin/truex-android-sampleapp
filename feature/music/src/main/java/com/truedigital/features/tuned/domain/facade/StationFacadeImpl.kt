package com.truedigital.features.tuned.domain.facade

import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.presentation.station.facade.StationFacade
import io.reactivex.Single
import javax.inject.Inject

class StationFacadeImpl @Inject constructor(
    val stationRepository: StationRepository,
    val musicUserRepository: MusicUserRepository
) : StationFacade {

    override fun loadStation(stationId: Int): Single<Station> = stationRepository.get(stationId)

    override fun loadFavourited(station: Station): Single<Boolean> =
        stationRepository.isFavourited(station.id)

    override fun toggleFavourite(station: Station): Single<Any> =
        loadFavourited(station).flatMap { isFavourite ->
            if (isFavourite) {
                stationRepository.removeFavourite(station.id)
            } else {
                stationRepository.addFavourite(station.id)
            }
        }

    override fun clearVotes(stationId: Int): Single<Any> = stationRepository.deleteVotes(stationId)
}
