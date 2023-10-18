package com.truedigital.features.music.domain.track.usecase

import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import javax.inject.Inject

interface GetTrackUseCase {
    fun execute(id: Int): Single<Track>
}

class GetTrackUseCaseImpl @Inject constructor(private val trackRepository: TrackRepository) : GetTrackUseCase {

    override fun execute(id: Int): Single<Track> {
        return trackRepository.get(id)
    }
}
