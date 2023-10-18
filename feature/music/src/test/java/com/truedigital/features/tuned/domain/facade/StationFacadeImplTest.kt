package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StationFacadeImplTest {

    private lateinit var stationFacadeImpl: StationFacadeImpl
    private val stationRepository: StationRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()

    @BeforeEach
    fun setUp() {
        stationFacadeImpl = StationFacadeImpl(stationRepository, musicUserRepository)
    }

    @Test
    fun testLoadStation_verifyStationRepositoryGetData() {
        val mockId = 1
        whenever(stationRepository.get(any(), any()))
            .thenReturn(
                Single.just(
                    Station(
                        id = 1,
                        type = Station.StationType.ARTIST,
                        description = listOf(),
                        name = listOf(),
                        coverImage = listOf(),
                        bannerImage = listOf(),
                        bannerURL = "bannerURL",
                        isActive = true
                    )
                )
            )

        stationFacadeImpl.loadStation(mockId)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).get(mockId)
    }

    @Test
    fun testLoadFavourite_isFavourite_verifyStationRepositoryIsFavourite() {
        val mockId = 1
        whenever(stationRepository.isFavourited(any())).thenReturn(Single.just(true))

        stationFacadeImpl.loadFavourited(
            Station(
                id = mockId,
                type = Station.StationType.ARTIST,
                description = listOf(),
                name = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "bannerURL",
                isActive = true
            )
        )
            .test()
            .assertNoErrors()
            .assertValue { it }

        verify(stationRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testToggleFavourite_isFavourite_verifyRemoveFavourite() {
        val mockId = 1
        whenever(stationRepository.isFavourited(any())).thenReturn(Single.just(true))
        whenever(stationRepository.addFavourite(any())).thenReturn(Single.just(Any()))
        whenever(stationRepository.removeFavourite(any())).thenReturn(Single.just(Any()))

        stationFacadeImpl.toggleFavourite(
            Station(
                id = mockId,
                type = Station.StationType.ARTIST,
                description = listOf(),
                name = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "bannerURL",
                isActive = true
            )
        )
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).removeFavourite(mockId)
        verify(stationRepository, never()).addFavourite(mockId)
    }

    @Test
    fun testToggleFavourite_isNotFavourite_verifyAddFavourite() {
        val mockId = 1
        whenever(stationRepository.isFavourited(any())).thenReturn(Single.just(false))
        whenever(stationRepository.addFavourite(any())).thenReturn(Single.just(Any()))
        whenever(stationRepository.removeFavourite(any())).thenReturn(Single.just(Any()))

        stationFacadeImpl.toggleFavourite(
            Station(
                id = mockId,
                type = Station.StationType.ARTIST,
                description = listOf(),
                name = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "bannerURL",
                isActive = true
            )
        )
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).addFavourite(mockId)
        verify(stationRepository, never()).removeFavourite(mockId)
    }

    @Test
    fun testClearVotes_verifyStationRepositoryDeleteVotes() {
        val mockId = 1
        whenever(stationRepository.deleteVotes(any())).thenReturn(Single.just(Any()))

        stationFacadeImpl.clearVotes(mockId)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).deleteVotes(mockId)
    }
}
