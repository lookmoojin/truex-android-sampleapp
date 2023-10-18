package com.truedigital.features.music.presentation.search

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.track.usecase.GetTrackUseCase
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicTrackViewModelTest {

    private lateinit var viewModel: MusicTrackViewModel
    private val getTrackUseCase: GetTrackUseCase = mock()

    @BeforeEach
    fun setUp() {
        viewModel = MusicTrackViewModel(getTrackUseCase)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @AfterEach
    fun clean() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        validateMockitoUsage()
    }

    @Test
    fun testGetTrackUseCaseSuccess_returnPlayTrack() {
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

        whenever(getTrackUseCase.execute(any())).thenReturn(Single.just(mockResponse))

        viewModel.getTrack(1)

        assert(viewModel.onPlayTrack().value == mockResponse)
        assert(viewModel.onErrorPlayTrack().value == null)
    }

    @Test
    fun testGetTrackUseCaseFail_returnErrorPlayTrack() {
        whenever(getTrackUseCase.execute(any())).thenReturn(Single.error(Throwable("error")))

        viewModel.getTrack(1)

        assert(viewModel.onErrorPlayTrack().value == Unit)
        assert(viewModel.onPlayTrack().value == null)
    }
}
