package com.truedigital.features.music.widget.favorite

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.music.domain.favorite.AddMusicFavoriteUseCaseImpl
import com.truedigital.features.music.domain.favorite.FetchMusicFavoriteUseCaseImpl
import com.truedigital.features.music.domain.favorite.RemoveMusicFavoriteUseCaseImpl
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicFavoriteViewModelTest {

    private lateinit var musicFavoriteViewModel: MusicFavoriteViewModel
    private val addMusicFavoriteUseCase: AddMusicFavoriteUseCaseImpl = mock()
    private val removeMusicFavoriteUseCase: RemoveMusicFavoriteUseCaseImpl = mock()
    private val fetchMusicFavoriteUseCase: FetchMusicFavoriteUseCaseImpl = mock()

    @BeforeEach
    fun setup() {
        musicFavoriteViewModel = MusicFavoriteViewModel(
            addMusicFavoriteUseCase,
            fetchMusicFavoriteUseCase,
            removeMusicFavoriteUseCase
        )
    }

    @Test
    fun testFetchIsFavSong_returnTrue() {
        val favId = 1
        val favTypes = FavoriteType.TRACK
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(true)
        )

        musicFavoriteViewModel.fetchFavorite(favId, favTypes)

        assertEquals(
            true,
            musicFavoriteViewModel.onFavSong().value
        )
    }

    @Test
    fun testFetchIsFavSong_returnFalse() {
        val favId = 1
        val favTypes = FavoriteType.TRACK
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(false)
        )

        musicFavoriteViewModel.fetchFavorite(favId, favTypes)

        assertEquals(
            false,
            musicFavoriteViewModel.onFavSong().value
        )
    }

    @Test
    fun testFetchIsFavSong_whenError() {
        val favId = 1
        val favTypes = FavoriteType.TRACK
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.error(Throwable("onError"))
        )

        musicFavoriteViewModel.fetchFavorite(favId, favTypes)

        verify(fetchMusicFavoriteUseCase, times(1)).execute(any(), any())
    }

    @Test
    fun testToggleFav_whenAddFav_thenSuccess() {
        val favId = 1
        val favTypes = FavoriteType.TRACK
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(false)
        )
        whenever(addMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(Any())
        )

        musicFavoriteViewModel.toggleFavorite(favId, favTypes)

        assert(musicFavoriteViewModel.onAddFavToast().value == Unit)
    }

    @Test
    fun testToggleFav_whenRemoveFav_thenSuccess() {
        val favId = 1
        val favTypes = FavoriteType.TRACK
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(true)
        )
        whenever(removeMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(Any())
        )

        musicFavoriteViewModel.toggleFavorite(favId, favTypes)

        assert(musicFavoriteViewModel.onRemoveFavToast().value == Unit)
    }

    @Test
    fun testToggleFav_isFavoriteIsTrue_thenError() {
        val favId = 1
        val favTypes = FavoriteType.UNKNOWN
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(false)
        )
        musicFavoriteViewModel.setPrivateData(true)

        musicFavoriteViewModel.toggleFavorite(favId, favTypes)

        assert(musicFavoriteViewModel.onFavErrorToast().value == Unit)
        assert(musicFavoriteViewModel.onFavAddErrorToast().value == Unit)
    }

    @Test
    fun testToggleFav_isFavoriteIsFalse_thenError() {
        val favId = 1
        val favTypes = FavoriteType.UNKNOWN
        whenever(fetchMusicFavoriteUseCase.execute(favId, favTypes)).thenReturn(
            Single.just(false)
        )
        musicFavoriteViewModel.setPrivateData(false)

        musicFavoriteViewModel.toggleFavorite(favId, favTypes)

        assert(musicFavoriteViewModel.onFavErrorToast().value == Unit)
        assert(musicFavoriteViewModel.onFavRemoveErrorToast().value == Unit)
    }
}
