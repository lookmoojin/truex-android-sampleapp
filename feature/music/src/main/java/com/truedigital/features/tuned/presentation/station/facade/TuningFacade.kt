package com.truedigital.features.tuned.presentation.station.facade

import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface TuningFacade {
    fun getStationTrackVotes(station: Station?): Single<List<LikedTrack>>
    fun deleteVote(station: Station?, track: Track?): Single<Vote>
}
