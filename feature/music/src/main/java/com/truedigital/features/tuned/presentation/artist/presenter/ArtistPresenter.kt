package com.truedigital.features.tuned.presentation.artist.presenter

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacade
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_RESOURCE_NOT_FOUND
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.foundation.extension.parcelable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class ArtistPresenter @Inject constructor(
    private val artistFacade: ArtistFacade,
    private val config: Configuration
) : Presenter {
    companion object {
        const val ARTIST_KEY = "artist"
        const val ARTIST_ID_KEY = "artist_id"
        const val AUTO_PLAY_KEY = "auto_play"
        const val SORT_TYPE_NEW_RELEASE = "newrelease"

        private const val INITIAL_OFFSET = 1
        private const val DEFAULT_PAGE_SIZE = 10
        private const val MINIMUM_SIZE = 3
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var artistObservable: Single<Artist>? = null
    private var artistSubscription: Disposable? = null
    private var artistPlayCountObservable: Single<Int>? = null
    private var artistPlayCountSubscription: Disposable? = null
    private var popularSongsObservable: Single<List<Track>>? = null
    private var popularSongsSubscription: Disposable? = null
    private var latestSongsObservable: Single<List<Track>>? = null
    private var latestSongsSubscription: Disposable? = null
    private var artistSimilarStationObservable: Single<Station>? = null
    private var artistSimilarStationSubscription: Disposable? = null
    private var isFollowedObservable: Single<Boolean>? = null
    private var isFollowedSubscription: Disposable? = null
    private var toggleFollowedObservable: Single<Any>? = null
    private var toggleFollowedSubscription: Disposable? = null
    private var appearsInObservable: Single<List<Station>>? = null
    private var appearsInSubscription: Disposable? = null
    private var videoAppearsInObservable: Single<List<Track>>? = null
    private var videoAppearsInSubscription: Disposable? = null
    private var similarArtistsObservable: Single<List<Artist>>? = null
    private var similarArtistsSubscription: Disposable? = null
    private var albumsObservable: Single<List<Album>>? = null
    private var albumsSubscription: Disposable? = null
    private var albumsAppearsOnObservable: Single<List<Album>>? = null
    private var albumsAppearsOnSubscription: Disposable? = null
    private var clearArtistVotesObservable: Single<Any>? = null
    private var clearArtistVotesSubscription: Disposable? = null

    private var artist: Artist? = null
    private var station: Station? = null
    private var tracks: List<Track>? = null
    private var latestTracks: List<Track>? = null
    private var isFollowed = false
    private var playingTrackId: Int? = null
    private var autoPlay = false

    private var pageSize = DEFAULT_PAGE_SIZE
    private var albumOffset = INITIAL_OFFSET
    private var videoOffset = INITIAL_OFFSET
    private var albums = mutableListOf<Album>()
    private var videos = mutableListOf<Track>()

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region Lifecycle

    override fun onStart(arguments: Bundle?) {
        arguments?.let { bundle ->
            autoPlay = bundle.getBoolean(AUTO_PLAY_KEY)
            if (bundle.containsKey(ARTIST_KEY)) {
                artist = bundle.parcelable(ARTIST_KEY)
                artist?.let {
                    view.showArtist(it)

                    if (config.enableArtistCount) {
                        artistPlayCountObservable = getArtistPlayCountObservable(it.id)
                    }
                    popularSongsObservable = getPopularSongsObservable(it)
                    latestSongsObservable = getLatestSongsObservable(it)
                    isFollowedObservable = getIsFollowedObservable(it)
                    appearsInObservable = getAppearsInObservable(it)
                    videoAppearsInObservable = getVideoAppearsInObservable(it)
                    similarArtistsObservable = getSimilarArtistsObservable(it)
                    if (artistFacade.getAlbumNavigationAllowed()) {
                        albumsObservable = getAlbumsObservable(it)
                        albumsAppearsOnObservable = getAlbumsAppearsOnObservable(it)
                    }
                }
            } else if (bundle.containsKey(ARTIST_ID_KEY)) {
                artistObservable = getArtistObservable(bundle.getInt(ARTIST_ID_KEY))
            }
            if (!artistFacade.getHasArtistShuffleRight()) view.showNoArtistShuffleRight()

            if (!artistFacade.isArtistHintShown()) {
                view.showArtistHint()
            }
        }
    }

    override fun onResume() {
        artistSubscription = getArtistSubscription()
        artistPlayCountSubscription = getArtistPlayCountSubscription()
        popularSongsSubscription = getPopularSongsSubscription()
        latestSongsSubscription = getLatestSongsSubscription()
        artistSimilarStationSubscription = getArtistSimilarStationSubscription()
        isFollowedSubscription = getIsFollowedSubscription()
        toggleFollowedSubscription = getToggleFollowedSubscription()
        similarArtistsSubscription = getSimilarArtistSubscription()
        appearsInSubscription = getAppearsInSubscription()
        videoAppearsInSubscription = getVideoAppearsInSubscription()
        albumsSubscription = getAlbumsSubscription()
        albumsAppearsOnSubscription = getAlbumsAppearsOnSubscription()
        clearArtistVotesSubscription = getClearArtistVotesSubscription()
    }

    override fun onPause() {
        artistSubscription?.dispose()
        artistPlayCountSubscription?.dispose()
        popularSongsSubscription?.dispose()
        latestSongsSubscription?.dispose()
        artistSimilarStationSubscription?.dispose()
        isFollowedSubscription?.dispose()
        toggleFollowedSubscription?.dispose()
        similarArtistsSubscription?.dispose()
        appearsInSubscription?.dispose()
        videoAppearsInSubscription?.dispose()
        albumsSubscription?.dispose()
        albumsAppearsOnSubscription?.dispose()
        clearArtistVotesSubscription?.dispose()
    }
    // endregion

    // region Callbacks

    fun onPlayArtistMix() {
        station?.let { station ->
            view.playArtistMix(station)
            view.showPlayArtistMix()
        } ?: run {
            if (artistSimilarStationObservable == null) {
                artist?.let { artist ->
                    view.showLoadingArtistMix()
                    artistSimilarStationObservable = getArtistSimilarStationObservable(artist)
                    artistSimilarStationSubscription = getArtistSimilarStationSubscription()
                }
            }
        }
    }

    fun onPlayArtistShuffle() {
        if (artistFacade.getHasArtistShuffleRight()) {
            val playableTracks = tracks?.filter { it.allowStream } ?: return
            if (playableTracks.isNotEmpty()) {
                artist?.let { view.playArtistSongs(it, playableTracks, forceShuffle = true) }
            }
        } else {
            view.showUpgradeDialog()
        }
    }

    fun onPlayArtistTrendingTrack(position: Int) {
        if (artistFacade.getHasArtistShuffleRight()) {
            val playableTracks = tracks?.filter { it.allowStream } ?: return
            if (playableTracks.isNotEmpty()) {
                artist?.let {
                    view.playArtistSongs(
                        it,
                        playableTracks,
                        position,
                        forceSequential = true
                    )
                }
            }
        } else {
            view.showUpgradeDialog()
        }
    }

    fun onArtistLatestTrackSelected(track: Track) {
        if (artistFacade.getHasArtistShuffleRight()) {
            val playableTracks = latestTracks?.filter { it.allowStream } ?: return
            val position = playableTracks.indexOf(track)
            if (playableTracks.isNotEmpty() && position != -1) {
                artist?.let {
                    view.playArtistSongs(
                        it,
                        playableTracks,
                        position,
                        forceSequential = true
                    )
                }
            }
        } else {
            view.showUpgradeDialog()
        }
    }

    fun onStationSelected(station: Station) {
        router.navigateToStation(station)
    }

    fun onAlbumSelected(album: Album) {
        router.navigateToAlbum(album)
    }

    fun onArtistSelected(artist: Artist) {
        router.navigateToArtist(artist)
    }

    fun onVideoSelected(video: Track) {
        view.playVideo(video)
    }

    fun onImageSelected() {
        artist?.image?.let {
            view.showEnlargedImage(it)
        }
    }

    fun onTrendingSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_TRACKS, it)
        }
    }

    fun onLatestSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_LATEST, it)
        }
    }

    fun onVideoAppearsInSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_VIDEO, it)
        }
    }

    fun onAppearsInSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_STATION, it)
        }
    }

    fun onAlbumsSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_ALBUM, it)
        }
    }

    fun onAppearsOnAlbumsSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_APPEAR_ON, it)
        }
    }

    fun onSimilarSeeAllSelected() {
        artist?.let {
            router.navigateToProductList(ProductListType.ARTIST_SIMILAR, it)
        }
    }

    fun onRetryPopularSongs() {
        artist?.let {
            view.showPopularSongsLoading()
            popularSongsObservable = getPopularSongsObservable(it)
            popularSongsSubscription = getPopularSongsSubscription()
        }
    }

    fun onRetryLatestSongs() {
        artist?.let {
            view.showLatestSongsLoading()
            latestSongsObservable = getLatestSongsObservable(it)
            latestSongsSubscription = getLatestSongsSubscription()
        }
    }

    fun onRetryStationsAppearsIn() {
        artist?.let {
            view.showStationsAppearsInLoading()
            appearsInObservable = getAppearsInObservable(it)
            appearsInSubscription = getAppearsInSubscription()
        }
    }

    fun onRetryVideosAppearsIn() {
        artist?.let {
            view.showVideoAppearsInLoading()
            videoOffset = INITIAL_OFFSET

            videos.clear()
            videoAppearsInObservable = getVideoAppearsInObservable(it)
            videoAppearsInSubscription = getVideoAppearsInSubscription()
        }
    }

    fun onRetrySimilarArtists() {
        artist?.let {
            view.showSimilarArtistsLoading()
            similarArtistsObservable = getSimilarArtistsObservable(it)
            similarArtistsSubscription = getSimilarArtistSubscription()
        }
    }

    fun onRetryAlbums() {
        artist?.let {
            view.showAlbumsLoading()
            albums.clear()
            albumOffset = INITIAL_OFFSET
            albumsObservable = getAlbumsObservable(it)
            albumsSubscription = getAlbumsSubscription()
        }
    }

    fun onRetryAlbumsAppearsOn() {
        artist?.let {
            view.showAlbumsAppearsOnLoading()
            albumsAppearsOnObservable = getAlbumsAppearsOnObservable(it)
            albumsAppearsOnSubscription = getAlbumsAppearsOnSubscription()
        }
    }

    fun onLoadMoreAlbums() {
        artist?.let {
            albumOffset += pageSize
            albumsObservable = getAlbumsObservable(it)
            albumsSubscription = getAlbumsSubscription()
        }
    }

    fun onLoadMoreVideos() {
        artist?.let {
            videoOffset += pageSize
            videoAppearsInObservable = getVideoAppearsInObservable(it)
            videoAppearsInSubscription = getVideoAppearsInSubscription()
        }
    }

    fun onShowMoreOptions() {
        artist?.let { view.showMoreOptions(it) }
    }

    fun onMoreOptionSelected(selection: PickerOptions): Boolean {
        var actionHandled = true
        when (selection) {
            PickerOptions.ADD_TO_COLLECTION -> onToggleFollow()
            PickerOptions.CLEAR_VOTE -> onClearArtistVotes()
            PickerOptions.REMOVE_FROM_COLLECTION -> onToggleFollow()
            else -> {
                actionHandled = false
            }
        }
        return actionHandled
    }

    fun onMoreTrackOptionSelected(selection: PickerOptions) {
        when (selection) {
            PickerOptions.ADD_TO_QUEUE -> view.showAddToQueueToast()
            else -> {
                // Do nothing
            }
        }
    }

    fun onToggleFollow() {
        artist?.let {
            if (toggleFollowedObservable == null) {
                toggleFollowedObservable = getToggleFollowedObservable(it)
                toggleFollowedSubscription = getToggleFollowedSubscription()
                isFollowed = !isFollowed
                view.showFollowed(isFollowed)
            }
        }
    }

    fun onFavouriteSelect(isFavourited: Boolean, isSuccess: Boolean) {
        if (isSuccess) {
            if (isFavourited) {
                view.showFollowSuccess()
            } else {
                view.showUnFollowSuccess()
            }
        } else {
            if (isFavourited) {
                view.showFollowError()
            } else {
                view.showUnFollowError()
            }
        }
    }

    private fun onClearArtistVotes() {
        artist?.let {
            if (clearArtistVotesObservable == null) {
                clearArtistVotesObservable = getClearArtistVotesObservable(it)
                clearArtistVotesSubscription = getClearArtistVotesSubscription()
            }
        }
    }

    fun onCloseHint() {
        artistFacade.setArtistHintShown()
    }

    // endregion

    fun onUpdatePlaybackState(
        sourceId: Int?,
        trackId: Int?,
        @PlaybackStateCompat.State state: Int?
    ) {
        Timber.d("$sourceId|$state")
        if (playingTrackId != trackId) {
            view.showCurrentPlayingTrack(trackId)
            playingTrackId = trackId
        }
    }

    // region rx

    private fun getArtistObservable(artistId: Int) =
        artistFacade.loadArtist(artistId).cacheOnMainThread()

    private fun getArtistSubscription() =
        artistObservable?.tunedSubscribe(
            {
                artistObservable = null
                artist = it

                if (config.enableArtistCount) {
                    artistPlayCountObservable = getArtistPlayCountObservable(it.id)
                    artistPlayCountSubscription = getArtistPlayCountSubscription()
                }
                popularSongsObservable = getPopularSongsObservable(it)
                popularSongsSubscription = getPopularSongsSubscription()
                latestSongsObservable = getLatestSongsObservable(it)
                latestSongsSubscription = getLatestSongsSubscription()
                isFollowedObservable = getIsFollowedObservable(it)
                isFollowedSubscription = getIsFollowedSubscription()
                appearsInObservable = getAppearsInObservable(it)
                appearsInSubscription = getAppearsInSubscription()
                videoAppearsInObservable = getVideoAppearsInObservable(it)
                videoAppearsInSubscription = getVideoAppearsInSubscription()
                similarArtistsObservable = getSimilarArtistsObservable(it)
                similarArtistsSubscription = getSimilarArtistSubscription()
                if (artistFacade.getAlbumNavigationAllowed()) {
                    albumsObservable = getAlbumsObservable(it)
                    albumsSubscription = getAlbumsSubscription()
                    albumsAppearsOnObservable = getAlbumsAppearsOnObservable(it)
                    albumsAppearsOnSubscription = getAlbumsAppearsOnSubscription()
                }
                view.showArtist(it)
            },
            {
                artistObservable = null
                view.showLoadArtistError()
            }
        )

    private fun getArtistPlayCountObservable(artistId: Int) =
        artistFacade.loadArtistPlayCount(artistId).cacheOnMainThread()

    private fun getArtistPlayCountSubscription() =
        artistPlayCountObservable?.tunedSubscribe(
            {
                artistPlayCountObservable = null
                view.showArtistPlayCount(it)
            },
            {
                artistPlayCountObservable = null
                view.hideArtistPlayCount()
            }
        )

    private fun getPopularSongsObservable(artist: Artist) =
        artistFacade.loadPopularSongs(artist.id).cacheOnMainThread()

    private fun getPopularSongsSubscription() =
        popularSongsObservable?.tunedSubscribe(
            {
                popularSongsObservable = null
                tracks = it
                view.showPopularSongs(it)
                if (autoPlay) onPlayArtistShuffle()
            },
            {
                popularSongsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hidePopularSongs()
                } else {
                    view.showPopularSongsError()
                }
            }
        )

    private fun getLatestSongsObservable(artist: Artist) =
        artistFacade.loadLatestSongs(artist.id).cacheOnMainThread()

    private fun getLatestSongsSubscription() =
        latestSongsObservable?.tunedSubscribe(
            {
                latestSongsObservable = null
                latestTracks = it
                view.showLatestSongs(it)
            },
            {
                latestSongsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideLatestSongs()
                } else {
                    view.showLatestSongsError()
                }
            }
        )

    private fun getArtistSimilarStationObservable(artist: Artist) =
        artistFacade.loadArtistAndSimilarStation(artist.id).cacheOnMainThread()

    private fun getArtistSimilarStationSubscription() =
        artistSimilarStationObservable?.tunedSubscribe(
            {
                artistSimilarStationObservable = null
                station = it
                view.playArtistMix(it)
                view.showPlayArtistMix()
            },
            {
                artistSimilarStationObservable = null
                view.showArtistMixEnabled()
                view.showArtistSimilarStationError()
            }
        )

    private fun getIsFollowedObservable(artist: Artist): Single<Boolean> =
        artistFacade.loadFollowed(artist.id).cacheOnMainThread()

    private fun getIsFollowedSubscription(): Disposable? =
        isFollowedObservable?.tunedSubscribe(
            {
                isFollowedObservable = null
                isFollowed = it
                view.showFollowed(it)
            },
            {
                isFollowedObservable = null
                view.showFollowed(false)
            }
        )

    private fun getToggleFollowedObservable(artist: Artist) =
        artistFacade.toggleFavourite(artist.id).cacheOnMainThread()

    private fun getToggleFollowedSubscription(): Disposable? {
        val wasFollowed = isFollowed
        return toggleFollowedObservable?.tunedSubscribe(
            {
                toggleFollowedObservable = null
                if (isFollowed) {
                    view.showFollowSuccess()
                } else {
                    view.showUnFollowSuccess()
                }
            },
            {
                if (isFollowed) {
                    view.showFollowError()
                } else {
                    view.showUnFollowError()
                }
                toggleFollowedObservable = null
                isFollowed = wasFollowed
                view.showFollowed(isFollowed)
            }
        )
    }

    private fun getAppearsInObservable(artist: Artist) =
        artistFacade.loadStationsAppearsIn(artist.id).cacheOnMainThread()

    private fun getAppearsInSubscription() =
        appearsInObservable?.tunedSubscribe(
            {
                appearsInObservable = null
                view.showStationsAppearsIn(it)
            },
            {
                appearsInObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideStationsAppearsIn()
                } else {
                    view.showStationsAppearsInError()
                }
            }
        )

    private fun getVideoAppearsInObservable(artist: Artist) =
        artistFacade.getVideoAppearsIn(artist.id, videoOffset, pageSize, SORT_TYPE_NEW_RELEASE)
            .cacheOnMainThread()

    private fun getVideoAppearsInSubscription() =
        videoAppearsInObservable?.tunedSubscribe(
            {
                videoAppearsInObservable = null
                videos.addAll(it)
                view.showVideoAppearsIn(videos, morePages = it.size >= pageSize)
            },
            {
                videoAppearsInObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideVideoAppearsIn()
                } else {
                    view.showVideoAppearsInError()
                }
            }
        )

    private fun getSimilarArtistsObservable(artist: Artist) =
        artistFacade.loadSimilarArtists(artist.id).cacheOnMainThread()

    private fun getSimilarArtistSubscription() =
        similarArtistsObservable?.tunedSubscribe(
            {
                similarArtistsObservable = null
                view.showSimilarArtists(it)
                if (it.size > MINIMUM_SIZE && !artistFacade.getIsDMCAEnabled()) {
                    view.showArtistMixEnabled()
                } else {
                    view.showArtistMixDisabled()
                }
            },
            {
                similarArtistsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideSimilarArtists()
                } else {
                    view.showSimilarArtistsError()
                }
                view.showArtistMixDisabled()
            }
        )

    private fun getAlbumsObservable(artist: Artist) =
        artistFacade.loadArtistAlbums(artist.id, albumOffset, pageSize, SORT_TYPE_NEW_RELEASE)
            .cacheOnMainThread()

    private fun getAlbumsSubscription() =
        albumsObservable?.tunedSubscribe(
            {
                albumsObservable = null
                albums.addAll(it)
                view.showAlbums(albums, morePages = it.size >= pageSize)
            },
            {
                albumsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    if (albums.isEmpty()) {
                        view.hideAlbums()
                    } else {
                        view.showAlbums(albums, false)
                    }
                } else {
                    view.showAlbumsError()
                }
            }
        )

    private fun getAlbumsAppearsOnObservable(artist: Artist) =
        artistFacade.loadArtistAppearsOn(artist.id, SORT_TYPE_NEW_RELEASE).cacheOnMainThread()

    private fun getAlbumsAppearsOnSubscription() =
        albumsAppearsOnObservable?.tunedSubscribe(
            {
                albumsAppearsOnObservable = null
                view.showAlbumsAppearsOn(it)
            },
            {
                albumsAppearsOnObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideAlbumsAppearsOn()
                } else {
                    view.showAlbumsAppearsOnError()
                }
            }
        )

    private fun getClearArtistVotesObservable(artist: Artist) =
        artistFacade.clearArtistVotes(artist.id).cacheOnMainThread()

    private fun getClearArtistVotesSubscription() =
        clearArtistVotesObservable?.tunedSubscribe(
            {
                clearArtistVotesObservable = null
                view.showArtistVotesCleared()
            },
            {
                clearArtistVotesObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showArtistVotesCleared()
                } else {
                    view.showClearArtistVotesError()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        station: Station? = null,
        artist: Artist? = null,
        tracks: List<Track>? = null,
        latestTracks: List<Track>? = null,
        playingTrackId: Int? = null,
        isFollowed: Boolean = false
    ) {
        this.station = station
        this.artist = artist
        this.tracks = tracks
        this.latestTracks = latestTracks
        this.playingTrackId = playingTrackId
        this.isFollowed = isFollowed
    }

    @VisibleForTesting
    fun setPrivateDataSecondSection(
        albums: MutableList<Album> = mutableListOf<Album>()
    ) {
        this.albums = albums
    }

    @VisibleForTesting
    fun setSubscriptionFirstSection(
        artistSubscription: Disposable? = null,
        artistPlayCountSubscription: Disposable? = null,
        popularSongsSubscription: Disposable? = null,
        latestSongsSubscription: Disposable? = null,
        artistSimilarStationSubscription: Disposable? = null,
        isFollowedSubscription: Disposable? = null,
        toggleFollowedSubscription: Disposable? = null
    ) {
        this.artistSubscription = artistSubscription
        this.artistPlayCountSubscription = artistPlayCountSubscription
        this.popularSongsSubscription = popularSongsSubscription
        this.latestSongsSubscription = latestSongsSubscription
        this.artistSimilarStationSubscription = artistSimilarStationSubscription
        this.isFollowedSubscription = isFollowedSubscription
        this.toggleFollowedSubscription = toggleFollowedSubscription
    }

    @VisibleForTesting
    fun setSubscriptionSecondSection(
        similarArtistsSubscription: Disposable? = null,
        appearsInSubscription: Disposable? = null,
        videoAppearsInSubscription: Disposable? = null,
        albumsSubscription: Disposable? = null,
        albumsAppearsOnSubscription: Disposable? = null,
        clearArtistVotesSubscription: Disposable? = null
    ) {
        this.similarArtistsSubscription = similarArtistsSubscription
        this.appearsInSubscription = appearsInSubscription
        this.videoAppearsInSubscription = videoAppearsInSubscription
        this.albumsSubscription = albumsSubscription
        this.albumsAppearsOnSubscription = albumsAppearsOnSubscription
        this.clearArtistVotesSubscription = clearArtistVotesSubscription
    }

    @VisibleForTesting
    fun setObservableFirstSection(
        artistObservable: Single<Artist>? = null,
        artistPlayCountObservable: Single<Int>? = null,
        popularSongsObservable: Single<List<Track>>? = null,
        latestSongsObservable: Single<List<Track>>? = null,
        artistSimilarStationObservable: Single<Station>? = null,
        isFollowedObservable: Single<Boolean>? = null,
        toggleFollowedObservable: Single<Any>? = null
    ) {
        this.artistObservable = artistObservable
        this.artistPlayCountObservable = artistPlayCountObservable
        this.popularSongsObservable = popularSongsObservable
        this.latestSongsObservable = latestSongsObservable
        this.artistSimilarStationObservable = artistSimilarStationObservable
        this.isFollowedObservable = isFollowedObservable
        this.toggleFollowedObservable = toggleFollowedObservable
    }

    @VisibleForTesting
    fun setObservableSecondSection(
        similarArtistsObservable: Single<List<Artist>>? = null,
        appearsInObservable: Single<List<Station>>? = null,
        videoAppearsInObservable: Single<List<Track>>? = null,
        albumsObservable: Single<List<Album>>? = null,
        albumsAppearsOnObservable: Single<List<Album>>? = null,
        clearArtistVotesObservable: Single<Any>? = null
    ) {
        this.similarArtistsObservable = similarArtistsObservable
        this.appearsInObservable = appearsInObservable
        this.videoAppearsInObservable = videoAppearsInObservable
        this.albumsObservable = albumsObservable
        this.albumsAppearsOnObservable = albumsAppearsOnObservable
        this.clearArtistVotesObservable = clearArtistVotesObservable
    }

    interface ViewSurface {
        fun showArtist(artist: Artist)
        fun showLoadArtistError()

        fun showPopularSongs(tracks: List<Track>)
        fun showPopularSongsLoading()
        fun showPopularSongsError()
        fun hidePopularSongs()

        fun showLatestSongs(tracks: List<Track>)
        fun showLatestSongsLoading()
        fun showLatestSongsError()
        fun hideLatestSongs()

        fun showVideoAppearsIn(videos: List<Track>, morePages: Boolean)
        fun showVideoAppearsInLoading()
        fun showVideoAppearsInError()
        fun hideVideoAppearsIn()

        fun showStationsAppearsIn(stations: List<Station>)
        fun showStationsAppearsInLoading()
        fun showStationsAppearsInError()
        fun hideStationsAppearsIn()

        fun showAlbums(albums: List<Album>, morePages: Boolean)
        fun showAlbumsLoading()
        fun showAlbumsError()
        fun hideAlbums()

        fun showAlbumsAppearsOn(albums: List<Album>)
        fun showAlbumsAppearsOnLoading()
        fun showAlbumsAppearsOnError()
        fun hideAlbumsAppearsOn()

        fun showSimilarArtists(artists: List<Artist>)
        fun showSimilarArtistsLoading()
        fun showSimilarArtistsError()
        fun hideSimilarArtists()

        fun showArtistPlayCount(count: Int)
        fun hideArtistPlayCount()

        // Player actions
        fun playArtistSongs(
            artist: Artist,
            tracks: List<Track>,
            startIndex: Int? = null,
            forceShuffle: Boolean = false,
            forceSequential: Boolean = false
        )

        fun showNoArtistShuffleRight()
        fun showCurrentPlayingTrack(trackId: Int?)

        fun playArtistMix(station: Station)
        fun showPlayArtistMix()
        fun showLoadingArtistMix()
        fun showArtistMixEnabled()

        fun showFollowSuccess()
        fun showFollowed(followed: Boolean)
        fun showFollowError()
        fun showUnFollowSuccess()
        fun showUnFollowError()
        fun showUpgradeDialog()
        fun showArtistMixDisabled()
        fun showArtistSimilarStationError()

        fun showMoreOptions(artist: Artist)
        fun showArtistVotesCleared()
        fun showClearArtistVotesError()

        fun playVideo(video: Track)

        fun showArtistHint()

        fun showShareLoading()
        fun showEnlargedImage(image: String)
        fun showAddToQueueToast()
    }

    interface RouterSurface {
        fun navigateToStation(station: Station)
        fun navigateToArtist(artist: Artist)
        fun navigateToAlbum(album: Album)
        fun navigateToProductList(type: ProductListType, artist: Artist)
    }
}
