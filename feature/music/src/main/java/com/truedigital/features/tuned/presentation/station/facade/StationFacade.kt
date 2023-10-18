package com.truedigital.features.tuned.presentation.station.facade

import com.truedigital.features.tuned.data.station.model.Station
import io.reactivex.Single

interface StationFacade {
    fun loadStation(stationId: Int): Single<Station>
    fun toggleFavourite(station: Station): Single<Any>
    fun loadFavourited(station: Station): Single<Boolean>
    fun clearVotes(stationId: Int): Single<Any>
}
