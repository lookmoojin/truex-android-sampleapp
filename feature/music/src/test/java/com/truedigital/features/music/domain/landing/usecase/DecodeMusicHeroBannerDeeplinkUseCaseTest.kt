package com.truedigital.features.music.domain.landing.usecase

import android.net.Uri
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCase
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DecodeMusicHeroBannerDeeplinkUseCaseTest {

    private lateinit var decodeMusicHeroBannerDeeplinkUseCase: DecodeMusicHeroBannerDeeplinkUseCase

    @MockK
    lateinit var isInternalDeeplinkUseCase: IsInternalDeeplinkUseCase

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(Uri::class)

        decodeMusicHeroBannerDeeplinkUseCase = DecodeMusicHeroBannerDeeplinkUseCaseImpl(
            isInternalDeeplinkUseCase
        )
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostAlbum_returnAlbumType() {
        // Given
        val mockId = "123456789"
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM}/$mockId"
        every { Uri.parse(mockUrl).lastPathSegment } returns mockId
        every { Uri.parse(mockUrl).pathSegments } returns listOf(com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM, mockId)

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.ALBUM, result.first)
        assertEquals(mockId, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostAlbum_lastPathSegmentNull_returnAlbumType() {
        // Given
        val mockUrl = "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM}"
        every { Uri.parse(mockUrl).lastPathSegment } returns null
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.ALBUM, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostArtist_returnArtistType() {
        // Given
        val mockId = "123456789"
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST}/$mockId"
        every { Uri.parse(mockUrl).lastPathSegment } returns mockId
        every { Uri.parse(mockUrl).pathSegments } returns listOf(com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST, mockId)

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.ARTIST, result.first)
        assertEquals(mockId, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostArtist_lastPathSegmentNull_returnArtistType() {
        // Given
        val mockUrl = "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST}"
        every { Uri.parse(mockUrl).lastPathSegment } returns null
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.ARTIST, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostPlaylist_returnPlaylistType() {
        // Given
        val mockId = "123456789"
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST}/$mockId"
        every { Uri.parse(mockUrl).lastPathSegment } returns mockId
        every { Uri.parse(mockUrl).pathSegments } returns listOf(
            com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST,
            mockId
        )

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.PLAYLIST, result.first)
        assertEquals(mockId, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostPlaylist_lastPathSegmentNull_returnPlaylistType() {
        // Given
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST}"
        every { Uri.parse(mockUrl).lastPathSegment } returns null
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.PLAYLIST, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostSong_returnSongType() {
        // Given
        val mockId = "123456789"
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG}/$mockId"
        every { Uri.parse(mockUrl).pathSegments } returns listOf(mockId)
        every { Uri.parse(mockUrl).lastPathSegment } returns mockId

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.SONG, result.first)
        assertEquals(mockId, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostSong_lastPathSegmentNull_returnSongType() {
        // Given
        val mockUrl = "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG}"
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()
        every { Uri.parse(mockUrl).lastPathSegment } returns null

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.SONG, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostRadio_lastPathIsNotNull_returnRadioType() {
        // Given
        val mockId = "123456789"
        val mockUrl =
            "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.RADIO}/$mockId"
        every { Uri.parse(mockUrl).pathSegments } returns listOf(mockId)
        every { Uri.parse(mockUrl).lastPathSegment } returns mockId

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.RADIO, result.first)
        assertEquals(mockId, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostRadio_lastPathSegmentNull_returnSeeMoreRadioType() {
        // Given
        val mockUrl = "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.RADIO}"
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()
        every { Uri.parse(mockUrl).lastPathSegment } returns null

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHostRadio_lastPathSegmentEmpty_returnSeeMoreRadioType() {
        // Given
        val mockUrl = "https://${MusicConstant.Deeplink.DEEPLINK_HOST}/${MusicConstant.Type.RADIO}"
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()
        every { Uri.parse(mockUrl).lastPathSegment } returns ""

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.SEE_MORE_RADIO, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsMusicHost_returnExternalBrowserType() {
        // Given
        val mockUrl = "https://${com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST}"
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, result.first)
        assertEquals("", result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsTrueIdListenHostSong_firstPathSegmentIsListen_returnSongType() {
        // Given
        val mockUrl =
            "https://${DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID_HTTP}/${com.truedigital.features.listens.share.constant.MusicConstant.Slug.LISTEN}/${com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG}"
        every { Uri.parse(mockUrl).pathSegments } returns listOf(
            com.truedigital.features.listens.share.constant.MusicConstant.Slug.LISTEN,
            com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG
        )
        every { Uri.parse(mockUrl).lastPathSegment } returns com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.SONG, result.first)
        assertEquals(com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsTrueIdListenHostSong_firstPathSegmentIsNotListen_isInternalDeeplink_returnSongInternalDeeplink() {
        // Given
        val mockUrl =
            "https://${DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID_HTTP}/home/${com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG}"
        every { Uri.parse(mockUrl).pathSegments } returns listOf("home", com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG)
        every { Uri.parse(mockUrl).lastPathSegment } returns com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG
        every { isInternalDeeplinkUseCase.execute(any()) } returns true

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.INTERNAL_DEEPLINK, result.first)
        assertEquals(mockUrl, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsInternal_returnInternalBrowserType() {
        // Given
        val mockUrl = "https://${DeeplinkConstants.DeeplinkConstants.HOST_ONELINK}"
        every { isInternalDeeplinkUseCase.execute(any()) } returns true
        every { Uri.parse(mockUrl).pathSegments } returns emptyList()

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute(mockUrl)

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.INTERNAL_DEEPLINK, result.first)
        assertEquals(mockUrl, result.second)
    }

    @Test
    fun testDecodeDeeplink_urlIsEmpty_returnExternalBrowserType() {
        // Given
        every { Uri.parse("").pathSegments } returns emptyList()
        every { isInternalDeeplinkUseCase.execute(any()) } returns false

        // When
        val result = decodeMusicHeroBannerDeeplinkUseCase.execute("")

        // Then
        assertEquals(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, result.first)
        assertEquals("", result.second)
    }
}
