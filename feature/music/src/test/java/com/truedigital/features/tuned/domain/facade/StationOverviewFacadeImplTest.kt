package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StationOverviewFacadeImplTest {

    private lateinit var stationOverviewFacadeImpl: StationOverviewFacadeImpl
    private val artistRepository: ArtistRepository = mock()
    private val stationRepository: StationRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()

    @BeforeEach
    fun setUp() {
        stationOverviewFacadeImpl = StationOverviewFacadeImpl(
            artistRepository,
            stationRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun testLoadFeaturedArtists_stationIsNull_verifyGetStationTrendingIdIsZero() {
        whenever(artistRepository.getStationTrending(any()))
            .thenReturn(
                Single.just(
                    listOf(
                        Artist(
                            id = 1,
                            name = "name",
                            image = "image"
                        )
                    )
                )
            )

        stationOverviewFacadeImpl.loadFeaturedArtists(null)
            .test()
            .assertNoErrors()
            .assertValue {
                it.isNotEmpty()
            }

        verify(artistRepository, times(1)).getStationTrending(0)
    }

    @Test
    fun testLoadFeaturedArtists_stationIdIsNotNull_verifyGetStationTrendingIdIsNotZero() {
        val mockId = 1
        whenever(artistRepository.getStationTrending(any()))
            .thenReturn(
                Single.just(
                    listOf(
                        Artist(
                            id = 1,
                            name = "name",
                            image = "image"
                        )
                    )
                )
            )

        stationOverviewFacadeImpl.loadFeaturedArtists(
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
            .assertValue {
                it.isNotEmpty()
            }

        verify(artistRepository, times(1)).getStationTrending(mockId)
    }

    @Test
    fun testLoadSimilarStations_stationIsNull_verifyGetStationTrendingIdIsZero() {
        whenever(stationRepository.getSimilar(any()))
            .thenReturn(
                Single.just(
                    listOf(
                        Station(
                            id = 2,
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
            )

        stationOverviewFacadeImpl.loadSimilarStations(null)
            .test()
            .assertNoErrors()
            .assertValue {
                it.isNotEmpty()
            }

        verify(stationRepository, times(1)).getSimilar(0)
    }

    @Test
    fun testLoadSimilarStations_stationIdIsNotNull_verifyGetStationTrendingIdIsNotZero() {
        val mockId = 1
        whenever(stationRepository.getSimilar(any()))
            .thenReturn(
                Single.just(
                    listOf(
                        Station(
                            id = 2,
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
            )

        stationOverviewFacadeImpl.loadSimilarStations(
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
            .assertValue {
                it.isNotEmpty()
            }

        verify(stationRepository, times(1)).getSimilar(mockId)
    }

    @Test
    fun testGetHasArtistShuffleRight_getCurrentTokenIsNotNull_accessTokenIsNull_returnHasArtistShuffleRight() {
        val mock =
            AuthenticationToken(refreshToken = "refreshToken", expiration = 1L, accessToken = null)
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(mock)

        val test = stationOverviewFacadeImpl.getHasArtistShuffleRight()

        Assertions.assertFalse(test)
    }

    @Test
    fun testGetHasArtistShuffleRight_getCurrentTokenIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        val test = stationOverviewFacadeImpl.getHasArtistShuffleRight()

        Assertions.assertFalse(test)
    }
}
