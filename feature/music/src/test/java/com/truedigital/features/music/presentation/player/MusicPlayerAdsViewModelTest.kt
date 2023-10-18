package com.truedigital.features.music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.ads.usecase.ActionPreviousNextUseCase
import com.truedigital.features.music.domain.ads.usecase.ClearCacheMusicPlayerAdsUseCase
import com.truedigital.features.music.domain.ads.usecase.GetMusicPlayerAdsUrlUseCase
import com.truedigital.features.music.domain.ads.usecase.IsShowMusicPlayerAdsUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicPlayerAdsViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var viewModel: MusicPlayerAdsViewModel

    @MockK
    lateinit var actionPreviousNextUseCase: ActionPreviousNextUseCase

    @MockK
    lateinit var clearCacheMusicPlayerAdsUseCase: ClearCacheMusicPlayerAdsUseCase

    @MockK
    lateinit var getMusicPlayerAdsUrlUseCase: GetMusicPlayerAdsUrlUseCase

    @MockK
    lateinit var isShowMusicPlayerAdsUseCase: IsShowMusicPlayerAdsUseCase

    @MockK
    lateinit var mediaSession: MediaSessionCompat

    @MockK
    lateinit var mediaControllerCompat: MediaControllerCompat

    @MockK
    lateinit var mediaMetadataCompat: MediaMetadataCompat

    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = MusicPlayerAdsViewModel(
            coroutineDispatcher,
            actionPreviousNextUseCase,
            clearCacheMusicPlayerAdsUseCase,
            getMusicPlayerAdsUrlUseCase,
            isShowMusicPlayerAdsUseCase
        )
    }

    @Test
    fun actionPrevious_isFirstTrackTrue_notVerifyActionPreviousTrue() {
        viewModel.actionPrevious(true, 1000L)

        verify(exactly = 0) { actionPreviousNextUseCase.execute(true, 1000L) }
    }

    @Test
    fun actionPrevious_isFirstTrackFalse_verifyActionPreviousTrue() {
        every { actionPreviousNextUseCase.execute(any(), any()) } returns Unit

        viewModel.actionPrevious(false, 1000L)

        verify { actionPreviousNextUseCase.execute(true, 1000L) }
    }

    @Test
    fun actionNext_isLastTrackTrue_notVerifyActionPreviousFalse() {
        viewModel.actionNext(true)

        verify(exactly = 0) { actionPreviousNextUseCase.execute(false) }
    }

    @Test
    fun actionNext_isLastTrackFalse_verifyActionPreviousFalse() {
        every { actionPreviousNextUseCase.execute(any(), any()) } returns Unit

        viewModel.actionNext(false)

        verify { actionPreviousNextUseCase.execute(false) }
    }

    @Test
    fun validateAds_metaDataNull_verifyClearCache() {
        every { clearCacheMusicPlayerAdsUseCase.execute() } returns Unit

        viewModel.validateAds(null)

        verify { clearCacheMusicPlayerAdsUseCase.execute() }
    }

    @Test
    fun validateAds_metaDataNotNull_isNotShowAds_verifyNotGetMusicPlayerAdsUrl() {
        every { mediaSession.controller } returns mediaControllerCompat
        every { mediaSession.controller.metadata } returns mediaMetadataCompat
        every { isShowMusicPlayerAdsUseCase.execute() } returns false

        viewModel.validateAds(mediaSession.controller.metadata)

        verify(exactly = 0) { getMusicPlayerAdsUrlUseCase.execute() }
    }

    @Test
    fun validateAds_metaDataNotNull_isShowAds_urlNotEmpty_returnAdsUrl() = runTest {
        every { mediaSession.controller } returns mediaControllerCompat
        every { mediaSession.controller.metadata } returns mediaMetadataCompat
        every { isShowMusicPlayerAdsUseCase.execute() } returns true
        every { getMusicPlayerAdsUrlUseCase.execute() } returns flowOf("url")

        viewModel.validateAds(mediaSession.controller.metadata)

        assert(viewModel.onShowAds().getOrAwaitValue() == "url")
        verify { getMusicPlayerAdsUrlUseCase.execute() }
    }

    @Test
    fun validateAds_metaDataNotNull_isShowAds_urlIsEmpty_notReturnAdsUrl() = runTest {
        every { mediaSession.controller } returns mediaControllerCompat
        every { mediaSession.controller.metadata } returns mediaMetadataCompat
        every { isShowMusicPlayerAdsUseCase.execute() } returns true
        every { getMusicPlayerAdsUrlUseCase.execute() } returns flowOf("")

        viewModel.validateAds(mediaSession.controller.metadata)

        verify { getMusicPlayerAdsUrlUseCase.execute() }
    }
}
