package com.truedigital.features.music.presentation.musicshare

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCase
import com.truedigital.common.share.nativeshare.utils.OneLinkCallback
import com.truedigital.common.share.nativeshare.utils.OneLinkGenerator
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicShareViewModelTest {
    private lateinit var viewModel: MusicShareViewModel
    private val generateDeeplinkFormatUseCase: GenerateDeeplinkFormatUseCase = mock()
    private val oneLinkGenerator: OneLinkGenerator = mock()

    @BeforeEach
    fun setUp() {
        viewModel = MusicShareViewModel(
            generateDeeplinkFormatUseCase, oneLinkGenerator
        )
    }

    @Test
    fun shareAlbum_successGenerateOneLink_verifyOpenNativeShareManager() {
        val mockId = "1234"
        val mockDeeplinkUrl = "https://music.trueid.net/album/$mockId"
        val oneLinkUrl = "https://trueid.onelink.me/cGLv/$mockId"
        whenever(generateDeeplinkFormatUseCase.execute(any(), any())).thenReturn(mockDeeplinkUrl)
        whenever(oneLinkGenerator.generateOneLink(any(), anyOrNull()))
            .thenAnswer { (it.arguments[1] as OneLinkCallback).onSuccess(oneLinkUrl) }

        viewModel.shareAlbum(mockId)

        verify(generateDeeplinkFormatUseCase, times(1)).execute(any(), any())
        verify(oneLinkGenerator, times(1)).generateOneLink(any(), anyOrNull())
        assertEquals(viewModel.onOpenNativeShare().getOrAwaitValue(), oneLinkUrl)
    }

    @Test
    fun shareAlbum_failureGenerateOneLink_verifyNotOpenNativeShareManager() {
        val mockId = "1234"
        val mockDeeplinkUrl = "https://music.trueid.net/album/145637613$mockId"
        whenever(generateDeeplinkFormatUseCase.execute(any(), any())).thenReturn(mockDeeplinkUrl)
        whenever(oneLinkGenerator.generateOneLink(any(), anyOrNull()))
            .thenAnswer { (it.arguments[1] as OneLinkCallback).onFailure("error") }

        viewModel.shareAlbum(mockId)

        verify(generateDeeplinkFormatUseCase, times(1)).execute(any(), any())
        verify(oneLinkGenerator, times(1)).generateOneLink(any(), anyOrNull())
    }

    @Test
    fun sharePlaylist_successGenerateOneLink_verifyOpenNativeShareManager() {
        val mockId = "1234"
        val mockDeeplinkUrl = "https://music.trueid.net/playlist/338002$mockId"
        val oneLinkUrl = "https://trueid.onelink.me/cGLv/338002$mockId"
        whenever(generateDeeplinkFormatUseCase.execute(any(), any())).thenReturn(mockDeeplinkUrl)
        whenever(oneLinkGenerator.generateOneLink(any(), anyOrNull()))
            .thenAnswer { (it.arguments[1] as OneLinkCallback).onSuccess(oneLinkUrl) }

        viewModel.sharePlaylist(mockId)

        verify(generateDeeplinkFormatUseCase, times(1)).execute(any(), any())
        verify(oneLinkGenerator, times(1)).generateOneLink(any(), anyOrNull())
        assertEquals(viewModel.onOpenNativeShare().getOrAwaitValue(), oneLinkUrl)
    }

    @Test
    fun shareSong_successGenerateOneLink_verifyOpenNativeShareManager() {
        val mockId = "1234"
        val mockDeeplinkUrl = "https://music.trueid.net/song/338002$mockId"
        val oneLinkUrl = "https://trueid.onelink.me/cGLv/338002$mockId"
        whenever(generateDeeplinkFormatUseCase.execute(any(), any())).thenReturn(mockDeeplinkUrl)
        whenever(oneLinkGenerator.generateOneLink(any(), anyOrNull()))
            .thenAnswer { (it.arguments[1] as OneLinkCallback).onSuccess(oneLinkUrl) }

        viewModel.shareSong(mockId)

        verify(generateDeeplinkFormatUseCase, times(1)).execute(any(), any())
        verify(oneLinkGenerator, times(1)).generateOneLink(any(), anyOrNull())
        assertEquals(viewModel.onOpenNativeShare().getOrAwaitValue(), oneLinkUrl)
    }
}
