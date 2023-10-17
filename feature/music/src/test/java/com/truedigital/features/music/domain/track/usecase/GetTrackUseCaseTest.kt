package com.truedigital.features.music.domain.track.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTrackUseCaseTest {

    private lateinit var getTrackUseCase: GetTrackUseCase
    private var trackRepository: TrackRepository = mock()

    @BeforeEach
    fun setup() {
        getTrackUseCase = GetTrackUseCaseImpl(trackRepository)
    }

    @Test
    fun testGetTrackSuccess_returnTrack() {
        val mockResponse = Track(
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
            isCached = false
        )

        whenever(trackRepository.get(any())).thenReturn(Single.just(mockResponse))

        val test = getTrackUseCase.execute(1).test()

        test.assertNoErrors()
        test.assertValue {
            it == mockResponse
        }
    }

    @Test
    fun testGetTrackFail_returnError() {
        whenever(trackRepository.get(any())).thenReturn(Single.error(Throwable("error")))

        val test = getTrackUseCase.execute(1).test()

        test.errors()
    }
}
