package com.truedigital.features.tuned.presentation.bottomsheet.presenter

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacade
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.common.Presenter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class BottomSheetProductPresenter @Inject constructor(
    private val bottomSheetProductFacade: BottomSheetProductFacade
) : Presenter {

    companion object {
        const val PRODUCT_ID_KEY = "product_id"
        const val HAVE_COLLECTION_STATUS = "have_collection_status"

        val TRACK_ITEM_TYPES = listOf(
            ProductPickerType.SONG,
            ProductPickerType.VIDEO,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.VIDEO_PLAYER,
            ProductPickerType.MY_PLAYLIST_SONG
        )
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var productObservable: Single<Product>? = null
    private var productSubscription: Disposable? = null

    private var isInCollectionObservable: Single<Boolean>? = null
    private var isInCollectionSubscription: Disposable? = null
    private var hasArtistMixObservable: Single<Boolean>? = null
    private var hasArtistMixSubscription: Disposable? = null

    private var addToCollectionObservable: Single<Any>? = null
    private var addToCollectionSubscription: Disposable? = null
    private var removeFromCollectionObservable: Single<Any>? = null
    private var removeFromCollectionSubscription: Disposable? = null
    private var tracksObservable: Single<List<Track>>? = null
    private var tracksSubscription: Disposable? = null

    private var clearVotesObservable: Single<Any>? = null
    private var clearVotesSubscription: Disposable? = null

    private var product: Product? = null
    private var productType: ProductPickerType? = null
    private var haveCollectionStatus = false
    private var isFavourited: Boolean = false

    fun onInject(
        view: ViewSurface,
        router: RouterSurface,
        product: Product?,
        productType: ProductPickerType?
    ) {
        this.view = view
        this.router = router
        this.product = product
        this.productType = productType
    }

    override fun onStart(arguments: Bundle?) {
        super.onStart(arguments)
        arguments?.let {
            haveCollectionStatus = it.getBoolean(HAVE_COLLECTION_STATUS, false)
            if (product == null) {
                val productId = it.getInt(PRODUCT_ID_KEY)
                productObservable = productType?.let { pickerType ->
                    getProductObservable(productId, pickerType)
                }
                productSubscription = getProductSubscription()
            } else {
                adjustProductOptions()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        productSubscription?.dispose()
        isInCollectionSubscription?.dispose()
        hasArtistMixSubscription?.dispose()
    }

    fun adjustProductType() {
        if (productType == ProductPickerType.PLAYLIST) {
            val playlist = product as Playlist
            // use blocking get here to avoid mess up with init options and adjust options
            productType =
                bottomSheetProductFacade.checkPlaylistType(playlist.creatorId).blockingGet()
        }
        productType?.let { view.updateProductType(it) }
    }

    private fun adjustProductOptions() {
        // adjust options based on asynchronous requests
        if (!haveCollectionStatus) {
            isInCollectionObservable = productType?.let { type ->
                getIsInCollectionObservable(product?.id ?: 0, type)
            }
            isInCollectionSubscription = getIsInCollectionSubscription()
        }

        if (productType == ProductPickerType.ARTIST) {
            hasArtistMixObservable = getHasArtistMixObservable(product?.id ?: 0)
            hasArtistMixSubscription = getHasArtistMixSubscription()
        }
    }

    fun onOptionsSelected(item: PickerOptions) {
        when (item) {
            PickerOptions.ADD_TO_COLLECTION -> addToCollection()
            PickerOptions.REMOVE_FROM_COLLECTION -> removeFromCollection()
            PickerOptions.FOLLOW -> addToCollection()
            PickerOptions.UNFOLLOW -> removeFromCollection()
            PickerOptions.ADD_TO_QUEUE -> addToQueue()
            PickerOptions.REMOVE_FROM_QUEUE -> view.removeFromQueue()
            PickerOptions.SHOW_ARTIST -> showArtist()
            PickerOptions.SHOW_ALBUM -> showAlbum()
            PickerOptions.PLAYER_SETTINGS -> router.showPlayerSetting()
            PickerOptions.PLAY_VIDEO -> playVideo()
            PickerOptions.CLEAR_VOTE -> onClearVotes()
            PickerOptions.ADD_TO_MY_PLAYLIST -> view.showAddToMyPlaylist()
            else -> { /*do nothing*/
            }
        }
    }

    private fun addToCollection() {
        if (addToCollectionObservable == null) {
            addToCollectionObservable = productType?.let { type ->
                getAddToCollectionObservable(product?.id ?: 0, type)
            }
            addToCollectionSubscription = getAddToCollectionSubscription()
        }
    }

    private fun removeFromCollection() {
        if (removeFromCollectionObservable == null) {
            removeFromCollectionObservable = productType?.let { type ->
                getRemoveFromCollectionObservable(product?.id ?: 0, type)
            }
            removeFromCollectionSubscription = getRemoveFromCollectionSubscription()
        }
    }

    private fun addToQueue() {
        val pickerType = productType
        when {
            TRACK_ITEM_TYPES.contains(pickerType) -> view.addToQueue(
                listOf(product as Track),
                false
            )
            pickerType == ProductPickerType.ALBUM ||
                pickerType == ProductPickerType.PLAYLIST ||
                pickerType == ProductPickerType.PLAYLIST_OWNER ||
                pickerType == ProductPickerType.PLAYLIST_VERIFIED_OWNER ->
                if (tracksObservable == null) {
                    tracksObservable =
                        product?.id?.let { productId -> getTracksObservable(productId, pickerType) }
                    tracksSubscription = getPlayTracksSubscription()
                }
            else -> throw IllegalStateException("This type does not support add to queue")
        }
    }

    private fun showArtist() {
        when {
            TRACK_ITEM_TYPES.contains(productType) -> (product as Track).artists.firstOrNull()
                ?.let { router.showArtist(it.id) }
            productType == ProductPickerType.ALBUM -> {
                val release =
                    if (product is Album) (product as Album).primaryRelease else product as Release
                release?.artists?.firstOrNull()?.let { router.showArtist(it.id) }
            }
            else -> throw IllegalStateException("This type does not support show artist")
        }
    }

    private fun showAlbum() {
        when {
            TRACK_ITEM_TYPES.contains(productType) -> router.showAlbum((product as Track).releaseId)
            else -> throw IllegalStateException("This type does not support show album")
        }
    }

    private fun playVideo() {
        when {
            TRACK_ITEM_TYPES.contains(productType) -> {
                val track = product as Track
                when {
                    track.isVideo -> view.playVideo(track)
                    track.video != null -> view.playVideo(track.video as Track)
                    else -> throw IllegalStateException("This track object has no video attached")
                }
            }
            else -> throw IllegalStateException("This type does not support play video")
        }
    }

    private fun onClearVotes() {
        when (productType) {
            ProductPickerType.ARTIST,
            ProductPickerType.MIX -> {
                if (clearVotesObservable == null) {
                    clearVotesObservable = product?.let { _product ->
                        getClearVotesObservable(
                            _product.id,
                            productType
                        )
                    }
                    clearVotesSubscription = getClearVotesSubscription()
                }
            }
            else -> throw IllegalStateException("This type does not support play video")
        }
    }

    // region RX

    private fun getProductObservable(productId: Int, productPickerType: ProductPickerType) =
        bottomSheetProductFacade.getProduct(productId, productPickerType).cacheOnMainThread()

    private fun getProductSubscription() =
        productObservable?.tunedSubscribe(
            {
                productObservable = null
                product = it
                view.showProduct(it)
                adjustProductOptions()
            },
            {
                productObservable = null
                view.showProductError()
            }
        )

    private fun getIsInCollectionObservable(productId: Int, productPickerType: ProductPickerType) =
        bottomSheetProductFacade.isInCollection(productId, productPickerType).cacheOnMainThread()

    private fun getIsInCollectionSubscription() =
        isInCollectionObservable?.tunedSubscribe(
            {
                isInCollectionObservable = null
                isFavourited = it
                view.updateCollectionStatus(
                    if (it) {
                        ProductPickerCollectionStatus.IN_COLLECTION
                    } else {
                        ProductPickerCollectionStatus.NOT_IN_COLLECTION
                    }
                )
            },
            {
                isInCollectionObservable = null
            }
        )

    private fun getHasArtistMixObservable(artistId: Int) =
        bottomSheetProductFacade.getHasArtistAndSimilarStation(artistId).cacheOnMainThread()

    private fun getHasArtistMixSubscription() =
        hasArtistMixObservable?.tunedSubscribe(
            {
                hasArtistMixObservable = null
                if (it) view.showArtistClearVote()
            },
            {
                hasArtistMixObservable = null
            }
        )

    private fun getAddToCollectionObservable(productId: Int, productPickerType: ProductPickerType) =
        bottomSheetProductFacade.addToCollection(productId, productPickerType).cacheOnMainThread()

    private fun getAddToCollectionSubscription() =
        addToCollectionObservable?.tunedSubscribe(
            {
                addToCollectionObservable = null
                isFavourited = true
                view.showFavouritedToast(true)
                view.inCollectionStatusChanged(isFavourited)
            },
            {
                addToCollectionObservable = null
                view.showFavouritedError(isFavourited = true)
            }
        )

    private fun getRemoveFromCollectionObservable(
        productId: Int,
        productPickerType: ProductPickerType
    ) =
        bottomSheetProductFacade.removeFromCollection(productId, productPickerType)
            .cacheOnMainThread()

    private fun getRemoveFromCollectionSubscription() =
        removeFromCollectionObservable?.tunedSubscribe(
            {
                removeFromCollectionObservable = null
                isFavourited = false
                view.showFavouritedToast(false)
                view.inCollectionStatusChanged(isFavourited)
            },
            {
                removeFromCollectionObservable = null
                view.showFavouritedError(isFavourited = false)
            }
        )

    private fun getTracksObservable(productId: Int, productPickerType: ProductPickerType) =
        bottomSheetProductFacade.getTracksForProduct(productId, productPickerType)
            .doOnSubscribe { view.showLoading() }
            .cacheOnMainThread()

    private fun getPlayTracksSubscription() =
        tracksObservable?.tunedSubscribe(
            { trackList ->
                tracksObservable = null
                val playableTracks = trackList.filter { it.allowStream }
                if (playableTracks.isNotEmpty())
                    view.addToQueue(playableTracks, false)
                else
                    view.showGenericErrorMessage()
            },
            {
                tracksObservable = null
                view.showGenericErrorMessage()
            }
        )

    private fun getClearVotesObservable(productId: Int, productPickerType: ProductPickerType?) =
        bottomSheetProductFacade.clearVotes(productId, productPickerType).cacheOnMainThread()

    private fun getClearVotesSubscription() =
        clearVotesObservable?.tunedSubscribe(
            {
                clearVotesObservable = null
                view.showVotesCleared()
            },
            {
                clearVotesObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showVotesCleared()
                } else {
                    view.showClearVotesError()
                }
            }
        )

    @VisibleForTesting
    fun setSubscription(
        productSubscription: Disposable? = null,
        isInCollectionSubscription: Disposable? = null,
        hasArtistMixSubscription: Disposable? = null,
    ) {
        this.productSubscription = productSubscription
        this.isInCollectionSubscription = isInCollectionSubscription
        this.hasArtistMixSubscription = hasArtistMixSubscription
    }

    @VisibleForTesting
    fun setObservableFirstSection(
        productObservable: Single<Product>? = null,
        isInCollectionObservable: Single<Boolean>? = null,
        hasArtistMixObservable: Single<Boolean>? = null,
        addToCollectionObservable: Single<Any>? = null,
        removeFromCollectionObservable: Single<Any>? = null

    ) {
        this.productObservable = productObservable
        this.isInCollectionObservable = isInCollectionObservable
        this.hasArtistMixObservable = hasArtistMixObservable
        this.addToCollectionObservable = addToCollectionObservable
        this.removeFromCollectionObservable = removeFromCollectionObservable
    }

    @VisibleForTesting
    fun setObservableSecondSection(
        tracksObservable: Single<List<Track>>? = null,
        clearVotesObservable: Single<Any>? = null
    ) {
        this.tracksObservable = tracksObservable
        this.clearVotesObservable = clearVotesObservable
    }

    @VisibleForTesting
    fun testSubscription() {
        productSubscription = getProductSubscription()
        isInCollectionSubscription = getIsInCollectionSubscription()
        hasArtistMixSubscription = getHasArtistMixSubscription()
        addToCollectionSubscription = getAddToCollectionSubscription()
        removeFromCollectionSubscription = getRemoveFromCollectionSubscription()
        tracksSubscription = getPlayTracksSubscription()
        clearVotesSubscription = getClearVotesSubscription()
    }

    // endregion

    interface ViewSurface {
        fun showProduct(fetchedProduct: Product? = null)
        fun showProductError()
        fun updateProductType(productPickerType: ProductPickerType)

        fun updateCollectionStatus(status: ProductPickerCollectionStatus)
        fun showArtistClearVote()

        fun showLoading()
        fun showFavouritedToast(isFavourited: Boolean)
        fun showFavouritedError(isFavourited: Boolean)
        fun inCollectionStatusChanged(isInCollection: Boolean)

        fun showGenericErrorMessage()
        fun addToQueue(tracks: List<Track>, isOffline: Boolean)
        fun removeFromQueue()
        fun playVideo(video: Track)
        fun showVotesCleared()
        fun showClearVotesError()
        fun showAddToMyPlaylist()
    }

    interface RouterSurface {
        fun shareProduct(link: String)
        fun showArtist(artistId: Int)
        fun showAlbum(albumId: Int)
        fun showPlayerSetting()
    }
}
