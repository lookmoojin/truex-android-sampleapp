package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SaveCacheMusicShelfDataUseCaseTest {

    private val cacheMusicLandingRepository: CacheMusicLandingRepository = mock()
    private lateinit var saveCacheMusicShelfDataUseCase: SaveCacheMusicShelfDataUseCase
    private val musicForYouShelfMock = MusicForYouShelfModel(
        index = 0,
        title = "title",
        productListType = ProductListType.TAGGED_PLAYLISTS
    )

    @BeforeEach
    fun setUp() {
        saveCacheMusicShelfDataUseCase = SaveCacheMusicShelfDataUseCaseImpl(
            cacheMusicLandingRepository
        )
    }

    @Test
    fun execute_shelfDataIsSaved() {
        // When
        val mockForYouShelfList = mutableListOf(musicForYouShelfMock)
        saveCacheMusicShelfDataUseCase.execute("baseShelfId", mockForYouShelfList)

        // Then
        verify(cacheMusicLandingRepository, times(1)).saveMusicShelfData(any(), any())
    }
}
