package com.truedigital.features.tuned.data.track.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.api.track.TrackMetadataApiInterface
import com.truedigital.features.tuned.api.track.TrackServiceApiInterface
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.model.response.RawLyricString
import com.truedigital.features.tuned.data.track.model.response.TrackContext
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TrackRepositoryTest {

    private val trackMetadataApi: TrackMetadataApiInterface = mock()
    private val trackServiceApi: TrackServiceApiInterface = mock()
    private lateinit var trackRepository: TrackRepository

    private val mockTrack = Track(
        id = 1,
        playlistTrackId = 1,
        songId = 1,
        releaseId = 1,
        artists = listOf(),
        name = "name",
        originalCredit = "originalCredit",
        isExplicit = false,
        trackNumber = 1,
        trackNumberInVolume = 1,
        volumeNumber = 1,
        releaseArtists = listOf(),
        sample = "sample",
        isOnCompilation = false,
        releaseName = "releaseName",
        allowDownload = false,
        allowStream = false,
        duration = 3L,
        image = "image",
        hasLyrics = false,
        video = null,
        isVideo = false,
        vote = null,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false,
        translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
    )

    @BeforeEach
    fun setUp() {
        trackRepository = TrackRepositoryImpl(trackMetadataApi, trackServiceApi)
    }

    @Test
    fun testGet_translationListHaveThValue_returnTrackNameTh() {
        whenever(trackMetadataApi.get(any())).thenReturn(Single.just(mockTrack))

        trackRepository.get(1)
            .test()
            .assertNoErrors()
            .assertValue { track ->
                track.id == mockTrack.id &&
                    track.playlistTrackId == mockTrack.playlistTrackId &&
                    track.name == mockTrack.translationsList.first().value
            }

        verify(trackMetadataApi, times(1)).get(any())
    }

    @Test
    fun testGet_translationListHaveNotThValue_returnTrackName() {
        val mockTrackData = mockTrack.copy(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_EN,
                    value = "nameEN"
                )
            )
        )

        whenever(trackMetadataApi.get(any())).thenReturn(Single.just(mockTrackData))

        trackRepository.get(1)
            .test()
            .assertNoErrors()
            .assertValue { track ->
                track.id == mockTrackData.id &&
                    track.playlistTrackId == mockTrackData.playlistTrackId &&
                    track.name == mockTrackData.name
            }

        verify(trackMetadataApi, times(1)).get(any())
    }

    @Test
    fun testGet_translationListIsEmpty_returnTrackName() {
        val mockTrackData = mockTrack.copy(translationsList = emptyList())

        whenever(trackMetadataApi.get(any())).thenReturn(Single.just(mockTrackData))

        trackRepository.get(1)
            .test()
            .assertNoErrors()
            .assertValue { track ->
                track.id == mockTrackData.id &&
                    track.playlistTrackId == mockTrackData.playlistTrackId &&
                    track.name == mockTrackData.name
            }

        verify(trackMetadataApi, times(1)).get(any())
    }

    @Test
    fun testGetList_translationListHaveThValue_returnTrackNameTh() {
        whenever(trackMetadataApi.getMultiple(any())).thenReturn(Single.just(listOf(mockTrack)))

        trackRepository.getList(listOf(1))
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track.id == mockTrack.id &&
                    track.playlistTrackId == mockTrack.playlistTrackId &&
                    track.name == mockTrack.translationsList.first().value
            }

        verify(trackMetadataApi, times(1)).getMultiple(any())
    }

    @Test
    fun testGetList_translationListHaveNotThValue_returnTrackName() {
        val mockTrackData = mockTrack.copy(
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_EN,
                    value = "nameEN"
                )
            )
        )

        whenever(trackMetadataApi.getMultiple(any())).thenReturn(Single.just(listOf(mockTrackData)))

        trackRepository.getList(listOf(1))
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track.id == mockTrackData.id &&
                    track.playlistTrackId == mockTrackData.playlistTrackId &&
                    track.name == mockTrackData.name
            }

        verify(trackMetadataApi, times(1)).getMultiple(any())
    }

    @Test
    fun testGetList_translationListIsEmpty_returnTrackName() {
        val mockTrackData = mockTrack.copy(translationsList = emptyList())

        whenever(trackMetadataApi.getMultiple(any())).thenReturn(Single.just(listOf(mockTrackData)))

        trackRepository.getList(listOf(1))
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track.id == mockTrackData.id &&
                    track.playlistTrackId == mockTrackData.playlistTrackId &&
                    track.name == mockTrackData.name
            }

        verify(trackMetadataApi, times(1)).getMultiple(any())
    }

    @Test
    fun testValidateTracks_returnData() {
        val mockIds = listOf(1, 2)
        whenever(trackMetadataApi.validateTracks(any())).thenReturn(Single.just(mockIds))

        trackRepository.validateTracks(mockIds)
            .test()
            .assertNoErrors()
            .assertValue { ids ->
                ids[0] == mockIds[0] &&
                    ids[1] == mockIds[1]
            }

        verify(trackMetadataApi, times(1)).validateTracks(any())
    }

    @Test

    fun testIsFavourite_returnIsFavourite() {
        val mockIsFavorite = true

        whenever(trackServiceApi.userContext(any())).thenReturn(
            Single.just(TrackContext(isFavourited = mockIsFavorite))
        )

        trackRepository.isFavourited(1)
            .test()
            .assertValue(mockIsFavorite)

        verify(trackServiceApi, times(1)).userContext(any())
    }

    @Test
    fun testRemoveFavourite_verifyUnFavourite() {
        whenever(trackServiceApi.unfavourite(any())).thenReturn(Single.just(Any()))

        trackRepository.removeFavourite(1)

        verify(trackServiceApi, times(1)).unfavourite(any())
    }

    @Test
    fun testAddFavourite_verifyAddFavourite() {
        whenever(trackServiceApi.favourite(any())).thenReturn(Single.just(Any()))

        trackRepository.addFavourite(1)

        verify(trackServiceApi, times(1)).favourite(any())
    }

    @Test
    fun testGetFavourite_resultsIsNotEmpty_returnTrackData() {
        val pageResultsMock = PagedResults<Track>(
            1,
            10,
            100,
            listOf(mockTrack)
        )
        whenever(trackServiceApi.favourited(any(), any(), any())).thenReturn(
            Single.just(pageResultsMock)
        )

        trackRepository.getFavourited(1, 10, TrackRequestType.AUDIO)
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track.id == mockTrack.id &&
                    track.playlistTrackId == mockTrack.playlistTrackId &&
                    track.nameTranslations == mockTrack.nameTranslations
            }

        verify(trackServiceApi, times(1)).favourited(any(), any(), any())
    }

    @Test
    fun testGetFavouriteSongsCount_returnSongsCount() {
        val pageResultsMock = PagedResults<Track>(
            1,
            10,
            100,
            listOf(mockTrack)
        )
        whenever(trackServiceApi.favourited(any(), any(), any())).thenReturn(
            Single.just(pageResultsMock)
        )

        trackRepository.getFavouritedSongsCount()
            .test()
            .assertNoErrors()
            .assertValue { songCount ->
                songCount == pageResultsMock.total
            }

        verify(trackServiceApi, times(1)).favourited(any(), any(), any())
    }

    @Test
    fun testGetFavouriteVideosCount_returnVideoCount() {
        val pageResultsMock = PagedResults<Track>(
            1,
            10,
            100,
            listOf(mockTrack)
        )
        whenever(trackServiceApi.favourited(any(), any(), any())).thenReturn(
            Single.just(pageResultsMock)
        )

        trackRepository.getFavouritedVideosCount()
            .test()
            .assertNoErrors()
            .assertValue { videoCount ->
                videoCount == pageResultsMock.total
            }

        verify(trackServiceApi, times(1)).favourited(any(), any(), any())
    }

    @Test
    fun testGetLyric_returnRawLyric() {
        val rawLyricStringMock = RawLyricString(raw = "raw")

        whenever(trackMetadataApi.getLyrics(any())).thenReturn(Single.just(rawLyricStringMock))

        trackRepository.getLyric(1)
            .test()
            .assertNoErrors()
            .assertValue { rawLyricString ->
                rawLyricString.raw == rawLyricStringMock.raw
            }

        verify(trackMetadataApi, times(1)).getLyrics(any())
    }
}
