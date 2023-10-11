package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.pm.ActivityInfo
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.jraska.livedata.TestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class VideoItemViewerViewModelTest {

    private lateinit var viewModel: VideoItemViewerViewModel

    @BeforeEach
    fun setUp() {
        viewModel = VideoItemViewerViewModel()
    }

    @Test
    fun `test onPlayWhenReadyChanged true`() = runTest {
        // arrange
        val onShowPlayTestObserver = TestObserver.test(viewModel.onShowPlay)
        val onShowPauseTestObserver = TestObserver.test(viewModel.onShowPause)

        // act
        viewModel.onPlayWhenReadyChanged(true)

        // assert
        onShowPlayTestObserver.assertNoValue()
        onShowPauseTestObserver.assertHasValue()
    }

    @Test
    fun `test onPlayWhenReadyChanged false`() = runTest {
        // arrange
        val onShowPlayTestObserver = TestObserver.test(viewModel.onShowPlay)
        val onShowPauseTestObserver = TestObserver.test(viewModel.onShowPause)

        // act
        viewModel.onPlayWhenReadyChanged(false)

        // assert
        onShowPlayTestObserver.assertHasValue()
        onShowPauseTestObserver.assertNoValue()
    }

    @Test
    fun `test onEnterFullScreenClick Portrait`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetOrientation)

        // act
        val videoSize = VideoSize(9, 16)
        viewModel.onEnterFullScreenClick(videoSize)

        // assert
        testObserver.assertValue(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
    }

    @Test
    fun `test onEnterFullScreenClick Landscape`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetOrientation)

        // act
        val videoSize = VideoSize(16, 9)
        viewModel.onEnterFullScreenClick(videoSize)

        // assert
        testObserver.assertValue(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    @Test
    fun `test onEnterFullScreenClick Null`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetOrientation)

        // act
        viewModel.onEnterFullScreenClick(null)

        // assert
        testObserver.assertValue(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
    }

    @Test
    fun `test onPlaybackStateChanged STATE_READY`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onHideProgress)

        // act
        viewModel.onPlaybackStateChanged(Player.STATE_READY)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onPlaybackStateChanged STATE_ENDED`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onResetPlayer)

        // act
        viewModel.onPlaybackStateChanged(Player.STATE_ENDED)

        // assert
        testObserver.assertHasValue()
    }
}
