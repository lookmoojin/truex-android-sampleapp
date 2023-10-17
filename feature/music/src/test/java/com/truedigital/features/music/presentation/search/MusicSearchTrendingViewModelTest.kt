package com.truedigital.features.music.presentation.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepository
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingAlbumUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingAlbumUseCaseImpl
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingArtistsUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingArtistsUseCaseImpl
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingPlaylistUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingPlaylistUseCaseImpl
import com.truedigital.features.music.presentation.searchtrending.MusicSearchTrendingViewModel
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicSearchTrendingViewModelTest {

    private lateinit var viewModel: MusicSearchTrendingViewModel
    private val getMusicTrendingArtistsUseCase: GetMusicTrendingArtistsUseCase = mock()
    private val getMusicTrendingPlaylistUseCase: GetMusicTrendingPlaylistUseCase = mock()
    private val getMusicTrendingAlbumUseCase: GetMusicTrendingAlbumUseCase = mock()
    private val musicTrendingArtistCacheRepository: MusicTrendingArtistCacheRepository = mock()
    private val musicTrendingPlaylistCacheRepository: MusicTrendingPlaylistCacheRepository = mock()
    private val musicTrendingAlbumCacheRepository: MusicTrendingAlbumCacheRepository = mock()
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        viewModel = MusicSearchTrendingViewModel(
            getMusicTrendingArtistsUseCase,
            getMusicTrendingPlaylistUseCase,
            getMusicTrendingAlbumUseCase,
            musicTrendingArtistCacheRepository,
            musicTrendingPlaylistCacheRepository,
            musicTrendingAlbumCacheRepository
        )
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        validateMockitoUsage()
        Dispatchers.resetMain()
    }

    @Test
    fun searchTrending_success_dataItemsIsNotEmpty_showTrendingDataItems() = runTest {
        val responseArtistDataItems = getArtistDataItems()
        val responsePlaylistDataItems = getPlaylistDataItems()
        val responseAlbumDataItems = getAlbumDataItems()

        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flowOf(responseArtistDataItems))
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(
            flowOf(
                responsePlaylistDataItems
            )
        )
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flowOf(responseAlbumDataItems))

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)

        viewModel.onTrendingDataItems().getOrAwaitValue().also { dataItems ->
            assertEquals(
                dataItems[0] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[1] is MusicSearchTrendingAdapter.DataItem.TrendingArtistItem,
                true
            )
            assertEquals(
                dataItems[2] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[3] is MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem,
                true
            )

            val firstItem = dataItems[0] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(firstItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            assertEquals(
                firstItem.title,
                GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_TH
            )

            val secondItem = dataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem
            val expectArtistData =
                (responseArtistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem).trendingArtistList.first()
            assertEquals(secondItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID)
            assertEquals(secondItem.trendingArtistList.first().id, expectArtistData.id)
            assertEquals(secondItem.trendingArtistList.first().name, expectArtistData.name)
            assertEquals(secondItem.trendingArtistList.first().image, expectArtistData.image)

            val thirdItem = dataItems[2] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(thirdItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            assertEquals(
                thirdItem.title,
                GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN
            )

            val fourthItem =
                dataItems[3] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val expectPlaylistData =
                (responsePlaylistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem).trendingPlaylistList.first()
            assertEquals(fourthItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            assertEquals(fourthItem.trendingPlaylistList.first().id, expectPlaylistData.id)
            assertEquals(fourthItem.trendingPlaylistList.first().name, expectPlaylistData.name)
            assertEquals(fourthItem.trendingPlaylistList.first().image, expectPlaylistData.image)
        }
    }

    @Test
    fun searchTrending_success_apiArtistLoadFail_showTrendingDataItems() = runTest {
        val responsePlaylistDataItems = getPlaylistDataItems()
        val responseAlbumDataItems = getAlbumDataItems()

        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flowOf(null))
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(
            flowOf(
                responsePlaylistDataItems
            )
        )
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flowOf(responseAlbumDataItems))

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)

        viewModel.onTrendingDataItems().getOrAwaitValue().also { dataItems ->
            assertEquals(dataItems.size, 4)
            assertEquals(
                dataItems[0] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[1] is MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem,
                true
            )
            assertEquals(
                dataItems[3] is MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem,
                true
            )

            val firstItem = dataItems[0] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(firstItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            assertEquals(
                firstItem.title,
                GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN
            )

            val secondItem =
                dataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val expectPlaylistData =
                (responsePlaylistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem).trendingPlaylistList.first()
            assertEquals(secondItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            assertEquals(secondItem.trendingPlaylistList.first().id, expectPlaylistData.id)
            assertEquals(secondItem.trendingPlaylistList.first().name, expectPlaylistData.name)
            assertEquals(secondItem.trendingPlaylistList.first().image, expectPlaylistData.image)

            val thirdItem = dataItems[2] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(thirdItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            assertEquals(
                thirdItem.title,
                GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN
            )

            val fourthItem = dataItems[3] as MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem
            val expectAlbumData =
                (responseAlbumDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem).trendingAlbumList.first()
            assertEquals(fourthItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID)
            assertEquals(fourthItem.trendingAlbumList.first().id, expectAlbumData.id)
            assertEquals(fourthItem.trendingAlbumList.first().name, expectAlbumData.name)
            assertEquals(
                fourthItem.trendingAlbumList.first().artistName,
                expectAlbumData.artistName
            )
            assertEquals(fourthItem.trendingAlbumList.first().image, expectAlbumData.image)
            assertEquals(
                fourthItem.trendingAlbumList.first().artistName,
                expectAlbumData.artistName
            )
        }
    }

    @Test
    fun searchTrending_success_apiPlaylistLoadFail_showTrendingDataItems() = runTest {
        val responseArtistDataItems = getArtistDataItems()
        val responseAlbumDataItems = getAlbumDataItems()

        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flowOf(responseArtistDataItems))
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(flowOf(null))
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flowOf(responseAlbumDataItems))

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)

        viewModel.onTrendingDataItems().getOrAwaitValue().also { dataItems ->
            assertEquals(
                dataItems[0] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[1] is MusicSearchTrendingAdapter.DataItem.TrendingArtistItem,
                true
            )
            assertEquals(
                dataItems[2] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[3] is MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem,
                true
            )

            val firstItem = dataItems[0] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(firstItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            assertEquals(
                firstItem.title,
                GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_TH
            )

            val secondItem = dataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem
            val expectArtistData =
                (responseArtistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem).trendingArtistList.first()
            assertEquals(secondItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID)
            assertEquals(secondItem.trendingArtistList.first().id, expectArtistData.id)
            assertEquals(secondItem.trendingArtistList.first().name, expectArtistData.name)
            assertEquals(secondItem.trendingArtistList.first().image, expectArtistData.image)

            val thirdItem = dataItems[2] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(thirdItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID)
            assertEquals(
                thirdItem.title,
                GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN
            )

            val fourthItem = dataItems[3] as MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem
            val expectAlbumData =
                (responseAlbumDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem).trendingAlbumList.first()
            assertEquals(fourthItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID)
            assertEquals(fourthItem.trendingAlbumList.first().id, expectAlbumData.id)
            assertEquals(fourthItem.trendingAlbumList.first().name, expectAlbumData.name)
            assertEquals(fourthItem.trendingAlbumList.first().image, expectAlbumData.image)
            assertEquals(
                fourthItem.trendingAlbumList.first().artistName,
                expectAlbumData.artistName
            )
        }
    }

    @Test
    fun searchTrending_success_apiAlbumLoadFail_showTrendingDataItems() = runTest {
        val responseArtistDataItems = getArtistDataItems()
        val responsePlaylistDataItems = getPlaylistDataItems()

        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flowOf(responseArtistDataItems))
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(
            flowOf(
                responsePlaylistDataItems
            )
        )
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flowOf(null))

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)

        viewModel.onTrendingDataItems().getOrAwaitValue().also { dataItems ->
            assertEquals(
                dataItems[0] is MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem,
                true
            )
            assertEquals(
                dataItems[1] is MusicSearchTrendingAdapter.DataItem.TrendingArtistItem,
                true
            )

            val firstItem = dataItems[0] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(firstItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID)
            assertEquals(
                firstItem.title,
                GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_TH
            )

            val secondItem = dataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem
            val expectArtistData =
                (responseArtistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingArtistItem).trendingArtistList.first()
            assertEquals(secondItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID)
            assertEquals(secondItem.trendingArtistList.first().id, expectArtistData.id)
            assertEquals(secondItem.trendingArtistList.first().name, expectArtistData.name)
            assertEquals(secondItem.trendingArtistList.first().image, expectArtistData.image)

            val thirdItem = dataItems[2] as MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem
            assertEquals(thirdItem.id, MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID)
            assertEquals(
                thirdItem.title,
                GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN
            )

            val fourthItem =
                dataItems[3] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem
            val expectPlaylistData =
                (responsePlaylistDataItems[1] as MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem).trendingPlaylistList.first()
            assertEquals(fourthItem.id, MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID)
            assertEquals(fourthItem.trendingPlaylistList.first().id, expectPlaylistData.id)
            assertEquals(fourthItem.trendingPlaylistList.first().name, expectPlaylistData.name)
            assertEquals(fourthItem.trendingPlaylistList.first().image, expectPlaylistData.image)
        }
    }

    @Test
    fun searchTrending_success_dataItemIsEmpty_showError() = runTest {
        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flowOf(null))
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(flowOf(null))
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flowOf(null))

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onShowError().getOrAwaitValue(), Unit)
    }

    @Test
    fun searchTrending_fail_showError() = runTest {
        whenever(getMusicTrendingArtistsUseCase.execute()).thenReturn(flow { Exception() })
        whenever(getMusicTrendingPlaylistUseCase.execute()).thenReturn(flow { Exception() })
        whenever(getMusicTrendingAlbumUseCase.execute()).thenReturn(flow { Exception() })

        viewModel.searchTrending()
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onShowError().getOrAwaitValue(), Unit)
    }

    @Test
    fun testClearTrendingData_dataItemEmpty() = runTest {
        viewModel.clearTrendingData()

        verify(musicTrendingArtistCacheRepository, never()).clearCacheTrendingArtist()
        verify(musicTrendingPlaylistCacheRepository, never()).clearCacheTrendingPlaylist()
        verify(musicTrendingAlbumCacheRepository, never()).clearCacheTrendingAlbum()
        verify(getMusicTrendingArtistsUseCase, never()).execute()
        verify(getMusicTrendingPlaylistUseCase, never()).execute()
        verify(getMusicTrendingAlbumUseCase, never()).execute()
        assertEquals(viewModel.onTrendingDataItems().value?.size, 0)
    }

    @Test
    fun testClearCache_requestMusicTrendingArtistCacheRepository() = runTest {
        viewModel.clearCache()

        verify(musicTrendingArtistCacheRepository, times(1)).clearCacheTrendingArtist()
        verify(musicTrendingPlaylistCacheRepository, times(1)).clearCacheTrendingPlaylist()
        verify(musicTrendingAlbumCacheRepository, times(1)).clearCacheTrendingAlbum()
        verify(getMusicTrendingArtistsUseCase, never()).execute()
        verify(getMusicTrendingPlaylistUseCase, never()).execute()
    }

    private fun getArtistDataItems(): ArrayList<MusicSearchTrendingAdapter.DataItem> {
        val trendingArtistModel = arrayListOf(
            TrendingArtistModel().apply {
                this.id = 1
                this.name = "name"
                this.image = "image"
            }
        )
        return arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ARTIST_ID,
                    GetMusicTrendingArtistsUseCaseImpl.KEY_EXTRA_TRENDING_ARTISTS_TH
                )
            )
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingArtistItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ARTIST_ID,
                    trendingArtistModel
                )
            )
        }
    }

    private fun getPlaylistDataItems(): ArrayList<MusicSearchTrendingAdapter.DataItem> {
        val trendingPlaylistModel = arrayListOf(
            TrendingPlaylistModel().apply {
                this.id = 1
                this.name = "name"
                this.image = "image"
            }
        )

        return arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_HEADER_PLAYLIST_ID,
                    GetMusicTrendingPlaylistUseCaseImpl.KEY_EXTRA_TRENDING_PLAYLIST_EN
                )
            )
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingPlaylistItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_PLAYLIST_ID,
                    trendingPlaylistModel
                )
            )
        }
    }

    private fun getAlbumDataItems(): ArrayList<MusicSearchTrendingAdapter.DataItem> {
        val trendingAlbumModel = arrayListOf(
            TrendingAlbumModel().apply {
                this.id = 1
                this.name = "name"
                this.image = "image"
                this.artistName = "artistName"
            }
        )

        return arrayListOf<MusicSearchTrendingAdapter.DataItem>().apply {
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingHeaderItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_HEADER_ALBUM_ID,
                    GetMusicTrendingAlbumUseCaseImpl.KEY_EXTRA_TRENDING_ALBUM_EN
                )
            )
            this.add(
                MusicSearchTrendingAdapter.DataItem.TrendingAlbumItem(
                    MusicSearchTrendingAdapter.KEY_ITEM_CONTENT_ALBUM_ID,
                    trendingAlbumModel
                )
            )
        }
    }
}
