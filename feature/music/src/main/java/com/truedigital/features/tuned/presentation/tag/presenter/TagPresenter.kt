package com.truedigital.features.tuned.presentation.tag.presenter

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.domain.facade.tag.TagFacade
import com.truedigital.features.tuned.domain.facade.tag.model.TagDisplayType
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.common.Presenter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class TagPresenter @Inject constructor(val facade: TagFacade) : Presenter {

    companion object {
        const val TAG_KEY = "tag"
        const val TAG_NAME_KEY = "tag_name"
        const val TAG_DISPLAY_TYPE_KEY = "tag_display_type"
        const val DISPLAY_TITLE_KEY = "display_title"

        const val INITIAL_OFFSET = 1
        const val DEFAULT_PAGESIZE = 10
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var getTagContentObservable: Single<Tag>? = null
    private var getTagContentSubscription: Disposable? = null
    private var loadStationsObservable: Single<List<Station>>? = null
    private var loadStationsSubscription: Disposable? = null
    private var loadPlaylistsObservable: Single<List<Playlist>>? = null
    private var loadPlaylistsSubscription: Disposable? = null
    private var loadArtistsObservable: Single<List<Artist>>? = null
    private var loadArtistsSubscription: Disposable? = null
    private var loadAlbumsObservable: Single<List<Album>>? = null
    private var loadAlbumsSubscription: Disposable? = null

    private var tag: Tag? = null
    private var tagDisplayType: TagDisplayType = TagDisplayType.LANDSCAPE
    private var displayTitle: Boolean = true

    private var stations = mutableListOf<Station>()
    private var artists = mutableListOf<Artist>()
    private var albums = mutableListOf<Album>()
    private var playlists = mutableListOf<Playlist>()
    private var currentStationOffset = INITIAL_OFFSET
    private var currentArtistOffset = INITIAL_OFFSET
    private var currentAlbumOffset = INITIAL_OFFSET
    private var currentPlaylistOffset = INITIAL_OFFSET

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region Lifecycle

    override fun onStart(arguments: Bundle?) {
        arguments?.let {
            val tagString = it.getString(TAG_KEY)
            val tagName = it.getString(TAG_NAME_KEY)
            tagDisplayType = TagDisplayType.getValue(it.getString(TAG_DISPLAY_TYPE_KEY))
            displayTitle = it.getBoolean(DISPLAY_TITLE_KEY, true)

            when {
                tagString != null -> {
                    tag = Tag.fromString(tagString)
                    tag?.let { tag ->
                        view.showTagContent(tag, tagDisplayType, displayTitle)
                    }
                    loadTagContent(false)
                }
                tagName != null -> getTagContentObservable = getTagContentByNameObservable(tagName)
                else -> view.showTagContentError()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getTagContentSubscription = getTagContentByNameSubscription()
        loadAlbumsSubscription = getAlbumsSubscription()
        loadArtistsSubscription = getArtistsSubscription()
        loadPlaylistsSubscription = getPlaylistsSubscription()
        loadStationsSubscription = getStationsSubscription()
    }

    override fun onPause() {
        getTagContentSubscription?.dispose()
        loadAlbumsSubscription?.dispose()
        loadArtistsSubscription?.dispose()
        loadPlaylistsSubscription?.dispose()
        loadStationsSubscription?.dispose()
        super.onPause()
    }

    // endregion

    fun onPlaylistSelected(playlist: Playlist) {
        router.navigateToTaggedPlaylist(playlist)
    }

    fun onArtistSelected(artist: Artist) {
        router.navigateToTaggedArtist(artist)
    }

    fun onAlbumSelected(album: Album) {
        router.navigateToTaggedAlbum(album)
    }

    fun onStationSelected(station: Station) {
        router.navigateToTaggedStation(station)
    }

    fun onLoadArtists(doSubscribe: Boolean = true, incremental: Boolean = true) {
        if (loadArtistsObservable == null) {
            if (incremental) currentArtistOffset += DEFAULT_PAGESIZE
            loadArtistsObservable =
                getArtistsObservable(tag?.name.orEmpty(), currentArtistOffset, DEFAULT_PAGESIZE)
            if (doSubscribe) loadArtistsSubscription = getArtistsSubscription()
        }
    }

    fun onLoadAlbums(doSubscribe: Boolean = true, incremental: Boolean = true) {
        if (loadAlbumsObservable == null) {
            if (incremental) currentAlbumOffset += DEFAULT_PAGESIZE
            loadAlbumsObservable =
                getAlbumsObservable(tag?.name.orEmpty(), currentAlbumOffset, DEFAULT_PAGESIZE)
            if (doSubscribe) loadAlbumsSubscription = getAlbumsSubscription()
        }
    }

    fun onLoadPlaylists(doSubscribe: Boolean = true, incremental: Boolean = true) {
        if (loadPlaylistsObservable == null) {
            if (incremental) currentPlaylistOffset += DEFAULT_PAGESIZE
            loadPlaylistsObservable =
                getPlaylistsObservable(tag?.name.orEmpty(), currentPlaylistOffset, DEFAULT_PAGESIZE)
            if (doSubscribe) loadPlaylistsSubscription = getPlaylistsSubscription()
        }
    }

    fun onLoadStations(doSubscribe: Boolean = true, incremental: Boolean = true) {
        if (loadStationsObservable == null) {
            if (incremental) currentStationOffset += DEFAULT_PAGESIZE
            loadStationsObservable =
                getStationsObservable(tag?.name.orEmpty(), currentStationOffset, DEFAULT_PAGESIZE)
            if (doSubscribe) loadStationsSubscription = getStationsSubscription()
        }
    }

    fun onArtistSeeAllSelected() {
        router.navigateToProductListWithTag(ProductListType.TAGGED_ARTISTS, tag?.name)
    }

    fun onAlbumSeeAllSelected() {
        router.navigateToProductListWithTag(ProductListType.TAGGED_ALBUMS, tag?.name)
    }

    fun onPlaylistSeeAllSelected() {
        router.navigateToProductListWithTag(ProductListType.TAGGED_PLAYLISTS, tag?.name)
    }

    fun onStationSeeAllSelected() {
        router.navigateToProductListWithTag(ProductListType.TAGGED_STATIONS, tag?.name)
    }

    fun onRetryTaggedStation() {
        loadStationsSubscription?.dispose()
        currentStationOffset = INITIAL_OFFSET
        stations.clear()
        onLoadStations(incremental = false)
    }

    fun onRetryTaggedArtist() {
        loadArtistsSubscription?.dispose()
        currentArtistOffset = INITIAL_OFFSET
        artists.clear()
        onLoadArtists(incremental = false)
    }

    fun onRetryTaggedAlbum() {
        loadAlbumsSubscription?.dispose()
        currentAlbumOffset = INITIAL_OFFSET
        albums.clear()
        onLoadAlbums(incremental = false)
    }

    fun onRetryTaggedPlaylist() {
        loadPlaylistsSubscription?.dispose()
        currentPlaylistOffset = INITIAL_OFFSET
        playlists.clear()
        onLoadPlaylists(incremental = false)
    }

    private fun loadTagContent(doSubscribe: Boolean = true) {
        stations.clear()
        albums.clear()
        artists.clear()
        playlists.clear()
        currentStationOffset = INITIAL_OFFSET
        currentArtistOffset = INITIAL_OFFSET
        currentAlbumOffset = INITIAL_OFFSET
        currentPlaylistOffset = INITIAL_OFFSET

        onLoadAlbums(doSubscribe, false)
        onLoadArtists(doSubscribe, false)
        onLoadPlaylists(doSubscribe, false)
        onLoadStations(doSubscribe, false)
    }

    // region RX

    private fun getTagContentByNameObservable(name: String) =
        facade.getTagByName(name).cacheOnMainThread()

    private fun getTagContentByNameSubscription(): Disposable? = getTagContentObservable
        ?.tunedSubscribe(
            {
                getTagContentObservable = null
                tag = it
                view.showTagContent(it, tagDisplayType, displayTitle)
                loadTagContent()
            },
            {
                getTagContentObservable = null
                view.showTagContentError()
            }
        )

    private fun getStationsObservable(tag: String, offset: Int, count: Int) =
        facade.loadStationsWithTag(tag, offset, count)
            .doOnSubscribe { if (offset == 1) view.showTaggedStationsLoading() }
            .cacheOnMainThread()

    private fun getStationsSubscription(): Disposable? = loadStationsObservable
        ?.tunedSubscribe(
            {
                loadStationsObservable = null
                stations.addAll(it)
                view.showTaggedStations(stations, morePages = (it.size >= DEFAULT_PAGESIZE))
            },
            {
                loadStationsObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (stations.isEmpty())
                        view.hideStationsSection()
                    else
                        view.showTaggedStations(stations, false)
                } else {
                    view.showStationsError()
                }
            }
        )

    private fun getAlbumsObservable(tag: String, offset: Int, count: Int) =
        facade.loadAlbumsWithTag(tag, offset, count)
            .doOnSubscribe { if (offset == 1) view.showTaggedAlbumsLoading() }
            .cacheOnMainThread()

    private fun getAlbumsSubscription(): Disposable? = loadAlbumsObservable
        ?.tunedSubscribe(
            {
                loadAlbumsObservable = null
                albums.addAll(it)
                view.showTaggedAlbums(albums, morePages = (it.size >= DEFAULT_PAGESIZE))
            },
            {
                loadAlbumsObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (albums.isEmpty())
                        view.hideAlbumsSection()
                    else
                        view.showTaggedAlbums(albums, false)
                } else {
                    view.showAlbumsError()
                }
            }
        )

    private fun getArtistsObservable(tag: String, offset: Int, count: Int) =
        facade.loadArtistsWithTag(tag, offset, count)
            .doOnSubscribe { if (offset == 1) view.showTaggedArtistsLoading() }
            .cacheOnMainThread()

    private fun getArtistsSubscription(): Disposable? = loadArtistsObservable
        ?.tunedSubscribe(
            {
                loadArtistsObservable = null
                artists.addAll(it)
                view.showTaggedArtists(artists, morePages = (it.size >= DEFAULT_PAGESIZE))
            },
            {
                loadArtistsObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (artists.isEmpty())
                        view.hideArtistsSection()
                    else
                        view.showTaggedArtists(artists, false)
                } else {
                    view.showArtistsError()
                }
            }
        )

    private fun getPlaylistsObservable(tag: String, offset: Int, count: Int) =
        facade.loadPlaylistsWithTag(tag, offset, count)
            .doOnSubscribe { if (offset == 1) view.showTaggedPlaylistsLoading() }
            .cacheOnMainThread()

    private fun getPlaylistsSubscription(): Disposable? = loadPlaylistsObservable
        ?.tunedSubscribe(
            {
                loadPlaylistsObservable = null
                playlists.addAll(it)
                view.showTaggedPlaylists(playlists, morePages = (it.size >= DEFAULT_PAGESIZE))
            },
            {
                loadPlaylistsObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (playlists.isEmpty())
                        view.hidePlaylistsSection()
                    else
                        view.showTaggedPlaylists(playlists, false)
                } else {
                    view.showPlaylistsError()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        tag: Tag? = null,
        stations: MutableList<Station> = mutableListOf(),
        albums: MutableList<Album> = mutableListOf(),
        artists: MutableList<Artist> = mutableListOf(),
        playlists: MutableList<Playlist> = mutableListOf()
    ) {
        this.tag = tag
        this.stations = stations
        this.albums = albums
        this.artists = artists
        this.playlists = playlists
    }

    @VisibleForTesting
    fun setSubscription(
        getTagContentSubscription: Disposable? = null,
        loadStationsSubscription: Disposable? = null,
        loadPlaylistsSubscription: Disposable? = null,
        loadArtistsSubscription: Disposable? = null,
        loadAlbumsSubscription: Disposable? = null
    ) {
        this.getTagContentSubscription = getTagContentSubscription
        this.loadStationsSubscription = loadStationsSubscription
        this.loadPlaylistsSubscription = loadPlaylistsSubscription
        this.loadArtistsSubscription = loadArtistsSubscription
        this.loadAlbumsSubscription = loadAlbumsSubscription
    }

    @VisibleForTesting
    fun setObservable(
        getTagContentObservable: Single<Tag>? = null,
        loadStationsObservable: Single<List<Station>>? = null,
        loadPlaylistsObservable: Single<List<Playlist>>? = null,
        loadArtistsObservable: Single<List<Artist>>? = null,
        loadAlbumsObservable: Single<List<Album>>? = null
    ) {
        this.getTagContentObservable = getTagContentObservable
        this.loadStationsObservable = loadStationsObservable
        this.loadPlaylistsObservable = loadPlaylistsObservable
        this.loadArtistsObservable = loadArtistsObservable
        this.loadAlbumsObservable = loadAlbumsObservable
    }

    // endregion

    interface ViewSurface {
        fun showTagContent(
            tag: Tag,
            tagDisplayType: TagDisplayType,
            displayTitle: Boolean = true
        )

        fun showTagContentError()

        fun showTaggedStationsLoading()
        fun showTaggedStations(stations: List<Station>, morePages: Boolean)
        fun hideStationsSection()
        fun showStationsError()

        fun showTaggedAlbumsLoading()
        fun showTaggedAlbums(albums: List<Album>, morePages: Boolean)
        fun hideAlbumsSection()
        fun showAlbumsError()

        fun showTaggedArtistsLoading()
        fun showTaggedArtists(artists: List<Artist>, morePages: Boolean)
        fun hideArtistsSection()
        fun showArtistsError()

        fun showTaggedPlaylistsLoading()
        fun showTaggedPlaylists(playlists: List<Playlist>, morePages: Boolean)
        fun hidePlaylistsSection()
        fun showPlaylistsError()
    }

    interface RouterSurface {
        fun navigateToProductListWithTag(productType: ProductListType, tag: String?)

        fun navigateToTaggedStation(station: Station)
        fun navigateToTaggedArtist(artist: Artist)
        fun navigateToTaggedAlbum(album: Album)
        fun navigateToTaggedPlaylist(playlist: Playlist)
    }
}
