package com.truedigital.features.music.domain.favorite

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoveMusicFavoriteUseCaseTest {
    private lateinit var removeMusicFavoriteUseCase: RemoveMusicFavoriteUseCaseImpl
    private val trackRepository: TrackRepository = mock()

    @BeforeEach
    fun setup() {
        removeMusicFavoriteUseCase = RemoveMusicFavoriteUseCaseImpl(
            trackRepository
        )
    }

    @Test
    fun testRemoveFavSong_thenComplete() {
        whenever(trackRepository.removeFavourite(any())).thenReturn(
            Single.just(Any())
        )

        removeMusicFavoriteUseCase.execute(1, FavoriteType.TRACK)
            .test()
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun testRemoveFavSong_thenError() {
        whenever(trackRepository.removeFavourite(any())).thenReturn(
            Single.just(Any())
        )
        removeMusicFavoriteUseCase.execute(1, FavoriteType.UNKNOWN)
            .test()
            .assertErrorMessage("Unknown favorite type")
    }
}
