package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class TuningFacadeImplTest {

    private lateinit var tuningFacade: TuningFacadeImpl
    private val stationRepository: StationRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()

    @BeforeEach
    fun setup() {
        tuningFacade = TuningFacadeImpl(stationRepository, musicUserRepository)
    }

    @Test
    fun getStationTrackVotes_stationNotNull_verifyGetVotesStationId() {
        whenever(musicUserRepository.get()).thenReturn(Single.just(MockDataModel.mockUserTuned))
        whenever(stationRepository.getVotes(1, 1)).thenReturn(
            Single.just(
                listOf(
                    getLikedTrack(),
                    getLikedTrack()
                )
            )
        )

        tuningFacade.getStationTrackVotes(
            Station(
                id = 1,
                type = Station.StationType.ARTIST,
                name = listOf(),
                description = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "",
                isActive = false
            )
        ).test()
            .assertValue { likedTrackList ->
                likedTrackList.size == 2
            }

        verify(musicUserRepository, times(1)).get()
        verify(stationRepository, times(1)).getVotes(1, 1)
    }

    @Test
    fun getStationTrackVotes_stationNull_verifyGetVotesStationIdDefault() {
        whenever(musicUserRepository.get()).thenReturn(Single.just(MockDataModel.mockUserTuned))
        whenever(stationRepository.getVotes(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getLikedTrack(),
                    getLikedTrack()
                )
            )
        )

        tuningFacade.getStationTrackVotes(null)
            .test()
            .assertValue { likedTrackList ->
                likedTrackList.size == 2
            }

        verify(musicUserRepository, times(1)).get()
        verify(stationRepository, times(1)).getVotes(0, 1)
    }

    @Test
    fun deleteVote_stationNotNull_trackNotNull_verifyDeleteVote() {
        whenever(stationRepository.deleteVote(any(), any())).thenReturn(
            Single.just(
                listOf(
                    StationVote(
                        Vote(
                            id = 1,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    ),
                    StationVote(
                        Vote(
                            id = 2,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    )
                )
            )
        )

        tuningFacade.deleteVote(
            Station(
                id = 1,
                type = Station.StationType.ARTIST,
                name = listOf(),
                description = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "",
                isActive = false
            ),
            MockDataModel.mockTrack
        )
            .test()
            .assertValue { vote ->
                vote.id == 1
            }

        verify(stationRepository, times(1)).deleteVote(1, 1)
    }

    @Test
    fun deleteVote_stationNull_verifyDeleteVoteStationIdDefault() {
        whenever(stationRepository.deleteVote(any(), any())).thenReturn(
            Single.just(
                listOf(
                    StationVote(
                        Vote(
                            id = 1,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    ),
                    StationVote(
                        Vote(
                            id = 2,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    )
                )
            )
        )

        tuningFacade.deleteVote(null, MockDataModel.mockTrack)
            .test()
            .assertValue { vote ->
                vote.id == 1
            }

        verify(stationRepository, times(1)).deleteVote(0, 1)
    }

    @Test
    fun deleteVote_trackNull_verifyDeleteVoteTrackIdDefault() {
        whenever(stationRepository.deleteVote(any(), any())).thenReturn(
            Single.just(
                listOf(
                    StationVote(
                        Vote(
                            id = 1,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    ),
                    StationVote(
                        Vote(
                            id = 2,
                            vote = "vote",
                            type = "type",
                            actionDate = Date()
                        ),
                        success = true
                    )
                )
            )
        )

        tuningFacade.deleteVote(
            Station(
                id = 1,
                type = Station.StationType.ARTIST,
                name = listOf(),
                description = listOf(),
                coverImage = listOf(),
                bannerImage = listOf(),
                bannerURL = "",
                isActive = false
            ),
            null
        )
            .test()
            .assertValue { vote ->
                vote.id == 1
            }

        verify(stationRepository, times(1)).deleteVote(1, 0)
    }

    private fun getLikedTrack(): LikedTrack {
        return LikedTrack(
            Rating.DISLIKED,
            MockDataModel.mockTrack,
            artists = listOf()
        )
    }
}
