package com.truedigital.features.tuned.presentation.station.presenter

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.station.facade.TuningFacade
import com.truedigital.foundation.extension.parcelable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class TuningPresenter @Inject constructor(private val tuningFacade: TuningFacade) : Presenter {
    companion object {
        const val STATION_KEY = "station"
    }

    private lateinit var view: ViewSurface

    private var station: Station? = null
    private var currentFilter: Rating = Rating.LIKED

    private var votesObservable: Single<List<LikedTrack>>? = null
    private var votesSubscription: Disposable? = null
    private var deleteLikeObservable: Single<Vote>? = null
    private var deleteLikeSubscription: Disposable? = null

    fun onInject(view: ViewSurface) {
        this.view = view
    }

    //region Lifecycle

    override fun onStart(arguments: Bundle?) {
        arguments?.let {
            station = it.parcelable(STATION_KEY)
            station?.let { _station -> view.showStation(_station) }

            view.showProgress()
            votesObservable = getVotesObservable()
        }
    }

    override fun onPause() {
        votesSubscription?.dispose()
        deleteLikeSubscription?.dispose()
    }

    override fun onResume() {
        deleteLikeSubscription = getDeleteLikeSubscription()
        votesSubscription = getVotesSubscription()
    }

    //endregion

    fun onUpdateRating() {
        votesObservable = getVotesObservable()
        updateVotes()
    }

    fun onRemoveVote(track: LikedTrack) {
        // Don't let them delete more than one at a time
        val isCurrentlyRemovingVote = deleteLikeObservable != null
        if (!isCurrentlyRemovingVote) {
            deleteLikeObservable = tuningFacade.deleteVote(station, track.track).cacheOnMainThread()
            deleteLikeSubscription = getDeleteLikeSubscription()
        } else {
            view.showDeleteInProgressError()
        }
    }

    fun onVotesSelected(type: Rating) {
        currentFilter = type
        updateVotes()
    }

    private fun updateVotes() {
        deleteLikeSubscription?.dispose()
        votesSubscription?.dispose()
        votesSubscription = getVotesSubscription()
    }

    private fun getVotesObservable(): Single<List<LikedTrack>> =
        tuningFacade.getStationTrackVotes(station).cacheOnMainThread()

    private fun getDeleteLikeSubscription() = deleteLikeObservable?.tunedSubscribe(
        {
            deleteLikeObservable = null
            votesObservable = getVotesObservable()
            votesSubscription = getVotesSubscription()
        },
        {
            deleteLikeObservable = null
            view.showDeleteError()
        }
    )

    private fun getVotesSubscription(): Disposable? =
        votesObservable?.map { list ->
            list.filter { it.type == currentFilter }
        }?.tunedSubscribe(
            {
                if (it.isEmpty()) {
                    showLikeOrDislikeEmpty()
                } else {
                    view.showVotes(it)
                }
            },
            {
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    showLikeOrDislikeEmpty()
                } else {
                    showLikeOrDislikeError()
                }
            }
        )

    @VisibleForTesting
    fun showLikeOrDislikeEmpty() {
        if (currentFilter == Rating.LIKED) {
            view.showLikesEmpty()
        } else {
            view.showDislikesEmpty()
        }
    }

    @VisibleForTesting
    fun showLikeOrDislikeError() {
        if (currentFilter == Rating.LIKED) {
            view.showLikesError()
        } else {
            view.showDislikesError()
        }
    }

    @VisibleForTesting
    fun setPrivateData(currentFilter: Rating = Rating.DISLIKED) {
        this.currentFilter = currentFilter
    }

    @VisibleForTesting
    fun setObservable(
        votesObservable: Single<List<LikedTrack>>? = null,
        deleteLikeObservable: Single<Vote>? = null
    ) {
        this.votesObservable = votesObservable
        this.deleteLikeObservable = deleteLikeObservable
    }

    @VisibleForTesting
    fun setSubscription(
        votesSubscription: Disposable? = null,
        deleteLikeSubscription: Disposable? = null
    ) {
        this.votesSubscription = votesSubscription
        this.deleteLikeSubscription = deleteLikeSubscription
    }

    interface ViewSurface {
        fun showVotes(likedTracks: List<LikedTrack>)
        fun showLikesError()
        fun showLikesEmpty()
        fun showDislikesError()
        fun showDislikesEmpty()
        fun showProgress()
        fun showDeleteError()
        fun showDeleteInProgressError()
        fun showStation(station: Station)
    }
}
