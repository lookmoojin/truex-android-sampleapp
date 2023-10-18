package com.truedigital.features.tuned.presentation.productlist

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacade
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.productlist.presenter.ProductListPresenter
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ProductListPresenterTest {

    private lateinit var productListPresenter: ProductListPresenter
    private val productListFacade: ProductListFacade = mock()
    private val view: ProductListPresenter.ViewSurface = mock()
    private val router: ProductListPresenter.RouterSurface = mock()
    private val bundle = Mockito.mock(Bundle::class.java)
    private val productsSubscription: Disposable = mock()

    private var gridPageSize = 10
    private var horizontalPageSize = 10

    @BeforeEach
    fun setup() {
        productListPresenter = ProductListPresenter(productListFacade)
        productListPresenter.onInject(view, router, gridPageSize, horizontalPageSize)
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        productListPresenter.setPrivateData(productsSubscription = productsSubscription)

        productListPresenter.onPause()

        verify(productsSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        productListPresenter.setPrivateData(productsSubscription = null)

        productListPresenter.onPause()

        verify(productsSubscription, times(0)).dispose()
    }

    @Test
    fun testOnItemSelect_whenRoute2Artist() {

        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockArtist,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToArtist(MockDataModel.mockArtist)
    }

    @Test
    fun testOnItemSelect_whenRoute2Release() {
        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockRelease,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToAlbum(MockDataModel.mockRelease.albumId)
    }

    @Test
    fun testOnItemSelect_whenRoute2Album() {
        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockAlbum,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToAlbum(MockDataModel.mockRelease.albumId)
    }

    @Test
    fun testOnItemSelect_whenRoute2Playlist() {
        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockPlaylist,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToPlaylist(MockDataModel.mockPlaylist.id)
    }

    @Test
    fun testOnItemSelect_whenRoute2Track_isVideo() {
        // given
        val trackIsVideo = MockDataModel.mockTrack.copy(isVideo = true)
        // when
        productListPresenter.onItemSelected(
            trackIsVideo,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(view, times(1)).playVideo(trackIsVideo)
    }

    @Test
    fun testOnItemSelect_whenRoute2Track_isNotVideo() {
        // when
        productListPresenter.onItemAdded(MockDataModel.mockTrack)
        productListPresenter.onItemSelected(
            MockDataModel.mockTrack,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(view, times(1)).playTracks(any(), any())
    }

    @Test
    fun testOnItemSelect_whenRoute2Track_isVideoNotAllowStream() {
        // given
        val trackIsVideo = MockDataModel.mockTrack.copy(allowStream = false)
        // when
        productListPresenter.onItemAdded(trackIsVideo)
        productListPresenter.onItemSelected(
            trackIsVideo,
            TagDisplayType.LANDSCAPE,
            true
        )
    }

    @Test
    fun testOnItemSelect_whenRoute2Station() {
        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockStation,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToStation(MockDataModel.mockStation)
    }

    @Test
    fun testOnItemSelect_whenRoute2Tag() {
        // when
        productListPresenter.onItemSelected(
            MockDataModel.mockTag,
            TagDisplayType.LANDSCAPE,
            true
        )
        // then
        verify(router, times(1)).navigateToTag(any(), any(), any())
    }

    @Test
    fun testOnItemLongClick() {
        // when
        productListPresenter.onItemLongClick(MockDataModel.mockTag)
        productListPresenter.onItemLongClick(MockDataModel.mockAlbum)
        // then
        verify(view, times(1)).showBottomSheet(MockDataModel.mockAlbum)
    }

    @Test
    fun testOnMoreSelected() {
        // when
        productListPresenter.onMoreSelected(MockDataModel.mockTag)
        productListPresenter.onMoreSelected(MockDataModel.mockArtist)
        // then
        verify(view, times(1)).showBottomSheet(MockDataModel.mockArtist)
    }

    @Test
    fun testOnLoadPage() {
        // given
        doReturn(ProductListType.FAV_PLAYLISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouritePlaylists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockPlaylist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onItemAdded(MockDataModel.mockArtist)
        productListPresenter.onLoadPage()

        // then
        verify(productListFacade, times(1)).loadFavouritePlaylists(any(), any())
    }

    @Test
    fun testOnChangeDisplayMode_whenProductTypeNotFav() {
        // given
        doReturn(ProductListType.TRACKS_PLAYLIST.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)
        productListPresenter.onStart(bundle)

        // when
        productListPresenter.onChangeDisplayMode(true)
        // then
        verify(view, times(1)).changeView()
        verify(view, times(1)).showError()
    }

    @Test
    fun testOnChangeDisplayMode_whenMorePage_productNotEmpty() {
        // when
        productListPresenter.onItemAdded(MockDataModel.mockPlaylist)
        productListPresenter.onChangeDisplayMode(true)
        // then
        verify(view, times(1)).changeView()
        verify(view, times(1)).showProducts(any(), any())
    }

    @Test
    fun onChangeDisplayMode_productsObservableIsNull_productsIsEmpty_productTypeInFavPages_showFavEmpty() {
        // Given
        productListPresenter.setPrivateData(
            productsObservable = null,
            products = mutableListOf(),
            productType = ProductListType.FOLLOWED_ARTISTS
        )

        // When
        productListPresenter.onChangeDisplayMode(true)

        // Then
        verify(view, times(1)).changeView()
        verify(view, times(1)).showFavEmpty()
    }

    @Test
    fun testOnRetryClicked() {
        // given
        doReturn(ProductListType.FAV_PLAYLISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouritePlaylists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockPlaylist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onRetryClicked()

        verify(productListFacade, times(2)).loadFavouritePlaylists(any(), any())
    }

    @Test
    fun testOnListItemRemoved() {
        // when
        productListPresenter.onItemAdded(MockDataModel.mockArtist)
        productListPresenter.onListItemRemoved(0)

        // then
        verify(view, times(1)).showFavEmpty()
    }

    @Test
    fun testOnListItemUpdated() {
        // when
        productListPresenter.onItemAdded(MockDataModel.mockArtist)
        productListPresenter.onListItemUpdated(0, MockDataModel.mockAlbum)
    }

    /**
     *
     * */

    @Test
    fun testLoadData_whenFavStation() {
        // given
        doReturn(ProductListType.FAV_STATIONS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouriteStations(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadFavouriteStations(any(), any())
    }

    @Test
    fun testLoadData_whenFollowedArtists() {
        // given
        doReturn(ProductListType.FOLLOWED_ARTISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFollowedArtists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadFollowedArtists(any(), any())
    }

    @Test
    fun testLoadData_whenFavAlbum() {
        // given
        doReturn(ProductListType.FAV_ALBUMS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouriteAlbums(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockRelease))
        )

        // when
        productListPresenter.onResume()
        productListPresenter.onStart(bundle)

        // then
        verify(productListFacade, times(1)).loadFavouriteAlbums(any(), any())
    }

    @Test
    fun testLoadData_whenFavSong() {
        // given
        doReturn(ProductListType.FAV_SONGS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouriteSongs(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadFavouriteSongs(any(), any())
    }

    @Test
    fun testLoadData_whenFavVideo() {
        // given
        doReturn(ProductListType.FAV_VIDEOS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadFavouriteVideos(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadFavouriteVideos(any(), any())
    }

    @Test
    fun testLoadData_whenTagStation() {
        // given
        doReturn(ProductListType.TAGGED_STATIONS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(
            productListFacade.loadStationsWithTag(any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadStationsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadData_whenTagArtists() {
        // given
        doReturn(ProductListType.TAGGED_ARTISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(
            productListFacade.loadArtistsWithTag(any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadData_whenTagAlbum() {
        // given
        doReturn(ProductListType.TAGGED_ALBUMS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        doReturn(false).whenever(bundle)
            .getBoolean(ProductListActivity.TARGET_TIME_KEY)

        whenever(productListFacade.loadAlbumsWithTag(any(), any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadAlbumsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadData_whenTagAlbumGroup() {
        // given
        doReturn(ProductListType.TAGGED_ALBUMS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        doReturn(true).whenever(bundle)
            .getBoolean(ProductListActivity.TARGET_TIME_KEY)

        whenever(
            productListFacade.loadAlbumsByTagGroup(any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadAlbumsByTagGroup(any(), any(), any())
    }

    @Test
    fun testLoadData_whenTagPlaylist() {
        // given
        doReturn(ProductListType.TAGGED_PLAYLISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(
            productListFacade.loadPlaylistsWithTag(any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockPlaylist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadPlaylistsWithTag(any(), any(), any())
    }

    @Test
    fun testLoadData_whenTrendStation() {
        // given
        doReturn(ProductListType.TRENDING_STATIONS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadTrendingStations()).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadTrendingStations()
    }

    @Test
    fun testLoadData_whenTrendArtist() {
        // given
        doReturn(ProductListType.TRENDING_ARTISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadTrendingArtists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadTrendingArtists(any(), any())
    }

    @Test
    fun testLoadData_whenTrendAlbum() {
        // given
        doReturn(ProductListType.TRENDING_ALBUMS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadTrendingAlbums(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadTrendingAlbums(any(), any())
    }

    @Test
    fun testLoadData_whenTrendPlaylist() {
        // given
        doReturn(ProductListType.TRENDING_PLAYLISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadTrendingPlaylists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockPlaylist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadTrendingPlaylists(any(), any())
    }

    @Test
    fun testLoadData_whenSuggestStation() {
        // given
        doReturn(ProductListType.SUGGESTED_STATIONS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadSuggestedStations(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )
        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadSuggestedStations(any(), any())
    }

    @Test
    fun testLoadData_whenRecommendedArtists() {
        // given
        doReturn(ProductListType.RECOMMENDED_ARTISTS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadRecommendedArtists(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadRecommendedArtists(any(), any())
    }

    @Test
    fun testLoadData_whenNewReleases() {
        // given
        doReturn(ProductListType.NEW_RELEASES.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadNewReleases(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadNewReleases(any(), any())
    }

    @Test
    fun testLoadData_whenDiscoverByTags() {
        // given
        doReturn(ProductListType.DISCOVER_BYTAG.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadMultipleTags(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTag))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadMultipleTags(any())
    }

    @Test
    fun testLoadData_whenStationFeaturedArtists() {
        // given
        doReturn(ProductListType.STATION_FEATURE_ARTIST.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadStationFeaturedArtists(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadStationFeaturedArtists(any())
    }

    @Test
    fun testLoadData_whenStationSimilar() {
        // given
        doReturn(ProductListType.STATION_SIMILAR.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadStationSimilar(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadStationSimilar(any())
    }

    @Test
    fun testLoadData_whenArtistVideos() {
        // given
        doReturn(ProductListType.ARTIST_VIDEO.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(
            productListFacade.loadArtistVideoAppearsIn(any(), any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistVideoAppearsIn(any(), any(), any(), any())
    }

    @Test
    fun testLoadData_whenArtistAppearsIn() {
        // given
        doReturn(ProductListType.ARTIST_STATION.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadArtistAppearsIn(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistAppearsIn(any())
    }

    @Test
    fun testLoadData_whenArtistAlbums() {
        // given
        doReturn(ProductListType.ARTIST_ALBUM.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(
            productListFacade.loadArtistAlbums(any(), any(), any(), any())
        ).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun testLoadData_whenArtistAppearsOn() {
        // given
        doReturn(ProductListType.ARTIST_APPEAR_ON.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadArtistAppearsOn(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistAppearsOn(any(), any())
    }

    @Test
    fun testLoadData_whenArtistSimilar() {
        // given
        doReturn(ProductListType.ARTIST_SIMILAR.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadArtistSimilar(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistSimilar(any())
    }

    @Test
    fun testLoadData_whenArtistPopularSongs() {
        // given
        doReturn(ProductListType.ARTIST_TRACKS.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadArtistPopularSongs(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistPopularSongs(any())
    }

    @Test
    fun testLoadData_whenArtistLatestSongs() {
        // given
        doReturn(ProductListType.ARTIST_LATEST.name).whenever(bundle)
            .getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)

        whenever(productListFacade.loadArtistLatestSongs(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // when
        productListPresenter.onStart(bundle)
        productListPresenter.onResume()

        // then
        verify(productListFacade, times(1)).loadArtistLatestSongs(any())
    }

    @Test
    fun testLoadData_whenUnknownType() {
        // when
        productListPresenter.onStart(bundle)

        // then
        verify(view, times(1)).showError()
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToQueue_showAddToQueueToast() {
        // When
        productListPresenter.onMoreOptionSelected(PickerOptions.ADD_TO_QUEUE)
        // Then
        verify(view, times(1)).showAddToQueueToast()
    }

    @Test
    fun onMoreOptionSelected_selectionIsElse_doNothing() {
        // When
        productListPresenter.onMoreOptionSelected(PickerOptions.SHOW_ARTIST)
        // Then
        verify(view, times(0)).showAddToQueueToast()
    }

    @Test
    fun onFavoriteSelect_isFavorite_isSuccess_showAddToFavoriteSuccessToast() {
        // When
        productListPresenter.onFavoriteSelect(isFavorite = true, isSuccess = true)

        // Then
        verify(view, times(1)).showAddToFavoriteSuccessToast()
    }

    @Test
    fun onFavoriteSelect_isFavorite_isNotSuccess_showAddToFavoriteFailToast() {
        // When
        productListPresenter.onFavoriteSelect(isFavorite = true, isSuccess = false)

        // Then
        verify(view, times(1)).showAddToFavoriteFailToast()
    }

    @Test
    fun onFavoriteSelect_isNotFavorite_isSuccess_showRemoveFromFavoriteSuccessToast() {
        // When
        productListPresenter.onFavoriteSelect(isFavorite = false, isSuccess = true)

        // Then
        verify(view, times(1)).showRemoveFromFavoriteSuccessToast()
    }

    @Test
    fun onFavoriteSelect_isNotFavorite_isNotSuccess_showRemoveFromFavoriteFailToast() {
        // When
        productListPresenter.onFavoriteSelect(isFavorite = false, isSuccess = false)

        // Then
        verify(view, times(1)).showRemoveFromFavoriteFailToast()
    }
}
