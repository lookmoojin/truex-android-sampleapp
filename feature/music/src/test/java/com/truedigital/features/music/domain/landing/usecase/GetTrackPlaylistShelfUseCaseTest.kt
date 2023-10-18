package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetTrackPlaylistShelfUseCaseTest {

    private lateinit var getTrackPlaylistShelfUseCase: GetTrackPlaylistShelfUseCase
    private val musicLandingRepository: MusicLandingRepository = mock()
    private val cacheTrackQueueRepository: CacheTrackQueueRepository = mock()
    private val playlistTrackResultResponseMock = PlaylistTrackResponse.Result(
        artist = listOf(
            PlaylistTrackResponse.Result.Artist(
                123,
                "artist Name"
            )
        ),
        image = "image_path",
        name = "playlist Track 01",
        playlistTrackId = 12345,
        trackId = 23600,
        translations = listOf(
            Translation(
                language = "TH",
                value = "playlist Track 01"
            )
        )
    )
    private val mockTrackPlaylistShelf = MusicForYouItemModel.TrackPlaylistShelf(
        index = 0,
        artist = "artist Name",
        playlistTrackId = 12345,
        playlistId = 1234,
        coverImage = "image_path",
        name = "playlist Track 01",
        trackId = 23600,
        trackIdList = listOf(23600),
        position = ""
    )

    @BeforeEach
    fun setup() {
        getTrackPlaylistShelfUseCase = GetTrackPlaylistShelfUseCaseImpl(
            musicLandingRepository = musicLandingRepository,
            cacheTrackQueueRepository = cacheTrackQueueRepository
        )
    }

    @Test
    fun testLoadPlaylistTrack_limitIsNotEmpty_returnList() = runTest {
        val mockLimit = 12

        whenever(musicLandingRepository.getPlaylistTrackShelf(any(), any(), any())).thenReturn(
            flowOf(
                PlaylistTrackResponse(
                    10,
                    1,
                    listOf(playlistTrackResultResponseMock),
                    10
                )
            )
        )

        getTrackPlaylistShelfUseCase.execute("1234", mockLimit.toString(), "")
            .collectSafe {
                assertEquals(listOf(mockTrackPlaylistShelf), it)
            }

        verify(musicLandingRepository, times(1)).getPlaylistTrackShelf("1234", 1, mockLimit)
    }

    @Test
    fun testLoadPlaylistTrack_limitIsEmpty_displayTypeIsEmpty_returnList() = runTest {
        whenever(musicLandingRepository.getPlaylistTrackShelf(any(), any(), any())).thenReturn(
            flowOf(
                PlaylistTrackResponse(
                    10,
                    1,
                    listOf(playlistTrackResultResponseMock),
                    10
                )
            )
        )

        getTrackPlaylistShelfUseCase.execute("1234", "", "")
            .collectSafe {
                assertEquals(listOf(mockTrackPlaylistShelf), it)
            }

        verify(musicLandingRepository, times(1)).getPlaylistTrackShelf("1234", 1, 12)
    }

    @Test
    fun testLoadPlaylistTrack_limitIsEmpty_displayTypeVertical_returnList() = runTest {
        val mockPosition = "1"
        val mockExpectedShelf = mockTrackPlaylistShelf.copy(position = mockPosition)
        whenever(musicLandingRepository.getPlaylistTrackShelf(any(), any(), any())).thenReturn(
            flowOf(
                PlaylistTrackResponse(
                    10,
                    1,
                    listOf(playlistTrackResultResponseMock.copy(position = mockPosition)),
                    10
                )
            )
        )

        getTrackPlaylistShelfUseCase.execute("1234", "", com.truedigital.features.listens.share.constant.MusicConstant.Display.VERTICAL_LIST)
            .collectSafe {
                assertEquals(listOf(mockExpectedShelf), it)
            }

        verify(musicLandingRepository, times(1)).getPlaylistTrackShelf("1234", 1, 15)
    }

    @Test
    fun loadPlaylistTrack_successVerticalDisplay_resultItem() = runTest {
        val mockPosition = "1"
        val mockExpectedShelf = mockTrackPlaylistShelf.copy(position = mockPosition)

        whenever(musicLandingRepository.getPlaylistTrackShelf(any(), any(), any())).thenReturn(
            flowOf(
                PlaylistTrackResponse(
                    10,
                    1,
                    listOf(playlistTrackResultResponseMock.copy(position = mockPosition)),
                    10
                )
            )
        )

        getTrackPlaylistShelfUseCase.execute("1234", "12", "verticallist")
            .collectSafe {
                assertEquals(listOf(mockExpectedShelf), it)
            }
    }

    @Test
    fun `test playlist id empty return_error`() = runTest {
        getTrackPlaylistShelfUseCase.execute("", "12", "")
            .catch { exception ->
                assertEquals(
                    "playlist id for you shelf error is empty",
                    exception.message
                )
            }.collect()
    }
}
