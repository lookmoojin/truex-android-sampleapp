package com.truedigital.features.music.domain.favorite

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchMusicFavoriteUseCaseTest {
    private lateinit var fetchMusicFavoriteUseCase: FetchMusicFavoriteUseCaseImpl
    private val trackRepository: TrackRepository = mock()

    @BeforeEach
    fun setup() {
        fetchMusicFavoriteUseCase = FetchMusicFavoriteUseCaseImpl(
            trackRepository
        )
    }

    @Test
    fun testFetchFavSong_returnTrue() {
        whenever(trackRepository.isFavourited(any())).thenReturn(
            Single.just(true)
        )

        fetchMusicFavoriteUseCase.execute(1, FavoriteType.TRACK)
            .test()
            .assertValue {
                it
            }
    }

    @Test
    fun testFetchFavSong_returnFalse() {
        whenever(trackRepository.isFavourited(any())).thenReturn(
            Single.just(false)
        )
        fetchMusicFavoriteUseCase.execute(1, FavoriteType.TRACK)
            .test()
            .assertValue {
                !it
            }
    }

    @Test
    fun testFetchFavSong_thenError() {
        whenever(trackRepository.isFavourited(any())).thenReturn(
            Single.just(false)
        )
        fetchMusicFavoriteUseCase.execute(1, FavoriteType.UNKNOWN)
            .test()
            .assertErrorMessage("Unknown favorite type")
    }
}
