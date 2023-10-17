package com.truedigital.features.tuned.domain.facade

import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.presentation.station.facade.TuningFacade
import io.reactivex.Single
import javax.inject.Inject

class TuningFacadeImpl @Inject constructor(
    private val stationRepository: StationRepository,
    private val musicUserRepository: MusicUserRepository
) : TuningFacade {
    override fun getStationTrackVotes(station: Station?): Single<List<LikedTrack>> =
        musicUserRepository.get().flatMap { stationRepository.getVotes(station?.id ?: 0, it.userId) }

    override fun deleteVote(station: Station?, track: Track?): Single<Vote> =
        stationRepository.deleteVote(station?.id ?: 0, track?.id ?: 0)
            .map { it.first().vote }
}
