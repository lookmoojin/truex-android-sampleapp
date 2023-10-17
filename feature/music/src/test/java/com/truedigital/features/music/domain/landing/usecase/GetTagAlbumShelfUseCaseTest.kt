package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.landing.model.response.tagalbum.TagAlbumResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
internal class GetTagAlbumShelfUseCaseTest {

    private lateinit var getTagAlbumShelfUseCase: GetTagAlbumShelfUseCase
    private val musicLandingRepository: MusicLandingRepository = mock()

    @BeforeEach
    fun setUp() {
        getTagAlbumShelfUseCase = GetTagAlbumShelfUseCaseImpl(musicLandingRepository)
    }

    @Test
    fun execute_tagIsNotEmpty_success_returnCorrectData() = runTest {
        // Given
        val resultMock = TagAlbumResponse.Result(
            albumId = 100,
            name = "name",
            primaryRelease = TagAlbumResponse.PrimaryRelease(image = "image", releaseId = 10),
            artists = listOf(TagAlbumResponse.Artist(name = "artistName"))
        )
        val tagAlbumResponseMock = TagAlbumResponse(
            results = listOf(resultMock)
        )
        whenever(musicLandingRepository.getTagAlbumShelf(any(), any(), any())).thenReturn(
            flowOf(tagAlbumResponseMock)
        )

        // When
        val flow = getTagAlbumShelfUseCase.execute("tag", "10")

        // Then
        flow.collect { musicForYouItemModelList ->
            val albumShelfItemResponse =
                musicForYouItemModelList.first() as MusicForYouItemModel.AlbumShelfItem

            assertEquals(albumShelfItemResponse.albumId, resultMock.albumId)
            assertEquals(albumShelfItemResponse.albumName, resultMock.name)
            assertEquals(albumShelfItemResponse.coverImage, resultMock.primaryRelease?.image)
            assertEquals(albumShelfItemResponse.releaseId, resultMock.primaryRelease?.releaseId)
            assertEquals(albumShelfItemResponse.artistName, resultMock.artists?.first()?.name)
        }
    }

    @Test
    fun executeSuccess_limitEmpty_returnCorrectData() = runTest {
        // Given
        val resultMock = TagAlbumResponse.Result(
            albumId = 100,
            name = "name",
            primaryRelease = TagAlbumResponse.PrimaryRelease(image = "image", releaseId = 10),
            artists = listOf(TagAlbumResponse.Artist(name = "artistName"))
        )
        val tagAlbumResponseMock = TagAlbumResponse(
            results = listOf(resultMock)
        )
        whenever(musicLandingRepository.getTagAlbumShelf(any(), any(), any())).thenReturn(
            flowOf(tagAlbumResponseMock)
        )

        // When
        val flow = getTagAlbumShelfUseCase.execute("tag", "")

        // Then
        flow.collect { musicForYouItemModelList ->
            val albumShelfItemResponse =
                musicForYouItemModelList.first() as MusicForYouItemModel.AlbumShelfItem

            assertEquals(albumShelfItemResponse.albumId, resultMock.albumId)
            assertEquals(albumShelfItemResponse.albumName, resultMock.name)
            assertEquals(albumShelfItemResponse.coverImage, resultMock.primaryRelease?.image)
            assertEquals(albumShelfItemResponse.releaseId, resultMock.primaryRelease?.releaseId)
            assertEquals(albumShelfItemResponse.artistName, resultMock.artists?.first()?.name)
        }
        verify(musicLandingRepository, times(1)).getTagAlbumShelf("tag", 1, 10)
    }

    @Test
    fun execute_tagIsEmpty_returnError() = runTest {
        // When
        val flow = getTagAlbumShelfUseCase.execute("", "10")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, GetTagAlbumShelfUseCaseImpl.ERROR_TAG_EMPTY)
        }.collect { }
    }

    @Test
    fun execute_fail_returnEmptyList() = runTest {
        // Given
        whenever(
            musicLandingRepository.getTagAlbumShelf(
                any(),
                any(),
                any()
            )
        ).thenReturn(flow { Exception() })

        // When
        val flow = getTagAlbumShelfUseCase.execute("tag", "10")

        // Then
        flow.collect { musicForYouItemModelList ->
            assertTrue(musicForYouItemModelList.isEmpty())
        }
    }
}
