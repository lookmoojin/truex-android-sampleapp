package com.truedigital.features.music.domain.favorite

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddMusicFavoriteUseCaseTest {
    private lateinit var addMusicFavoriteUseCase: AddMusicFavoriteUseCase
    private val trackRepository: TrackRepository = mock()

    @BeforeEach
    fun setup() {
        addMusicFavoriteUseCase = AddMusicFavoriteUseCaseImpl(
            trackRepository
        )
    }

    @Test
    fun testAddFavSong_returnTrue() {
        whenever(trackRepository.addFavourite(any())).thenReturn(
            Single.just(Any())
        )

        addMusicFavoriteUseCase.execute(1, FavoriteType.TRACK)
            .test()
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun testAddFavSong_thenError() {
        whenever(trackRepository.addFavourite(any())).thenReturn(
            Single.just(Any())
        )
        addMusicFavoriteUseCase.execute(1, FavoriteType.UNKNOWN)
            .test()
            .assertErrorMessage("Unknown favorite type")
    }
}
