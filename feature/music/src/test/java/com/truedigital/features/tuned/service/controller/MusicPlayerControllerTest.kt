package com.truedigital.features.tuned.service.controller

import android.support.v4.media.session.PlaybackStateCompat
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.features.tuned.service.music.facade.MusicPlayerFacade
import com.truedigital.features.tuned.service.music.model.TrackQueue
import com.truedigital.features.tuned.service.util.PlayQueue
import com.truedigital.features.utils.MockDataModel
import com.truedigital.foundation.player.model.MediaAsset
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MusicPlayerControllerTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private lateinit var musicPlayerController: MusicPlayerController
    private val musicPlayerFacade: MusicPlayerFacade = mock()
    private val playerSurface: MusicPlayerController.PlayerSurface = mock()
    private val loadStreamSubscription: Disposable = mock()
    private val loadTracksSubscription: Disposable = mock()
    private val loadTrackExtrasSubscription: Disposable = mock()

    private val mockAd = Ad(
        title = "title",
        impressionUrl = "impressionUrl",
        duration = "duration",
        mediaFile = "mediaFile",
        image = "image",
        clickUrl = "clickUrl",
        vast = "vast"
    )
    private val mockAdMediaAsset = MusicPlayerController.AdMediaAsset(ad = mockAd)
    private val mockTrackMediaAsset = MusicPlayerController.TrackMediaAsset(
        track = MockDataModel.mockTrack
    )
    private val mockRadioMediaAsset = MusicPlayerController.RadioMediaAsset(
        radio = MockDataModel.mockRadioShelf
    )
    private val mockStakkarMediaAsset = MusicPlayerController.StakkarMediaAsset(
        stakkar = MockDataModel.mockStakkar
    )

    private fun mockCurrentMediaTrack() {
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))
        musicPlayerController.playVideo(MockDataModel.mockTrack)
    }

    @BeforeEach
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }

        musicPlayerController = MusicPlayerController(musicPlayerFacade)
        musicPlayerController.onInject(playerSurface)
    }

    @AfterEach
    fun reset() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun onStop_currentMediaNull_verifyPlayerStop() {
        // When
        musicPlayerController.onStop()

        // Then
        verify(playerSurface, times(1)).stop()
    }

    @Test
    fun onStop_subscriptionIsNotNull_disposeSubscription() {
        // Given
        musicPlayerController.setSubscription(
            loadStreamSubscription = loadStreamSubscription,
            loadTracksSubscription = loadTracksSubscription,
            loadTrackExtrasSubscription = loadTrackExtrasSubscription
        )

        // When
        musicPlayerController.onStop()

        // Then
        verify(loadStreamSubscription, times(1)).dispose()
        verify(loadTracksSubscription, times(1)).dispose()
        verify(loadTrackExtrasSubscription, times(1)).dispose()
    }

    @Test
    fun onStop_subscriptionIsNull_doNothing() {
        // Given
        musicPlayerController.setSubscription(
            loadStreamSubscription = null,
            loadTracksSubscription = null,
            loadTrackExtrasSubscription = null
        )

        // When
        musicPlayerController.onStop()

        // Then
        verify(loadStreamSubscription, times(0)).dispose()
        verify(loadTracksSubscription, times(0)).dispose()
        verify(loadTrackExtrasSubscription, times(0)).dispose()
    }

    @Test
    fun onPlayAudio_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onPlayAudio()

        // Then
        verify(playerSurface, times(0)).setStreamingDisabled()
        verify(playerSurface, times(0)).resume(any())
    }

    @Test
    fun onPlayAudio_currentMediaIsNull_locationIsNotNull_locationDoesNotStartWithFilePrefix_streamingEnableIsFalse_setStreamingEnable() {
        // Given
        val mockTrackMediaAssetValue = mockTrackMediaAsset.apply { location = "location" }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAssetValue)
        whenever(musicPlayerFacade.isStreamingEnabled()).thenReturn(false)

        // When
        musicPlayerController.onPlayAudio()

        // Then
        verify(playerSurface, times(1)).setStreamingDisabled()
    }

    @Test
    fun onPlayAudio_currentMediaIsNull_locationIsNotNull_locationDoesNotStartWithFilePrefix_streamingEnableIsTrue_resumePlayer() {
        // Given
        val mockTrackMediaAssetValue = mockTrackMediaAsset.apply { location = "location" }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAssetValue)
        whenever(musicPlayerFacade.isStreamingEnabled()).thenReturn(true)

        // When
        musicPlayerController.onPlayAudio()

        // Then
        verify(playerSurface, times(1)).resume(any())
    }

    @Test
    fun onPlayAudio_currentMediaIsNull_locationIsNotNull_locationStartWithFilePrefix_resumePlayer() {
        // Given
        val mockTrackMediaAssetValue = mockTrackMediaAsset.apply {
            location = "${MusicPlayerController.FILE_PREFIX}location"
        }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAssetValue)

        // When
        musicPlayerController.onPlayAudio()

        // Then
        verify(playerSurface, times(1)).resume(any())
    }

    @Test
    fun onPlayAudio_currentMediaIsNull_locationIsNull_resumePlayer() {
        // Given
        val mockTrackMediaAssetValue = mockTrackMediaAsset.apply { location = null }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAssetValue)

        // When
        musicPlayerController.onPlayAudio()

        // Then
        verify(playerSurface, times(1)).resume(any())
    }

    @Test
    fun onPauseAudio_currentMediaIsNull_pausePlayer() {
        // When
        musicPlayerController.onPauseAudio()

        // Then
        verify(playerSurface, times(1)).pause()
    }

    @Test
    fun onPauseAudio_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_pausePlayer() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onPauseAudio()

        // Then
        verify(playerSurface, times(1)).pause()
    }

    @Test
    fun onPauseAudio_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onPauseAudio()

        // Then
        verify(playerSurface, times(0)).pause()
    }

    @Test
    fun onStopAudio_currentMediaIsNull_stopAudio() {
        // When
        musicPlayerController.onStopAudio()

        // Then
        verify(playerSurface, times(1)).stop()
    }

    @Test
    fun onStopAudio_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_stopAudio() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onStopAudio()

        // Then
        verify(playerSurface, times(1)).stop()
    }

    @Test
    fun onStopAudio_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onStopAudio()

        // Then
        verify(playerSurface, times(0)).stop()
    }

    @Test
    fun onSkipToPrevious_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onSkipToPrevious()

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onSkipToPrevious_currentMediaIsNull_lastKnowPositionMoreThanSkipToPrevious_hasPreviousIsFalse_seekToIsCalled() {
        // Given
        val mockLastKnownPosition = MusicPlayerController.SKIP_TO_PREVIOUS_THRESHOLD + 1
        val mockPlayQueue = TrackQueue(listOf())
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            lastKnownPosition = mockLastKnownPosition,
            playQueue = mockPlayQueue
        )

        // When
        musicPlayerController.onSkipToPrevious()

        // Then
        verify(playerSurface, times(1)).seekTo(any(), any())
    }

    @Test
    fun onSkipToPrevious_currentMediaIsNull_lastKnowPositionMoreThanSkipToPrevious_hasPreviousIsTrue_seekToIsCalled() {
        // Given
        val mockLastKnownPosition = MusicPlayerController.SKIP_TO_PREVIOUS_THRESHOLD + 1
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            lastKnownPosition = mockLastKnownPosition,
            playQueue = mockPlayQueue
        )

        // When
        musicPlayerController.onSkipToPrevious()

        // Then
        verify(playerSurface, times(1)).seekTo(any(), any())
    }

    @Test
    fun onSkipToPrevious_currentMediaIsNull_lastKnowPositionLessThanSkipToPrevious_hasPreviousIsTrue_seekToIsCalled() {
        // Given
        val mockLastKnownPosition = MusicPlayerController.SKIP_TO_PREVIOUS_THRESHOLD - 1
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack))
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            lastKnownPosition = mockLastKnownPosition,
            playQueue = mockPlayQueue
        )

        // When
        musicPlayerController.onSkipToPrevious()

        // Then
        verify(playerSurface, times(1)).seekTo(any(), any())
    }

    @Test
    fun onSkipToPrevious_caseElse_repeatModeIsRepeatModeOne_setRepeatMode() {
        // Given
        val mockLastKnownPosition = MusicPlayerController.SKIP_TO_PREVIOUS_THRESHOLD - 1
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            lastKnownPosition = mockLastKnownPosition,
            playQueue = mockPlayQueue
        )
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToPrevious()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(musicPlayerFacade, times(1)).isShufflePlayEnabled()
        verify(playerSurface, times(1)).setPlayMode(any(), any())
    }

    @Test
    fun onSkipToNext_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(playerSurface, times(0)).stop()
        verify(playerSurface, times(0)).resetMetaData()
    }

    @Test
    fun onSkipToNext_currentMediaIsNull_blockSkipPressIsTrue_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null, blockSkipPress = true)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(playerSurface, times(0)).stop()
        verify(playerSurface, times(0)).resetMetaData()
    }

    @Test
    fun onSkipToNext_currentSourceIsNotNull_sourceStationIsNull_repeatModeIsRepeatModeOne_repeatModeIsSet() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = null }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = null,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(playerSurface, times(1)).setPlayMode(any(), any())
    }

    @Test
    fun onSkipToNext_currentSourceIsNotNull_sourceStationIsNull_repeatModeIsRepeatModeAll_repeatModeIsNotSet() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = null }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = null,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ALL)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(0)).setRepeatMode(any())
        verify(playerSurface, times(0)).setPlayMode(any(), any())
    }

    @Test
    fun onSkipToNext_currentSourceIsNotNull_sourceStationIsNotNull_repeatModeIsRepeatModeAll_repeatModeIsNotSet() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = null,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ALL)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(0)).setRepeatMode(any())
        verify(playerSurface, times(0)).setPlayMode(any(), any())
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsTrue_hijackedMediaIsNotNull_availableSkipsMoreThanZero_MediaIsNotNull_addLocalSkipAndLogTrackSkip() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        musicPlayerController.setPrivateDataSecondSection(
            hijackedMedia = mockTrackMediaAsset,
            availableSkips = 1
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.addLocalSkip()).thenReturn(Single.just(1L))
        whenever(musicPlayerFacade.logTrackSkip(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(Any())
        )

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(1)).addLocalSkip()
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsTrue_hijackedMediaIsNotNull_availableSkipsLessThanZero_MediaIsNull_addLocalSkipAndLogTrackSkip() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        musicPlayerController.setPrivateDataSecondSection(
            hijackedMedia = mockTrackMediaAsset,
            availableSkips = -1
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.addLocalSkip()).thenReturn(Single.just(1L))

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(1)).addLocalSkip()
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsTrue_hijackedMediaIsNull_availableSkipMoreThanZero_MediaIsNull_addLocalSkipAndLogTrackSkip() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        musicPlayerController.setPrivateDataSecondSection(
            hijackedMedia = null,
            availableSkips = 1
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.addLocalSkip()).thenReturn(Single.just(1L))

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(1)).addLocalSkip()
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsTrue_hijackedMediaIsNull_availableSkipsLessThanZero_setSkipLimitReached() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        musicPlayerController.setPrivateDataSecondSection(
            hijackedMedia = null,
            availableSkips = -1
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(playerSurface, times(1)).setSkipLimitReached()
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsFalse_mediaIsNotNull_mediaIsTrackMediaAsset_logTrackSkipIsCalled() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.logTrackSkip(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(Any())
        )

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(1)).logTrackSkip(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onSkipToNext_skipLimitEnabledIsFalse_mediaIsNotNull_mediaIsNotTrackMediaAsset_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply {
            this.sourceStation = MockDataModel.mockStation
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockStakkarMediaAsset,
            blockSkipPress = false,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(false)

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(0)).logTrackSkip(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onSkipToNext_currentSourceIsNull_stopPlayerAndResetMetaData() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockStakkarMediaAsset,
            blockSkipPress = false,
            currentSource = null
        )

        // When
        musicPlayerController.onSkipToNext()

        // Then
        verify(playerSurface, times(1)).stop()
        verify(playerSurface, times(1)).resetMetaData()
    }

    @Test
    fun onLike_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(0)).likeTrack(any(), any())
    }

    @Test
    fun onLike_currentMediaIsNotNull_currentMediaIsNotTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(0)).likeTrack(any(), any())
    }

    @Test
    fun onLike_trackIsNotNull_currentSourceIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = null
        )

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(0)).likeTrack(any(), any())
    }

    @Test
    fun onLike_trackIsNotNull_currentSourceIsNotNull_sourceStationIsNull_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = null }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(0)).likeTrack(any(), any())
    }

    @Test
    fun onLike_trackIsNotNull_currentSourceIsNotNull_sourceStationIsNotNull_offlineIsTrue_doNothing() {
        // Given
        val mockStationValue = MockDataModel.mockStation.apply { this.isOffline = true }
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = mockStationValue }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(0)).likeTrack(any(), any())
    }

    @Test
    fun onLike_trackIsNotNull_currentSourceIsNotNull_sourceStationIsNotNull_offlineIsFalse_likeTrackIsCalled() {
        // Given
        val mockStationValue = MockDataModel.mockStation.apply { this.isOffline = false }
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = mockStationValue }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.likeTrack(any(), any())).thenReturn(
            Single.just(MockDataModel.mockVote)
        )

        // When
        musicPlayerController.onLike()

        // Then
        verify(musicPlayerFacade, times(1)).likeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNull_doNothing() {
        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(0)).dislikeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNotNull_currentMediaIsNotTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(0)).dislikeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNotNull_currentSourceIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = null
        )

        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(0)).dislikeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNotNull_currentSourceIsNotNull_sourceStationIsNull_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = null }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )

        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(0)).dislikeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNotNull_currentSourceIsNotNull_sourceStationIsNotNull_offlineIsTrue_doNothing() {
        // Given
        val mockStationValue = MockDataModel.mockStation.apply { this.isOffline = true }
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = mockStationValue }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )

        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(0)).dislikeTrack(any(), any())
    }

    @Test
    fun onDislike_currentMediaIsNotNull_currentSourceIsNotNull_sourceStationIsNotNull_offlineIsFalse_dislikeTrackIsCalled() {
        // Given
        val mockStationValue = MockDataModel.mockStation.apply { this.isOffline = false }
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = mockStationValue }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )
        musicPlayerController.setPrivateDataSecondSection(availableSkips = -1)
        whenever(musicPlayerFacade.dislikeTrack(any(), any())).thenReturn(
            Single.just(MockDataModel.mockVote)
        )
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)

        // When
        musicPlayerController.onDislike()

        // Then
        verify(musicPlayerFacade, times(1)).dislikeTrack(any(), any())
    }

    @Test
    fun onRemoveRating_currentMediaIsNull_doNothing() {
        // When
        musicPlayerController.onRemoveRating()

        // Then
        verify(musicPlayerFacade, times(0)).removeRating(any(), any())
    }

    @Test
    fun onRemoveRating_currentMediaIsNotNull_currentMediaIsNotTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onRemoveRating()

        // Then
        verify(musicPlayerFacade, times(0)).removeRating(any(), any())
    }

    @Test
    fun onRemoveRating_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_currentSourceIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = null
        )

        // When
        musicPlayerController.onRemoveRating()

        // Then
        verify(musicPlayerFacade, times(0)).removeRating(any(), any())
    }

    @Test
    fun onRemoveRating_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_onlineStationIsTrue_removeRatingIsCalled() {
        // Given
        val mockStationValue = MockDataModel.mockStation.apply { this.isOffline = false }
        val mockTrackValue = MockDataModel.mockTrack.apply { this.sourceStation = mockStationValue }
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = mockTrackValue
        )
        whenever(musicPlayerFacade.removeRating(any(), any())).thenReturn(
            Single.just(MockDataModel.mockVote)
        )

        // When
        musicPlayerController.onRemoveRating()

        // Then
        verify(musicPlayerFacade, times(1)).removeRating(any(), any())
    }

    @Test
    fun onToggleShuffle_enabledDefault_verifySetPlayMode() {
        // Given
        val mockIsShufflePlayEnabled = true
        val mockRepeatMode = 0
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(mockRepeatMode)

        // When
        musicPlayerController.onToggleShuffle()

        // Then
        verify(musicPlayerFacade, times(1)).setShufflePlay(false)
        verify(playerSurface, times(1)).setPlayMode(mockIsShufflePlayEnabled, mockRepeatMode)
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleShuffle_enabledNull_verifySetPlayMode() {
        // Given
        val mockIsShufflePlayEnabled = false
        val mockRepeatMode = 0
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(mockRepeatMode)

        // When
        musicPlayerController.onToggleShuffle(null)

        // Then
        verify(musicPlayerFacade, times(1)).setShufflePlay(true)
        verify(playerSurface, times(1)).setPlayMode(mockIsShufflePlayEnabled, mockRepeatMode)
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleShuffle_enabledTrue_verifySetPlayModeTrue() {
        // Given
        val mockIsShufflePlayEnabled = true
        val mockRepeatMode = 0
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(mockRepeatMode)

        // When
        musicPlayerController.onToggleShuffle(true)

        // Then
        verify(musicPlayerFacade, times(1)).setShufflePlay(true)
        verify(playerSurface, times(1)).setPlayMode(mockIsShufflePlayEnabled, mockRepeatMode)
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleShuffle_enabledFalse_verifySetPlayModeFalse() {
        // Given
        val mockIsShufflePlayEnabled = false
        val mockRepeatMode = 0
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(mockRepeatMode)

        // When
        musicPlayerController.onToggleShuffle(false)

        // Then
        verify(musicPlayerFacade, times(1)).setShufflePlay(false)
        verify(playerSurface, times(1)).setPlayMode(mockIsShufflePlayEnabled, mockRepeatMode)
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleRepeat_repeatModeNone_verifySetRepeatModeAll() {
        // Given
        val mockIsShufflePlayEnabled = false
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_NONE)

        // When
        musicPlayerController.onToggleRepeat()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(PlayQueue.REPEAT_MODE_ALL)
        verify(playerSurface, times(1)).setPlayMode(
            mockIsShufflePlayEnabled,
            PlayQueue.REPEAT_MODE_ALL
        )
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleRepeat_repeatModeAll_verifySetRepeatModeOne() {
        // Given
        val mockIsShufflePlayEnabled = false
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ALL)

        // When
        musicPlayerController.onToggleRepeat()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(PlayQueue.REPEAT_MODE_ONE)
        verify(playerSurface, times(1)).setPlayMode(
            mockIsShufflePlayEnabled,
            PlayQueue.REPEAT_MODE_ONE
        )
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleRepeat_repeatModeOne_verifySetRepeatModeNone() {
        // Given
        val mockIsShufflePlayEnabled = false
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)

        // When
        musicPlayerController.onToggleRepeat()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(PlayQueue.REPEAT_MODE_NONE)
        verify(playerSurface, times(1)).setPlayMode(
            mockIsShufflePlayEnabled,
            PlayQueue.REPEAT_MODE_NONE
        )
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onToggleRepeat_repeatModeOther_verifySetRepeatModeNone() {
        // Given
        val mockIsShufflePlayEnabled = false
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(mockIsShufflePlayEnabled)
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(-1)

        // When
        musicPlayerController.onToggleRepeat()

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(PlayQueue.REPEAT_MODE_NONE)
        verify(playerSurface, times(1)).setPlayMode(
            mockIsShufflePlayEnabled,
            PlayQueue.REPEAT_MODE_NONE
        )
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onSeekTo_currentMediaNull_positionMinusValue_notVerifyPlayerSeekTo() {
        // When
        musicPlayerController.onSeekTo(-1L)

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onSeekTo_currentMediaNull_positionNotMinusValue_notVerifyPlayerSeekTo() {
        // When
        musicPlayerController.onSeekTo(10L)

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onSeekTo_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_seekToIsCalled() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onSeekTo(10L)

        // Then
        verify(playerSurface, times(1)).seekTo(any(), anyOrNull())
    }

    @Test
    fun onSeekTo_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockRadioMediaAsset)

        // When
        musicPlayerController.onSeekTo(10L)

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onSeekTo_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onSeekTo(10L)

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onReplay_currentMediaNull_notVerifyPlayerSeekTo() {
        // When
        musicPlayerController.onReplay()

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onReplay_currentMediaNotNull_verifyPlayerSeekTo() {
        // Given
        mockCurrentMediaTrack()

        // When
        musicPlayerController.onReplay()

        // Then
        verify(playerSurface, times(1)).seekTo(0, false)
    }

    @Test
    fun onReplay_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_seekToIsCalled() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onReplay()

        // Then
        verify(playerSurface, times(1)).seekTo(any(), any())
    }

    @Test
    fun onReplay_currentMediaIsNotNull_currentMediaIsAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAsset)

        // When
        musicPlayerController.onReplay()

        // Then
        verify(playerSurface, times(0)).seekTo(any(), any())
    }

    @Test
    fun onPlaybackError_disposeStreamSubscription() {
        // Given
        musicPlayerController.setSubscription(loadStreamSubscription = loadStreamSubscription)
        musicPlayerController.setPrivateDataFirstSection(currentSource = null)

        // When
        musicPlayerController.onPlaybackError(error = Exception())

        // Then
        verify(loadStreamSubscription, times(1)).dispose()
    }

    @Test
    fun onPlaybackTick_currentMediaNull_playWhenReadyTrue_verifyPlayerSetPlaybackPosition() {
        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(playerSurface, times(1)).setPlaybackPosition(10L)
    }

    @Test
    fun onPlaybackTick_currentMediaNull_playWhenReadyFalse_verifyPlayerPause() {
        // Given
        musicPlayerController.onRemoveTrack(10)

        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(playerSurface, times(1)).setPlaybackPosition(10L)
        verify(playerSurface, times(1)).pause()
    }

    @Test
    fun onPlaybackEnded_currentSourceIsNull_resetMetaData() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentSource = null)

        // When
        musicPlayerController.onPlaybackEnded()

        // Then
        verify(playerSurface, times(1)).resetMetaData()
    }

    @Test
    fun onPlaybackEnded_currentSourceIsNotNull_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = MockDataModel.mockTrack,
            currentMedia = null
        )
        musicPlayerController.setPrivateDataSecondSection(hijackedMedia = mockStakkarMediaAsset)

        // When
        musicPlayerController.onPlaybackEnded()

        // Then
        verify(playerSurface, times(0)).resetMetaData()
        verify(musicPlayerFacade, times(0)).logTrackFinish(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackEnded_currentSourceIsNotNull_currentMediaIsNotNull_currentMediaIsNotTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockAdMediaAsset
        )
        musicPlayerController.setPrivateDataSecondSection(hijackedMedia = mockStakkarMediaAsset)

        // When
        musicPlayerController.onPlaybackEnded()

        // Then
        verify(playerSurface, times(0)).resetMetaData()
        verify(musicPlayerFacade, times(0)).logTrackFinish(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackEnded_currentSourceIsNotNull_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_logTrackFinishIsCalled() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockTrackMediaAsset
        )
        musicPlayerController.setPrivateDataSecondSection(hijackedMedia = mockStakkarMediaAsset)
        whenever(musicPlayerFacade.logTrackFinish(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(Any())
        )

        // When
        musicPlayerController.onPlaybackEnded()

        // Then
        verify(musicPlayerFacade, times(1)).logTrackFinish(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackTick_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_currentSourceIsNotNull_logTrackPlayIsCalled() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = MockDataModel.mockTrack
        )
        whenever(musicPlayerFacade.logTrackPlay(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(true)
        )

        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(musicPlayerFacade, times(1)).logTrackPlay(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackTick_currentMediaIsNotNull_currentMediaIsTrackMediaAsset_currentSourceIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockTrackMediaAsset,
            currentSource = null
        )

        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(musicPlayerFacade, times(0)).logTrackPlay(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackTick_currentMediaIsNotNull_currentMediaIsNotTrackMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = mockAdMediaAsset,
            currentSource = null
        )

        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(musicPlayerFacade, times(0)).logTrackPlay(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackTick_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentMedia = null,
            currentSource = null
        )

        // When
        musicPlayerController.onPlaybackTick(10L)

        // Then
        verify(musicPlayerFacade, times(0)).logTrackPlay(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onPlaybackEnded_currentSourceNotNull_currentMediaNull_notVerifyLogTrackFinish() {
        // Given
        musicPlayerController.playFullStream(
            MockDataModel.mockPlaylist,
            listOf(MockDataModel.mockTrack),
            0,
            forceShuffle = false,
            forceSequential = false
        )

        // When
        musicPlayerController.onPlaybackEnded()

        // Then
        verify(musicPlayerFacade, times(0)).logTrackFinish(any(), any(), any(), any())
    }

    @Test
    fun onLostAudioFocus_canDuckDefault_currentMediaNull_verifyPlayerPause() {
        // When
        musicPlayerController.onLostAudioFocus()

        // Then
        verify(playerSurface, times(1)).pause()
    }

    @Test
    fun onLostAudioFocus_canDuckTrue_verifyPlayerSetVolume() {
        // When
        musicPlayerController.onLostAudioFocus(true)

        // Then
        verify(playerSurface, times(1)).setVolume(any())
    }

    @Test
    fun onLostAudioFocus_canDuckTrue_playingIsTrue_verifyPlayerSetVolume() {
        // Given
        musicPlayerController.setPrivateDataSecondSection(playing = true)

        // When
        musicPlayerController.onLostAudioFocus(true)

        // Then
        verify(playerSurface, times(1)).setVolume(any())
    }

    @Test
    fun onLostAudioFocus_canDuckTrue_playingIsFalse_verifyPlayerSetVolume() {
        // Given
        musicPlayerController.setPrivateDataSecondSection(playing = false)

        // When
        musicPlayerController.onLostAudioFocus(true)

        // Then
        verify(playerSurface, times(1)).setVolume(any())
    }

    @Test
    fun onGainedAudioFocus_currentMediaIsNotNull_lostFocusWhilstPlayingIsTrue_resumePlayer() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)
        musicPlayerController.setPrivateDataSecondSection(lostFocusWhilstPlaying = true)

        // When
        musicPlayerController.onGainedAudioFocus()

        // Then
        verify(playerSurface, times(1)).resume(any())
    }

    @Test
    fun onGainedAudioFocus_currentMediaIsNotNull_lostFocusWhilstPlayingIsFalse_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)
        musicPlayerController.setPrivateDataSecondSection(lostFocusWhilstPlaying = false)

        // When
        musicPlayerController.onGainedAudioFocus()

        // Then
        verify(playerSurface, times(0)).resume(any())
    }

    @Test
    fun onGainedAudioFocus_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)
        musicPlayerController.setPrivateDataSecondSection(lostFocusWhilstPlaying = false)

        // When
        musicPlayerController.onGainedAudioFocus()

        // Then
        verify(playerSurface, times(0)).resume(any())
    }

    @Test
    fun onGainedAudioFocus_duckingIsTrue_setVolume() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)
        musicPlayerController.setPrivateDataSecondSection(
            lostFocusWhilstPlaying = false,
            isDucking = true
        )

        // When
        musicPlayerController.onGainedAudioFocus()

        // Then
        verify(playerSurface, times(1)).setVolume(any())
    }

    @Test
    fun onGainedAudioFocus_duckingIsFalse_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)
        musicPlayerController.setPrivateDataSecondSection(
            lostFocusWhilstPlaying = false,
            isDucking = false
        )

        // When
        musicPlayerController.onGainedAudioFocus()

        // Then
        verify(playerSurface, times(0)).setVolume(any())
    }

    @Test
    fun onBecomingNoisy_playingTrue_pausePlayer() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)
        musicPlayerController.setPrivateDataSecondSection(playing = true)

        // When
        musicPlayerController.onBecomingNoisy()

        // Then
        verify(playerSurface, times(1)).pause()
    }

    @Test
    fun onBecomingNoisy_playingFalse_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)
        musicPlayerController.setPrivateDataSecondSection(playing = false)

        // When
        musicPlayerController.onBecomingNoisy()

        // Then
        verify(playerSurface, times(0)).pause()
    }

    @Test
    fun onSourceLoaded_enqueuedAssetIsNull_currentSourceIsNotNull_currentTrackIsTrackMediaAsset_streamTrackIsCalled() {
        // Given
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(
            playQueue = mockPlayQueue,
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockTrackMediaAsset
        )
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(mockTrackMediaAsset))

        // When
        musicPlayerController.onSourceLoaded()

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun onSourceLoaded_enqueuedAssetIsNull_currentSourceIsNotNull_currentTrackIsNotTrackMediaAsset_doNothing() {
        // Given
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(
            playQueue = mockPlayQueue,
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockAdMediaAsset
        )

        // When
        musicPlayerController.onSourceLoaded()

        // Then
        verify(musicPlayerFacade, times(0)).streamTrack(any())
    }

    @Test
    fun onSourceLoaded_enqueuedAssetIsNull_currentSourceIsNull_doNothing() {
        // Given
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(
            playQueue = mockPlayQueue,
            currentSource = null
        )

        // When
        musicPlayerController.onSourceLoaded()

        // Then
        verify(musicPlayerFacade, times(0)).streamTrack(any())
    }

    @Test
    fun onSourceLoaded_enqueuedAssetIsNotNull_shouldDoPreBufferImmediatelyIsSet() {
        // Given
        musicPlayerController.setPrivateDataThirdSection(enqueuedAsset = mockTrackMediaAsset)

        // When
        musicPlayerController.onSourceLoaded()

        // Then
        verify(musicPlayerFacade, times(0)).streamTrack(any())
    }

    @Test
    fun onPlayStakkarById_currentStakkarNull_loadStakkarSuccess_verifyStreamStakkar() {
        // Given
        whenever(musicPlayerFacade.loadStakkar(any())).thenReturn(Single.just(MockDataModel.mockStakkar))
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(Single.just(MediaAsset(id = 1)))

        // When
        musicPlayerController.onPlayStakkar(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadStakkar(1)
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(1F)
    }

    @Test
    fun onPlayStakkarById_currentStakkarNull_isDuckingTrue_notVerifyPlayerRequestAudioFocus() {
        // Given
        whenever(musicPlayerFacade.loadStakkar(any())).thenReturn(Single.just(MockDataModel.mockStakkar))
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(Single.just(MediaAsset(id = 1)))
        musicPlayerController.onLostAudioFocus(true)

        // When
        musicPlayerController.onPlayStakkar(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadStakkar(1)
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
        verify(playerSurface, times(0)).requestAudioFocus()
        verify(playerSurface, times(0)).setVolume(1F)
    }

    @Test
    fun onPlayStakkarById_currentStakkarNull_locationNotNull_verifyIsStreamingEnabled() {
        // Given
        whenever(musicPlayerFacade.loadStakkar(any())).thenReturn(
            Single.just(MockDataModel.mockStakkar.apply { type = Stakkar.MediaType.VIDEO })
        )
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(
            Single.just(MediaAsset(id = 1, location = "abc"))
        )
        whenever(musicPlayerFacade.isStreamingEnabled()).thenReturn(false)

        // When
        musicPlayerController.onPlayStakkar(1)

        // Then
        verify(musicPlayerFacade, times(1)).isStreamingEnabled()
        verify(musicPlayerFacade, times(1)).loadStakkar(1)
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(1F)
    }

    @Test
    fun onPlayStakkarById_currentStakkarNull_locationStartPrefix_verifyStreamStakkar() {
        // Given
        whenever(musicPlayerFacade.loadStakkar(any())).thenReturn(
            Single.just(MockDataModel.mockStakkar.apply { type = Stakkar.MediaType.VIDEO })
        )
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(
            Single.just(MediaAsset(id = 1, location = "file://abc"))
        )

        // When
        musicPlayerController.onPlayStakkar(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadStakkar(1)
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(1F)
    }

    @Test
    fun onPlayStakkarById_currentStakkarNull_loadStreamStakkarFailed_verifyStreamStakkar() {
        // Given
        whenever(musicPlayerFacade.loadStakkar(any())).thenReturn(Single.just(MockDataModel.mockStakkar))
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(Single.error(Throwable("error")))

        // When
        musicPlayerController.onPlayStakkar(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadStakkar(1)
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(1F)
    }

    @Test
    fun onPlayStakkarById_currentStakkarIdIsEqualsStakkarId_streamStakkar() {
        // Given
        val mockId = 10
        val mockStakkarValue = MockDataModel.mockStakkar.copy(id = mockId)
        musicPlayerController.setPrivateDataThirdSection(currentStakkar = mockStakkarValue)
        whenever(musicPlayerFacade.streamStakkar(any())).thenReturn(Single.just(mockTrackMediaAsset))

        // When
        musicPlayerController.onPlayStakkar(mockId)

        // Then
        verify(musicPlayerFacade, times(1)).streamStakkar(any())
    }

    @Test
    fun onPlayRadio_duckingIsTrue_playRadioPlayer() {
        // Given
        musicPlayerController.setPrivateDataSecondSection(isDucking = true)

        // When
        musicPlayerController.onPlayRadio(MockDataModel.mockRadioShelf)

        // Then
        verify(playerSurface, times(1)).play(any())
    }

    @Test
    fun onPlayRadio_duckingIsFalse_playRadioPlayerAndRequestAudioFocus() {
        // Given
        musicPlayerController.setPrivateDataSecondSection(isDucking = false)

        // When
        musicPlayerController.onPlayRadio(MockDataModel.mockRadioShelf)

        // Then
        verify(playerSurface, times(1)).play(any())
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(any())
    }

    @Test
    fun onPlayAd_currentMediaIsNotNull_currentMediaIsAdMediaAsset_adIdEqualsCurrentMediaId_urlIsNotNull_playPlayer() {
        // Given
        val mockId = 1
        val mockAdMediaAssetValue = mockAdMediaAsset.apply { this.id = mockId }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAssetValue)

        // When
        musicPlayerController.onPlayAd(mockId, "url")

        // Then
        verify(playerSurface, times(1)).play(any())
    }

    @Test
    fun onPlayAd_currentMediaIsNotNull_currentMediaIsAdMediaAsset_adIdEqualsCurrentMediaId_urlIsNull_doNothing() {
        // Given
        val mockId = 1
        val mockAdMediaAssetValue = mockAdMediaAsset.apply { this.id = mockId }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAssetValue)

        // When
        musicPlayerController.onPlayAd(mockId, null)

        // Then
        verify(playerSurface, times(0)).play(any())
    }

    @Test
    fun onPlayAd_currentMediaIsNotNull_currentMediaIsAdMediaAsset_adIdNotEqualsCurrentMediaId_doNothing() {
        // Given
        val mockId = 1
        val mockAdMediaAssetValue = mockAdMediaAsset.apply { this.id = mockId }
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAssetValue)

        // When
        musicPlayerController.onPlayAd(2, null)

        // Then
        verify(playerSurface, times(0)).play(any())
    }

    @Test
    fun onPlayAd_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onPlayAd(2, null)

        // Then
        verify(playerSurface, times(0)).play(any())
    }

    @Test
    fun onPlayAd_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)

        // When
        musicPlayerController.onPlayAd(2, null)

        // Then
        verify(playerSurface, times(0)).play(any())
    }

    @Test
    fun onDismissAd_currentMediaIsNotNull_currentMediaIsAdMediaAsset_adIdEqualsCurrentMediaId_playNext() {
        // Given
        val mockId = 1
        val mockAdMediaAssetValue = mockAdMediaAsset.apply { this.id = mockId }
        musicPlayerController.setSubscription(loadStreamSubscription = loadStreamSubscription)
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAssetValue)

        // When
        musicPlayerController.onDismissAd(mockId)

        // Then
        verify(loadStreamSubscription, times(1)).dispose()
    }

    @Test
    fun onDismissAd_currentMediaIsNotNull_currentMediaIsAdMediaAsset_adIdNotEqualsCurrentMediaId_doNothing() {
        // Given
        val mockAdMediaAssetValue = mockAdMediaAsset.apply { this.id = 1 }
        musicPlayerController.setSubscription(loadStreamSubscription = loadStreamSubscription)
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockAdMediaAssetValue)

        // When
        musicPlayerController.onDismissAd(2)

        // Then
        verify(loadStreamSubscription, times(0)).dispose()
    }

    @Test
    fun onDismissAd_currentMediaIsNotNull_currentMediaIsNotAdMediaAsset_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = mockTrackMediaAsset)

        // When
        musicPlayerController.onDismissAd(1)

        // Then
        verify(loadStreamSubscription, times(0)).dispose()
    }

    @Test
    fun onDismissAd_currentMediaIsNull_doNothing() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(currentMedia = null)

        // When
        musicPlayerController.onDismissAd(1)

        // Then
        verify(loadStreamSubscription, times(0)).dispose()
    }

    @Test
    fun playRadio_hasTrackPlayRightFalse_verifyPlayerSetUpgradeRequired() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(false)

        // When
        musicPlayerController.playRadio(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).setUpgradeRequired()
    }

    @Test
    fun playRadio_currentSourceIsTrue_resumePlayer() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockTrackMediaAsset
        )
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)

        // When
        musicPlayerController.playRadio(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).resume(any())
    }

    @Test
    fun playRadio_currentSourceIsFalse_pauseAudioAndSetAvailableActions() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = MockDataModel.mockTrack,
            currentMedia = mockTrackMediaAsset
        )
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalSkips()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(10))

        // When
        musicPlayerController.playRadio(MockDataModel.mockTrack.copy(id = 20))

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(1)).getTotalSkips()
        verify(musicPlayerFacade, times(1)).isPlayLimitEnabled()
        verify(musicPlayerFacade, times(1)).getTotalPlays()
    }

    @Test
    fun playRadio_trackIsNull_stationsIsNull_doNotCallEnqueueStation() {
        // Given
        val mockCurrentSource = MockDataModel.mockTrack.apply {
            this.sourceStation = null
        }
        musicPlayerController.setPrivateDataFirstSection(
            currentSource = mockCurrentSource,
            currentMedia = mockTrackMediaAsset
        )

        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(10))
        whenever(musicPlayerFacade.enqueueStation(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // When
        musicPlayerController.playRadio(
            source = MockDataModel.mockTrack.copy(id = 20),
            tracks = null
        )

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(musicPlayerFacade, times(1)).isSkipLimitEnabled()
        verify(musicPlayerFacade, times(0)).getTotalSkips()
        verify(musicPlayerFacade, times(1)).isPlayLimitEnabled()
        verify(musicPlayerFacade, times(0)).getTotalPlays()
        verify(musicPlayerFacade, times(0)).enqueueStation(any(), any())
    }

    @Test
    fun playFullStream_hasTrackPlayRightFalse_verifyPlayerSetUpgradeRequired() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(false)

        // When
        musicPlayerController.playFullStream(MockDataModel.mockTrack, listOf(), 1, true, true)

        // Then
        verify(playerSurface, times(1)).setUpgradeRequired()
    }

    @Test
    fun playFullStream_repeatModeOne_hasSequentialPlaybackRightTrue_forceSequentialTrue_verifyShufflePlayFalse() {
        // Given
        musicPlayerController.setSubscription(loadTracksObservable = Single.just(listOf()))
        musicPlayerController.setPrivateDataFirstSection(blockSkipPress = true)

        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalSkips()).thenReturn(Single.just(10))
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(0))
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.hasSequentialPlaybackRight()).thenReturn(true)

        // When
        musicPlayerController.playFullStream(MockDataModel.mockTrack, listOf(), 1, true, true)

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(playerSurface, times(2)).setQueueUpdated()
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(musicPlayerFacade, times(1)).setShufflePlay(any())
    }

    @Test
    fun playFullStream_repeatModeAll_forceShuffleTrue_verifyShufflePlayTrue() {
        // Given
        musicPlayerController.setSubscription(loadTracksObservable = Single.just(listOf()))
        musicPlayerController.setPrivateDataFirstSection(blockSkipPress = true)

        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalSkips()).thenReturn(Single.just(10))
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(0))
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ALL)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.hasSequentialPlaybackRight()).thenReturn(true)

        // When
        musicPlayerController.playFullStream(MockDataModel.mockTrack, listOf(), 1, true, false)

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(playerSurface, times(2)).setQueueUpdated()
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(musicPlayerFacade, times(1)).setShufflePlay(any())
    }

    @Test
    fun playFullStream_forceShuffleFalse_isShufflePlayEnabledTrue_verifyShufflePlayTrue() {
        // Given
        musicPlayerController.setSubscription(loadTracksObservable = Single.just(listOf()))
        musicPlayerController.setPrivateDataFirstSection(blockSkipPress = true)

        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalSkips()).thenReturn(Single.just(10))
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(0))
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.hasSequentialPlaybackRight()).thenReturn(false)

        // When
        musicPlayerController.playFullStream(MockDataModel.mockTrack, listOf(), 1, false, false)

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(playerSurface, times(2)).setQueueUpdated()
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(musicPlayerFacade, times(1)).setShufflePlay(any())
    }

    @Test
    fun playFullStream_forceShuffleFalse_isShufflePlayEnabledFalse_verifyShufflePlayFalse() {
        // Given
        musicPlayerController.setSubscription(loadTracksObservable = Single.just(listOf()))
        musicPlayerController.setPrivateDataFirstSection(blockSkipPress = true)

        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isSkipLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalSkips()).thenReturn(Single.just(10))
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(0))
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.hasSequentialPlaybackRight()).thenReturn(false)

        // When
        musicPlayerController.playFullStream(MockDataModel.mockTrack, listOf(), 1, false, false)

        // Then
        verify(playerSurface, times(1)).pause()
        verify(playerSurface, times(1)).setQueueTitle(any(), any())
        verify(playerSurface, times(1)).setAvailableActions(any())
        verify(playerSurface, times(2)).setQueueUpdated()
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(playerSurface, times(2)).setPlayMode(any(), any())
        verify(musicPlayerFacade, times(1)).setShufflePlay(any())
    }

    @Test
    fun playVideoByTrack_hasTrackPlayRightFalse_verifyPlayerSetUpgradeRequired() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(false)

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).setUpgradeRequired()
    }

    @Test
    fun playVideoByTrack_isPlayLimitEnabledTrue_getTotalPlaysLessZero_verifyPlayerSetPlayLimitReached() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(-1))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).setPlayLimitReached()
    }

    @Test
    fun playVideoByTrack_isPlayLimitEnabledTrue_getTotalPlaysZero_verifyPlayerSetPlayLimitReached() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(0))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).setPlayLimitReached()
    }

    @Test
    fun playVideoByTrack_isPlayLimitEnabledTrue_getTotalPlaysMoreZero_notVerifyPlayerSetPlayLimitReached() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(true)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(0)).setPlayLimitReached()
    }

    @Test
    fun playVideoByTrack_isPlayLimitEnabledFalse_notVerifyPlayerSetPlayLimitReached() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(0)).setPlayLimitReached()
        verify(musicPlayerFacade, times(0)).getTotalPlays()
    }

    @Test
    fun playVideoByTrack_isDuckingFalse_verifyPlayerRequestAudioFocusAndSetVolume() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(1f)
    }

    @Test
    fun playVideoByTrack_isDuckingTrue_verifyPlayerRequestAudioFocusAndSetVolume() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))
        musicPlayerController.onLostAudioFocus(true)

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(0)).requestAudioFocus()
        verify(playerSurface, times(1)).setVolume(0.3f)
    }

    @Test
    fun playVideoByTrack_playerSourceNotNull_verifyStreamTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))

        // When
        musicPlayerController.playVideo(
            MockDataModel.mockTrack.apply {
                playerSource = MockDataModel.mockTrack
            }
        )

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun playVideoByTrack_setAvailableActionsIsVideoTrue_verifyPlayerSetAvailableActions() {
        // Given
        val mockResult = mutableListOf(
            PlaybackStateCompat.ACTION_PLAY_PAUSE,
            PlaybackStateCompat.ACTION_SEEK_TO
        )
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(playerSurface, times(1)).setAvailableActions(mockResult)
    }

    @Test
    fun playVideoByTrack_streamTrackSuccess_mediaAssetLocation_verifyIsStreamingEnabled() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(
            Single.just(MediaAsset(id = 2, location = "location"))
        )

        // When
        whenever(musicPlayerFacade.isStreamingEnabled()).thenReturn(false)

        // Then
        musicPlayerController.playVideo(MockDataModel.mockTrack)
        verify(musicPlayerFacade, times(1)).streamTrack(any())
        verify(musicPlayerFacade, times(1)).isStreamingEnabled()
    }

    @Test
    fun playVideoByTrack_streamTrackSuccess_mediaAssetLocationPrefix_notVerifyIsStreamingEnabled() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(
            Single.just(MediaAsset(id = 2, location = "file://location"))
        )
        whenever(musicPlayerFacade.isStreamingEnabled()).thenReturn(false)

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
        verify(musicPlayerFacade, times(0)).isStreamingEnabled()
    }

    @Test
    fun playVideoByTrack_streamTrackError_verifyStreamTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.error(Throwable("error")))

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun playVideoByTrack_streamTrackHttpException404_verifyStreamTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun playVideoByTrack_streamTrackHttpException426_verifyStreamTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_UPGRADE_REQUIRED,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun playVideoByTrack_streamTrackHttpException403_verifyStreamTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_FORBIDDEN,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        // When
        musicPlayerController.playVideo(MockDataModel.mockTrack)

        // Then
        verify(musicPlayerFacade, times(1)).streamTrack(any())
    }

    @Test
    fun playVideoById_currentMediaNull_verifyLoadTrack() {
        // Given
        whenever(musicPlayerFacade.loadTrack(any())).thenReturn(Single.just(MockDataModel.mockTrack))

        // When
        musicPlayerController.playVideo(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadTrack(1)
    }

    @Test
    fun playVideoById_matchId_notVerifyLoadTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))
        musicPlayerController.playVideo(MockDataModel.mockTrack.apply { id = 1 })

        // When
        musicPlayerController.playVideo(1)

        // Then
        verify(musicPlayerFacade, times(0)).loadTrack(1)
    }

    @Test
    fun playVideoById_notMatchId_verifyLoadTrack() {
        // Given
        whenever(musicPlayerFacade.hasTrackPlayRight()).thenReturn(true)
        whenever(musicPlayerFacade.isPlayLimitEnabled()).thenReturn(false)
        whenever(musicPlayerFacade.getTotalPlays()).thenReturn(Single.just(1))
        whenever(musicPlayerFacade.streamTrack(any())).thenReturn(Single.just(MediaAsset(id = 2)))
        whenever(musicPlayerFacade.loadTrack(any())).thenReturn(Single.just(MockDataModel.mockTrack))
        musicPlayerController.playVideo(MockDataModel.mockTrack.apply { id = 2 })

        // When
        musicPlayerController.playVideo(1)

        // Then
        verify(musicPlayerFacade, times(1)).loadTrack(1)
    }

    @Test
    fun onGetAvailableSkip_returnAvailableSkipsDefault() {
        // When
        val result = musicPlayerController.onGetAvailableSkip()

        // Then
        assertEquals(0, result)
    }

    @Test
    fun onGetCurrentQueue_playQueueIsNotNull_returnTrackQueueInfo() {
        // Given
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            repeatMode = PlayQueue.REPEAT_MODE_ALL
        }
        musicPlayerController.setPrivateDataFirstSection(playQueue = mockPlayQueue)

        // When
        val result = musicPlayerController.onGetCurrentQueue()

        // Then
        assertNotNull(result)
    }

    @Test
    fun onGetCurrentQueue_playQueueIsNull_returnNull() {
        // Given
        musicPlayerController.setPrivateDataFirstSection(playQueue = null)

        // When
        val result = musicPlayerController.onGetCurrentQueue()

        // Then
        assertNull(result)
    }

    @Test
    fun onPlayTrack_playQueueNull_isShufflePlayEnabledFalse_verifyNull() {
        // Given
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(false)

        // When
        musicPlayerController.onPlayTrack(1)

        // Then
        assertEquals(null, musicPlayerController.onGetCurrentQueue())
    }

    @Test
    fun onPlayTrack_playQueueNull_isShufflePlayEnabledTrue_verifyNull() {
        // Given
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onPlayTrack(1)

        // Then
        assertEquals(null, musicPlayerController.onGetCurrentQueue())
    }

    @Test
    fun onSkipToTrack_playQueueNull_repeatModeOne_verifyPlayerSetPlayMode() {
        // Given
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToTrack(1)

        // Then
        verify(musicPlayerFacade, times(1)).setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
        verify(playerSurface, times(1)).setPlayMode(true, PlaybackStateCompat.REPEAT_MODE_ALL)
    }

    @Test
    fun onSkipToTrack_playQueueNull_repeatModeAll_notVerifyPlayerSetPlayMode() {
        // Given
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ALL)
        whenever(musicPlayerFacade.isShufflePlayEnabled()).thenReturn(true)

        // When
        musicPlayerController.onSkipToTrack(1)

        // Then
        verify(musicPlayerFacade, times(0)).setRepeatMode(any())
        verify(playerSurface, times(0)).setPlayMode(any(), any())
    }

    @Test
    fun onAddTracks_playQueueNull_verifyPlayerSetQueueUpdated() {
        // When
        musicPlayerController.onAddTracks(listOf())

        // Then
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onRemoveTrack_playQueueNull_verifyPlayerSetQueueUpdated() {
        // When
        musicPlayerController.onRemoveTrack(0)

        // Then
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onRemoveTrack_removeCurrentTrackIsTrue_repeatModeIsRepeatModeOne_setRepeatMode() {
        // Given
        val mockCurrentIndex = 0
        val mockPlayQueue = TrackQueue(listOf(MockDataModel.mockTrack)).apply {
            shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
            currentIndex = mockCurrentIndex
        }
        musicPlayerController.setPrivateDataFirstSection(
            playQueue = mockPlayQueue,
            currentSource = null
        )
        whenever(musicPlayerFacade.getRepeatMode()).thenReturn(PlayQueue.REPEAT_MODE_ONE)

        // When
        musicPlayerController.onRemoveTrack(mockCurrentIndex)

        // Then
        verify(musicPlayerFacade, times(1)).getRepeatMode()
        verify(musicPlayerFacade, times(1)).setRepeatMode(any())
    }

    @Test
    fun onMoveTrack_playQueueNull_verifyPlayerSetQueueUpdated() {
        // When
        musicPlayerController.onMoveTrack(1, 0)

        // Then
        verify(playerSurface, times(1)).setQueueUpdated()
    }

    @Test
    fun onClearQueue_playQueueNull_verifyPlayerResetMetaData() {
        // When
        musicPlayerController.onClearQueue()

        // Then
        verify(playerSurface, times(1)).stop()
        verify(playerSurface, times(1)).resetMetaData()
        verify(playerSurface, times(1)).setQueueUpdated()
    }
}
