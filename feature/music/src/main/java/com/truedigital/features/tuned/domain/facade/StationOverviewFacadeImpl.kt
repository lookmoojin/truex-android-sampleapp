package com.truedigital.features.tuned.domain.facade

import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.presentation.station.facade.StationOverviewFacade
import io.reactivex.Single
import javax.inject.Inject

class StationOverviewFacadeImpl @Inject constructor(
    val artistRepository: ArtistRepository,
    val stationRepository: StationRepository,
    val authenticationTokenRepository: AuthenticationTokenRepository
) : StationOverviewFacade {

    override fun loadFeaturedArtists(station: Station?): Single<List<Artist>> =
        artistRepository.getStationTrending(station?.id ?: 0)

    override fun loadSimilarStations(station: Station?): Single<List<Station>> =
        stationRepository.getSimilar(station?.id ?: 0)

    override fun getHasArtistShuffleRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasArtistShuffleRight ?: false
}
