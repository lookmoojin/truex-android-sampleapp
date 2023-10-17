package com.truedigital.features.tuned.presentation.station.presenter

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.station.facade.StationFacade
import com.truedigital.foundation.extension.parcelable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class StationPresenter @Inject constructor(val stationFacade: StationFacade) : Presenter {
    companion object {
        const val STATION_KEY = "station"
        const val STATION_ID_KEY = "station_id"
        const val TRACK_HASH_KEY = "track_hash"
        const val AUTO_PLAY_KEY = "play_station"
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var stationObservable: Single<Station>? = null
    private var stationSubscription: Disposable? = null
    private var isFavouritedObservable: Single<Boolean>? = null
    private var isFavouritedSubscription: Disposable? = null
    private var toggleFavouriteObservable: Single<Any>? = null
    private var toggleFavouriteSubscription: Disposable? = null
    private var clearVotesObservable: Single<Any>? = null
    private var clearVotesSubscription: Disposable? = null

    private var station: Station? = null
    private var trackHash: String? = null
    private var autoPlay: Boolean = false
    private var isFavourited = false

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    override fun onStart(arguments: Bundle?) {
        arguments?.let { bundle ->
            trackHash = bundle.getString(TRACK_HASH_KEY)
            autoPlay = bundle.getBoolean(AUTO_PLAY_KEY)

            if (bundle.containsKey(STATION_KEY)) {
                station = bundle.parcelable(STATION_KEY)
                station?.let {
                    view.initStation(it)
                    view.showOnlineStation(it, trackHash, autoPlay)

                    isFavouritedObservable = getIsFavouritedObservable(it)
                }
            }

            if (bundle.containsKey(STATION_ID_KEY)) {
                stationObservable = getStationObservable(bundle.getInt(STATION_ID_KEY))
            }
        }
    }

    override fun onResume() {
        stationSubscription = getStationSubscription()
        isFavouritedSubscription = getIsFavouritedSubscription()
        toggleFavouriteSubscription = getToggleFavouriteSubscription()
        clearVotesSubscription = getClearVotesSubscription()
    }

    override fun onPause() {
        stationSubscription?.dispose()
        isFavouritedSubscription?.dispose()
        toggleFavouriteSubscription?.dispose()
        clearVotesSubscription?.dispose()
    }

    fun onMoreOptionSelected(selection: PickerOptions): Boolean {
        var actionHandled = true
        when (selection) {
            PickerOptions.ADD_TO_COLLECTION -> onToggleFavourite()
            PickerOptions.REMOVE_FROM_COLLECTION -> onToggleFavourite()
            PickerOptions.CLEAR_VOTE -> onClearVote()
            else -> {
                actionHandled = false
            }
        }
        return actionHandled
    }

    fun onToggleFavourite() {
        station?.let {
            if (toggleFavouriteObservable == null) {
                toggleFavouriteObservable = getToggleFavouriteObservable(it)
                toggleFavouriteSubscription = getToggleFavouriteSubscription()
                isFavourited = !isFavourited
                view.showFavourited(isFavourited)
            }
        }
    }

    fun onImageSelected() {
        view.showEnlargedImage(station?.coverImage)
    }

    private fun onClearVote() {
        station?.let {
            if (clearVotesObservable == null) {
                clearVotesObservable = getClearVotesObservable(it.id)
                clearVotesSubscription = getClearVotesSubscription()
            }
        }
    }

    fun onShowMoreOptions() {
        station?.let { view.showMoreOptions(it) }
    }

    private fun getStationObservable(id: Int) = stationFacade.loadStation(id).cacheOnMainThread()

    private fun getStationSubscription(): Disposable? =
        stationObservable
            ?.doOnSubscribe { view.showLoading() }
            ?.tunedSubscribe(
                {
                    stationObservable = null
                    station = it
                    view.initStation(it)
                    view.showOnlineStation(it, trackHash, autoPlay)

                    isFavouritedObservable = getIsFavouritedObservable(it)
                    isFavouritedSubscription = getIsFavouritedSubscription()
                },
                {
                    stationObservable = null
                    view.showLoadStationError()
                }
            )

    private fun getIsFavouritedObservable(station: Station) =
        stationFacade.loadFavourited(station).cacheOnMainThread()

    private fun getIsFavouritedSubscription(): Disposable? =
        isFavouritedObservable
            ?.tunedSubscribe(
                {
                    isFavouritedObservable = null
                    isFavourited = it
                    view.showFavourited(it)
                },
                {
                    isFavouritedObservable = null
                    view.showFavourited(false)
                }
            )

    private fun getToggleFavouriteObservable(station: Station) =
        stationFacade.toggleFavourite(station).cacheOnMainThread()

    private fun getToggleFavouriteSubscription(): Disposable? {
        val wasFavourited = isFavourited
        return toggleFavouriteObservable
            ?.tunedSubscribe(
                {
                    toggleFavouriteObservable = null
                    if (isFavourited) {
                        view.showFavouritedToast()
                    }
                },
                {
                    toggleFavouriteObservable = null
                    isFavourited = wasFavourited
                    view.showFavourited(isFavourited)
                    view.showFavouritedError()
                }
            )
    }

    private fun getClearVotesObservable(id: Int) = stationFacade.clearVotes(id).cacheOnMainThread()

    private fun getClearVotesSubscription() =
        clearVotesObservable?.tunedSubscribe(
            {
                clearVotesObservable = null
                view.showVotesCleared()
            },
            {
                clearVotesObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showClearVotesEmpty()
                } else {
                    view.showClearVotesError()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        station: Station? = null,
        isFavourited: Boolean = false
    ) {
        this.station = station
        this.isFavourited = isFavourited
    }

    @VisibleForTesting
    fun setSubscription(
        stationSubscription: Disposable? = null,
        isFavouritedSubscription: Disposable? = null,
        toggleFavouriteSubscription: Disposable? = null,
        clearVotesSubscription: Disposable? = null
    ) {
        this.stationSubscription = stationSubscription
        this.isFavouritedSubscription = isFavouritedSubscription
        this.toggleFavouriteSubscription = toggleFavouriteSubscription
        this.clearVotesSubscription = clearVotesSubscription
    }

    @VisibleForTesting
    fun setObservable(
        toggleFavouriteObservable: Single<Any>? = null,
        clearVotesObservable: Single<Any>? = null,
        stationObservable: Single<Station>? = null,
        isFavouritedObservable: Single<Boolean>? = null
    ) {
        this.toggleFavouriteObservable = toggleFavouriteObservable
        this.clearVotesObservable = clearVotesObservable
        this.stationObservable = stationObservable
        this.isFavouritedObservable = isFavouritedObservable
    }

    interface ViewSurface {
        fun initStation(station: Station)
        fun showOnlineStation(
            station: Station,
            trackHash: String? = null,
            autoPlay: Boolean = false
        )

        fun showLoading()
        fun showLoadStationError()
        fun showFavourited(favourited: Boolean)
        fun showFavouritedError()
        fun showFavouritedToast()
        fun showMoreOptions(station: Station)
        fun showVotesCleared()
        fun showClearVotesError()
        fun showClearVotesEmpty()
        fun showEnlargedImage(images: List<LocalisedString>?)
    }

    interface RouterSurface {
        fun shareStation(station: Station, link: String)
    }
}
