package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepositoryImpl
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClearCacheMusicLandingUseCaseTest {

    private lateinit var clearCacheMusicLandingUseCase: ClearCacheMusicLandingUseCase
    private val cacheMusicLandingRepository: CacheMusicLandingRepository =
        CacheMusicLandingRepositoryImpl()

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
        clearCacheMusicLandingUseCase = ClearCacheMusicLandingUseCaseImpl(
            cacheMusicLandingRepository
        )
    }

    @Test
    fun execute_shelfDataIsCleared() {
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.saveMusicShelfData(mockBaseShelfId, mutableListOf(musicForYouShelfMock))
        assertNotNull(cacheMusicLandingRepository.loadMusicShelfData(mockBaseShelfId))

        cacheMusicLandingRepository.clearCache()
        assertNull(cacheMusicLandingRepository.loadMusicShelfData(mockBaseShelfId))
    }
}
