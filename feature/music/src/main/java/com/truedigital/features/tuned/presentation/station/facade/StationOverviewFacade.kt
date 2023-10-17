package com.truedigital.features.tuned.presentation.station.facade

import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.station.model.Station
import io.reactivex.Single

interface StationOverviewFacade {
    fun loadFeaturedArtists(station: Station?): Single<List<Artist>>
    fun loadSimilarStations(station: Station?): Single<List<Station>>
    fun getHasArtistShuffleRight(): Boolean
}
