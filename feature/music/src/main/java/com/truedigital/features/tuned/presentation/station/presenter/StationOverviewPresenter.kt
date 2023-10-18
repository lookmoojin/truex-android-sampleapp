package com.truedigital.features.tuned.presentation.station.presenter

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_RESOURCE_NOT_FOUND
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.station.facade.StationOverviewFacade
import com.truedigital.foundation.extension.parcelable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class StationOverviewPresenter @Inject constructor(val overviewFacade: StationOverviewFacade) :
    Presenter {
    companion object {
        const val STATION_KEY = "station"
        const val TRACK_HASH_KEY = "track_hash"
        const val AUTO_PLAY_KEY = "auto_play"
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var station: Station? = null
    private var trackHash: String? = null

    private var featuredArtistsObservable: Single<List<Artist>>? = null
    private var featuredArtistsSubscription: Disposable? = null
    private var similarStationsObservable: Single<List<Station>>? = null
    private var similarStationsSubscription: Disposable? = null

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region Lifecycle

    override fun onStart(arguments: Bundle?) {
        arguments?.let {
            station = it.parcelable(STATION_KEY)
            trackHash = it.getString(TRACK_HASH_KEY)
            val isAutoPlay = it.getBoolean(AUTO_PLAY_KEY)

            station?.let { _station ->
                view.initStation(_station)
                view.showOnlineStation(_station)
                featuredArtistsObservable = getFeaturedArtistsObservable()
                similarStationsObservable = getSimilarStationsObservable()

                if (isAutoPlay)
                    view.playStation(_station, trackHash)

                if (_station.type == Station.StationType.SINGLE_ARTIST &&
                    !overviewFacade.getHasArtistShuffleRight()
                ) {
                    view.showNoArtistShuffleRights()
                }
            }
        }
    }

    override fun onResume() {
        similarStationsSubscription = getSimilarStationsSubscription()
        featuredArtistsSubscription = getFeaturedArtistsSubscription()
    }

    override fun onPause() {
        featuredArtistsSubscription?.dispose()
        similarStationsSubscription?.dispose()
    }

    // endregion

    // region Callbacks

    fun onPlayStation() {
        if (station?.type == Station.StationType.SINGLE_ARTIST) {
            if (overviewFacade.getHasArtistShuffleRight()) {
                playStation()
            } else {
                view.showUpgradeDialog()
            }
        } else {
            playStation()
        }
    }

    fun onRetryFeaturedArtists() {
        view.showFeaturedArtistsLoading()
        featuredArtistsSubscription?.dispose()
        featuredArtistsObservable = getFeaturedArtistsObservable()
        featuredArtistsSubscription = getFeaturedArtistsSubscription()
    }

    fun onRetrySimilarStations() {
        view.showSimilarStationsLoading()
        similarStationsSubscription?.dispose()
        similarStationsObservable = getSimilarStationsObservable()
        similarStationsSubscription = getSimilarStationsSubscription()
    }

    fun onFeaturingSeeAllSelected() {
        station?.let {
            router.navigateToProductList(ProductListType.STATION_FEATURE_ARTIST, it.id)
        }
    }

    fun onSimilarSeeAllSelected() {
        station?.let {
            router.navigateToProductList(ProductListType.STATION_SIMILAR, it.id)
        }
    }

    fun onArtistSelected(artist: Artist) = router.navigateToArtist(artist)

    fun onStationSelected(station: Station) = router.navigateToStation(station)

    fun onStationBannerSelected() {
        station?.let {
            if (!it.bannerURL.isNullOrEmpty()) {
                router.navigateToUrl(Uri.parse(it.bannerURL))
            }
        }
    }

    fun onUpdatePlaybackState(stationId: Int?, @PlaybackStateCompat.State state: Int?) {
        val isBuffering = station?.id == stationId && state == PlaybackStateCompat.STATE_BUFFERING
        when {
            isBuffering -> view.showPlayButtonLoading()
            else -> view.showPlayStation()
        }
    }

    // endregion

    private fun playStation() {
        view.showPlayButtonLoading()
        station?.let { view.playStation(it, trackHash) }
    }

    // region rx

    private fun getFeaturedArtistsObservable() = overviewFacade.loadFeaturedArtists(station)
        .cacheOnMainThread()
        .doOnSubscribe { view.showFeaturedArtistsLoading() }

    private fun getFeaturedArtistsSubscription(): Disposable? = featuredArtistsObservable
        ?.tunedSubscribe(
            {
                featuredArtistsObservable = null
                view.showFeaturedArtists(it)
            },
            {
                featuredArtistsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideFeaturedArtists()
                } else {
                    view.showFeaturedArtistsError()
                }
            }
        )

    private fun getSimilarStationsObservable() = overviewFacade.loadSimilarStations(station)
        .cacheOnMainThread()
        .doOnSubscribe { view.showSimilarStationsLoading() }

    private fun getSimilarStationsSubscription(): Disposable? = similarStationsObservable
        ?.tunedSubscribe(
            {
                similarStationsObservable = null
                view.showSimilarStations(it)
            },
            {
                similarStationsObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideSimilarStations()
                } else {
                    view.showSimilarStationsError()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        station: Station? = null,
        featuredArtistsSubscription: Disposable? = null,
        similarStationsSubscription: Disposable? = null
    ) {
        this.station = station
        this.featuredArtistsSubscription = featuredArtistsSubscription
        this.similarStationsSubscription = similarStationsSubscription
    }

    @VisibleForTesting
    fun setObservable(
        featuredArtistsObservable: Single<List<Artist>>? = null,
        similarStationsObservable: Single<List<Station>>? = null
    ) {
        this.featuredArtistsObservable = featuredArtistsObservable
        this.similarStationsObservable = similarStationsObservable
    }

    interface ViewSurface {
        fun initStation(station: Station)
        fun showOnlineStation(station: Station)
        fun playStation(station: Station, trackHash: String? = null)
        fun showFeaturedArtists(artists: List<Artist>)
        fun showFeaturedArtistsLoading()
        fun showFeaturedArtistsError()
        fun hideFeaturedArtists()
        fun showSimilarStations(stations: List<Station>)
        fun showSimilarStationsLoading()
        fun showSimilarStationsError()
        fun hideSimilarStations()
        fun showPlayStation()
        fun showPlayButtonLoading()
        fun showUpgradeDialog()
        fun showNoArtistShuffleRights()
    }

    interface RouterSurface {
        fun navigateToArtist(artist: Artist)
        fun navigateToStation(station: Station)
        fun navigateToUrl(url: Uri)
        fun navigateToProductList(type: ProductListType, stationId: Int)
    }
}
