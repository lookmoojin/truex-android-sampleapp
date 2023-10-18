package com.truedigital.features.tuned.presentation.productlist.presenter

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacade
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ProductListPresenter @Inject constructor(private val facade: ProductListFacade) : Presenter {

    companion object {
        const val INITIAL_OFFSET = 1

        val FAV_PAGES = listOf(
            ProductListType.FAV_STATIONS,
            ProductListType.FOLLOWED_ARTISTS,
            ProductListType.FAV_ALBUMS,
            ProductListType.FAV_PLAYLISTS,
            ProductListType.FAV_SONGS,
            ProductListType.FAV_VIDEOS
        )

        private const val SORT_TYPE_NEW_RELEASE = "newrelease"
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface
    private var gridPageSize = 0
    private var horizontalPageSize = 0
    private var itemOffset = INITIAL_OFFSET
    private lateinit var productType: ProductListType
    private var productTag: String? = null
    private var productId: Int = 0
    private var tagWillTargetTime = false

    private val pageSize
        get() = if (view.isGridMode()) {
            gridPageSize
        } else {
            horizontalPageSize
        }

    private var productsObservable: Single<List<Product>>? = null
    private var productsSubscription: Disposable? = null

    private var products = mutableListOf<Product>()

    fun onInject(
        view: ViewSurface,
        router: RouterSurface,
        gridPageSize: Int,
        horizontalPageSize: Int
    ) {
        this.view = view
        this.router = router
        this.gridPageSize = gridPageSize
        this.horizontalPageSize = horizontalPageSize
    }

    // region Lifecycle

    override fun onStart(arguments: Bundle?) {
        arguments?.let {
            val typeString = it.getString(ProductListActivity.PRODUCT_LIST_TYPE_KEY)
            productType = typeString?.let { type ->
                ProductListType.valueOf(type)
            } ?: ProductListType.UNSUPPORTED
            productId = it.getInt(ProductListActivity.PRODUCT_LIST_ID_KEY)
            productTag = it.getString(ProductListActivity.PRODUCT_LIST_TAG_KEY).orEmpty()
            tagWillTargetTime = it.getBoolean(ProductListActivity.TARGET_TIME_KEY)
        }
        loadData()
    }

    override fun onPause() {
        productsSubscription?.dispose()
    }

    override fun onResume() {
        productsSubscription = getProductListSubscription()
    }

    // endregion

    // region Callbacks

    fun onItemSelected(
        item: Product,
        tagDisplayType: TagDisplayType,
        displayTitle: Boolean
    ) {
        when (item) {
            is Artist -> router.navigateToArtist(item)
            is Release -> router.navigateToAlbum(item.id)
            is Album -> router.navigateToAlbum(item.id)
            is Playlist -> router.navigateToPlaylist(item.id)
            is Track ->
                if (item.isVideo)
                    view.playVideo(item)
                else {
                    val tracks = products as List<Track>
                    val playableTracks = tracks.filter { it.allowStream }
                    if (playableTracks.isEmpty()) return

                    val startIndex = playableTracks.indexOfFirst { it.id == item.id }
                    view.playTracks(playableTracks, startIndex)
                }
            is Station -> router.navigateToStation(item)
            is Tag -> router.navigateToTag(item, tagDisplayType, displayTitle)
        }
    }

    fun onItemLongClick(item: Product) {
        if (item !is Tag)
            view.showBottomSheet(item)
    }

    fun onMoreSelected(item: Product) {
        if (item !is Tag)
            view.showBottomSheet(item)
    }

    fun onLoadPage() {
        if (productsObservable == null) {
            loadData()
            productsSubscription = getProductListSubscription()
        }
    }

    fun onChangeDisplayMode(morePages: Boolean) {
        view.changeView()
        if (productsObservable == null) {
            if (products.isEmpty()) {
                if (productType in FAV_PAGES) {
                    view.showFavEmpty()
                } else {
                    view.showError()
                }
            } else {
                view.showProducts(products, morePages)
            }
        }
    }

    fun onRetryClicked() {
        resetListData()
        productsSubscription = getProductListSubscription()
    }

    // endregion

    private fun loadData() {
        when (productType) {
            ProductListType.FAV_STATIONS -> loadFavouriteStations()
            ProductListType.FOLLOWED_ARTISTS -> loadFollowedArtists()
            ProductListType.FAV_ALBUMS -> loadFavouriteAlbums()
            ProductListType.FAV_PLAYLISTS -> loadFavouritePlaylists()
            ProductListType.FAV_SONGS -> loadFavouriteSongs()
            ProductListType.FAV_VIDEOS -> loadFavouriteVideos()
            ProductListType.TAGGED_STATIONS -> loadTaggedStations(productTag.orEmpty())
            ProductListType.TAGGED_ARTISTS -> loadTaggedArtists(productTag.orEmpty())
            ProductListType.TAGGED_ALBUMS -> loadTaggedAlbums(productTag.orEmpty())
            ProductListType.TAGGED_PLAYLISTS -> loadTaggedPlaylists(productTag.orEmpty())
            ProductListType.TRENDING_STATIONS -> loadTrendingStations()
            ProductListType.TRENDING_ARTISTS -> loadTrendingArtists()
            ProductListType.TRENDING_ALBUMS -> loadTrendingAlbums()
            ProductListType.TRENDING_PLAYLISTS -> loadTrendingPlaylists()
            ProductListType.SUGGESTED_STATIONS -> loadSuggestedStations()
            ProductListType.RECOMMENDED_ARTISTS -> loadRecommendedArtists()
            ProductListType.NEW_RELEASES -> loadNewReleases()
            ProductListType.DISCOVER_BYTAG -> loadDiscoverByTags(productTag.orEmpty())
            ProductListType.STATION_FEATURE_ARTIST -> loadStationFeaturedArtists(productId)
            ProductListType.STATION_SIMILAR -> loadStationSimilar(productId)
            ProductListType.ARTIST_VIDEO -> loadArtistVideos(productId)
            ProductListType.ARTIST_STATION -> loadArtistAppearsIn(productId)
            ProductListType.ARTIST_ALBUM -> loadArtistAlbums(productId)
            ProductListType.ARTIST_APPEAR_ON -> loadArtistAppearsOn(productId)
            ProductListType.ARTIST_SIMILAR -> loadArtistSimilar(productId)
            ProductListType.ARTIST_TRACKS -> loadArtistPopularSongs(productId)
            ProductListType.ARTIST_LATEST -> loadArtistLatestSongs(productId)
            ProductListType.UNSUPPORTED -> view.showError()
            else -> {
                // do noting
            }
        }
    }

    fun onListItemRemoved(position: Int) {
        products.removeAt(position)
        itemOffset--
        if (products.isEmpty()) view.showFavEmpty()
    }

    fun onListItemUpdated(position: Int, item: Product) {
        products[position] = item
    }

    fun onItemAdded(product: Product) {
        products.add(0, product)
        itemOffset++
    }

    fun onMoreOptionSelected(selection: PickerOptions) {
        when (selection) {
            PickerOptions.ADD_TO_QUEUE -> view.showAddToQueueToast()
            else -> {
                // Do nothing
            }
        }
    }

    fun onFavoriteSelect(isFavorite: Boolean, isSuccess: Boolean) {
        if (isFavorite) {
            if (isSuccess) {
                view.showAddToFavoriteSuccessToast()
            } else {
                view.showAddToFavoriteFailToast()
            }
        } else {
            if (isSuccess) {
                view.showRemoveFromFavoriteSuccessToast()
            } else {
                view.showRemoveFromFavoriteFailToast()
            }
        }
    }

    private fun resetListData() {
        itemOffset = INITIAL_OFFSET
        products.clear()
        loadData()
    }

    // region RX

    private fun loadFavouriteStations() {
        productsObservable = facade.loadFavouriteStations(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadFollowedArtists() {
        productsObservable = facade.loadFollowedArtists(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadFavouriteAlbums() {
        productsObservable = facade.loadFavouriteAlbums(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadFavouritePlaylists() {
        productsObservable = facade.loadFavouritePlaylists(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadFavouriteSongs() {
        productsObservable = facade.loadFavouriteSongs(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadFavouriteVideos() {
        productsObservable = facade.loadFavouriteVideos(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTaggedStations(tag: String) {
        productsObservable = facade.loadStationsWithTag(tag, itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTaggedArtists(tag: String) {
        productsObservable = facade.loadArtistsWithTag(tag, itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTaggedAlbums(tag: String) {
        productsObservable = if (tagWillTargetTime)
            facade.loadAlbumsByTagGroup(tag, itemOffset, pageSize)
                .cacheOnMainThread() as Single<List<Product>>
        else
            facade.loadAlbumsWithTag(tag, itemOffset, pageSize)
                .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTaggedPlaylists(tag: String) {
        productsObservable = facade.loadPlaylistsWithTag(tag, itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTrendingStations() {
        productsObservable =
            facade.loadTrendingStations().cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadTrendingArtists() {
        productsObservable = facade.loadTrendingArtists(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTrendingAlbums() {
        productsObservable = facade.loadTrendingAlbums(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadTrendingPlaylists() {
        productsObservable = facade.loadTrendingPlaylists(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadSuggestedStations() {
        productsObservable = facade.loadSuggestedStations(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadRecommendedArtists() {
        productsObservable = facade.loadRecommendedArtists(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadNewReleases() {
        productsObservable = facade.loadNewReleases(itemOffset, pageSize)
            .cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadDiscoverByTags(tags: String) {
        productsObservable =
            facade.loadMultipleTags(tags).cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadStationFeaturedArtists(productId: Int) {
        productsObservable = facade.loadStationFeaturedArtists(productId)
            .cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadStationSimilar(productId: Int) {
        productsObservable =
            facade.loadStationSimilar(productId).cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadArtistVideos(productId: Int) {
        productsObservable = facade.loadArtistVideoAppearsIn(
            productId,
            itemOffset,
            pageSize,
            SORT_TYPE_NEW_RELEASE
        ).cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadArtistAppearsIn(productId: Int) {
        productsObservable =
            facade.loadArtistAppearsIn(productId).cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadArtistAlbums(productId: Int) {
        productsObservable = facade.loadArtistAlbums(
            productId,
            itemOffset,
            pageSize,
            SORT_TYPE_NEW_RELEASE
        ).cacheOnMainThread() as Single<List<Product>>
        itemOffset += pageSize
    }

    private fun loadArtistAppearsOn(productId: Int) {
        productsObservable = facade.loadArtistAppearsOn(productId, SORT_TYPE_NEW_RELEASE)
            .cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadArtistSimilar(productId: Int) {
        productsObservable =
            facade.loadArtistSimilar(productId).cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadArtistPopularSongs(productId: Int) {
        productsObservable =
            facade.loadArtistPopularSongs(productId).cacheOnMainThread() as Single<List<Product>>
    }

    private fun loadArtistLatestSongs(productId: Int) {
        productsObservable =
            facade.loadArtistLatestSongs(productId).cacheOnMainThread() as Single<List<Product>>
    }

    private fun getProductListSubscription(): Disposable? =
        productsObservable?.doOnSubscribe {
            if (products.isEmpty()) {
                view.showLoading()
            }
        }?.tunedSubscribe(
            { productList ->
                productsObservable = null

                // due to caching and expiring, new releases or other calls may return dupe results during paging
                // the following block is trying to filter out the dupe results
                // this does not apply to daily motion calls as it always has the same internal id
                val currentItemSize = products.size
                val retrievedItemSize = productList.size
                val mergedList = products.map { it.id }.toMutableList()
                mergedList.addAll(productList.map { it.id })
                val uniqueItemSize = mergedList.toSet().size
                val dupeCount = currentItemSize + retrievedItemSize - uniqueItemSize
                if (dupeCount != 0) {
                    itemOffset = itemOffset - pageSize + dupeCount
                    loadData()
                    productsSubscription = getProductListSubscription()
                    return@tunedSubscribe
                }

                products.addAll(productList)
                when (productType) {
                    ProductListType.TRENDING_STATIONS,
                    ProductListType.DISCOVER_BYTAG,
                    ProductListType.STATION_FEATURE_ARTIST,
                    ProductListType.STATION_SIMILAR,
                    ProductListType.ARTIST_STATION,
                    ProductListType.ARTIST_APPEAR_ON,
                    ProductListType.ARTIST_SIMILAR,
                    ProductListType.ARTIST_TRACKS,
                    ProductListType.ARTIST_LATEST ->
                        view.showProducts(products, morePages = false)
                    else ->
                        view.showProducts(products, morePages = (productList.size >= pageSize))
                }
            },
            {
                productsObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (products.isEmpty()) {
                        view.showFavEmpty()
                    } else {
                        view.showProducts(products, false)
                    }
                } else {
                    view.showError()
                }
            }
        )

    // endregion

    @VisibleForTesting
    fun setPrivateData(
        productsObservable: Single<List<Product>>? = null,
        productsSubscription: Disposable? = null,
        productType: ProductListType = ProductListType.UNSUPPORTED,
        products: MutableList<Product> = mutableListOf()
    ) {
        this.productsObservable = productsObservable
        this.productsSubscription = productsSubscription
        this.productType = productType
        this.products = products
    }

    interface ViewSurface {
        fun changeView()
        fun showProducts(products: List<Product>, morePages: Boolean)
        fun showError()
        fun setViewMode()
        fun showLoading()
        fun showFavEmpty()
        fun isGridMode(): Boolean
        fun isFavActivity(): Boolean
        fun showBottomSheet(item: Product)
        fun playVideo(video: Track)
        fun playTracks(tracks: List<Track>, startIndex: Int)
        fun showAddToQueueToast()
        fun showAddToFavoriteSuccessToast()
        fun showAddToFavoriteFailToast()
        fun showRemoveFromFavoriteSuccessToast()
        fun showRemoveFromFavoriteFailToast()
    }

    interface RouterSurface {
        fun navigateToArtist(artist: Artist)
        fun navigateToAlbum(id: Int)
        fun navigateToPlaylist(id: Int)
        fun navigateToStation(station: Station)
        fun navigateToTag(
            tag: Tag,
            tagDisplayType: TagDisplayType,
            displayTitle: Boolean
        )
    }
}
