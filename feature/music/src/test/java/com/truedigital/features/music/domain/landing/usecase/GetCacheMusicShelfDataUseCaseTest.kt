package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetCacheMusicShelfDataUseCaseTest {

    private val cacheMusicLandingRepository: CacheMusicLandingRepository = mock()
    private lateinit var getCacheMusicShelfDataUseCase: GetCacheMusicShelfDataUseCase

    private val playlistShelfItemMock = MusicForYouItemModel.PlaylistShelfItem(
        playlistId = 1,
        "coverImage",
        "name",
        null
    )
    private val musicForYouShelfMock = MusicForYouShelfModel(
        index = 0,
        title = "title",
        productListType = ProductListType.TAGGED_PLAYLISTS,
        itemList = listOf(playlistShelfItemMock)
    )

    @BeforeEach
    fun setUp() {
        getCacheMusicShelfDataUseCase = GetCacheMusicShelfDataUseCaseImpl(
            cacheMusicLandingRepository
        )
    }

    @Test
    fun execute_baseShelfIdIsNull_returnNull() {
        // When
        val result = getCacheMusicShelfDataUseCase.execute(null)

        // Then
        assertNull(result)
    }

    @Test
    fun execute_baseShelfIdIsEmpty_returnNull() {
        // When
        val result = getCacheMusicShelfDataUseCase.execute("")

        // Then
        assertNull(result)
    }

    @Test
    fun execute_baseShelfIsNotNullOrEmpty_shelfDataIsNotNull_returnShelfData() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        whenever(cacheMusicLandingRepository.loadMusicShelfData(anyOrNull())).thenReturn(
            Pair(
                mockBaseShelfId,
                mutableListOf(musicForYouShelfMock)
            )
        )

        // When
        val result = getCacheMusicShelfDataUseCase.execute(mockBaseShelfId)

        // Then
        assertEquals(mockBaseShelfId, result?.first)
        assertEquals(musicForYouShelfMock, result?.second?.first())
    }

    @Test
    fun execute_baseShelfIsNotNullOrEmpty_shelfDataIsNull_returnNull() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        whenever(cacheMusicLandingRepository.loadMusicShelfData(anyOrNull())).thenReturn(null)

        // When
        val result = getCacheMusicShelfDataUseCase.execute(mockBaseShelfId)

        // Then
        assertNull(result)
    }
}
