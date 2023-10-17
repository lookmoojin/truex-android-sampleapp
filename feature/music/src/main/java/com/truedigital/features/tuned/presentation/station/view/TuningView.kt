package com.truedigital.features.tuned.presentation.station.view

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.mediaId
import com.truedigital.features.tuned.common.extensions.sourceId
import com.truedigital.features.tuned.common.extensions.startServiceDefault
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.databinding.ViewTuningBinding
import com.truedigital.features.tuned.presentation.components.LifecycleComponentView
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.station.presenter.TuningPresenter
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.visible
import timber.log.Timber
import javax.inject.Inject

class TuningView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LifecycleComponentView(context, attrs),
    TuningPresenter.ViewSurface {
    @Inject
    lateinit var presenter: TuningPresenter

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    private val binding: ViewTuningBinding by lazy {
        ViewTuningBinding.inflate(LayoutInflater.from(this.context), this, true)
    }

    private var mediaCallback: MediaControllerCompat.Callback? = null
    private var stationId: Int = -1
    private var lastTrackId: Int = -1
    private var lastRating: RatingCompat? = null

    //region Lifecycle
    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        presenter.onInject(this)
        with(binding) {
            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.separator)
                ?.let { dividerItemDecoration.setDrawable(it) }

            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = VotesAdapter { likedTrack ->
                likedTrack.track?.let {
                    if (it.id == lastTrackId) {
                        context.startServiceDefault {
                            action = MusicPlayerController.ACTION_REMOVE_RATING
                        }
                    } else {
                        presenter.onRemoveVote(likedTrack)
                    }
                }
            }
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.addItemDecoration(dividerItemDecoration)

            segmentLikes.setOnCheckedChangeListener { _, checkedId ->
                layoutEmpty.gone()
                if (checkedId == R.id.buttonLikes) {
                    presenter.onVotesSelected(Rating.LIKED)
                } else if (checkedId == R.id.buttonDislikes) {
                    presenter.onVotesSelected(Rating.DISLIKED)
                }
            }

            mediaCallback = object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    Timber.d("$state")
                }

                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                    metadata?.let {
                        val mediaId = it.mediaId
                        if (!mediaId.isNullOrEmpty()) {
                            val stationId = it.sourceId
                            val trackId = it.trackId

                            if (this@TuningView.stationId == stationId) {
                                if (lastTrackId == -1 || lastTrackId != trackId) {
                                    lastTrackId = trackId ?: 0
                                    lastRating =
                                        it.getRating(MediaMetadataCompat.METADATA_KEY_USER_RATING)
                                } else {
                                    val currentRating =
                                        it.getRating(MediaMetadataCompat.METADATA_KEY_USER_RATING)
                                    if (currentRating != lastRating) {
                                        presenter.onUpdateRating()
                                    }
                                    lastRating = currentRating
                                }
                            }
                        }
                    }
                }
            }

            mediaCallback?.let { mediaCallback ->
                mediaSession.controller?.registerCallback(mediaCallback)
            }

            lifecycleComponents.add(PresenterComponent(presenter))
        }
    }

    override fun onStop() {
        super.onStop()
        mediaCallback?.let {
            mediaSession.controller?.unregisterCallback(it)
        }
    }

    //endregion

    fun onClearVotes() {
        presenter.onUpdateRating()
    }

    //region ViewSurface

    override fun showStation(station: Station) {
        stationId = station.id
    }

    override fun showVotes(likedTracks: List<LikedTrack>) {
        with(binding) {
            progressBar.gone()
            layoutEmpty.gone()
            topSeparator.visible()
            recyclerView.visible()
            (recyclerView.adapter as VotesAdapter).items = likedTracks
            root.scrollTo(0, 0)
        }
    }

    override fun showLikesError() {
        with(binding) {
            progressBar.gone()
            recyclerView.invisible()
        }
        context.toast(R.string.error_loading_votes)
    }

    override fun showLikesEmpty() {
        with(binding) {
            progressBar.gone()
            textViewNoVoteTitle.text = context.getText(R.string.no_likes_title)
            textViewNoVoteDescription.text = context.getText(R.string.no_likes_description)
            imageViewThumb.setImageResource(R.drawable.music_ic_thumb_up_hollow)
            layoutEmpty.visible()
            recyclerView.invisible()
        }
    }

    override fun showDislikesError() {
        with(binding) {
            progressBar.gone()
            recyclerView.invisible()
        }
        context.toast(R.string.error_loading_votes)
    }

    override fun showDislikesEmpty() {
        with(binding) {
            progressBar.gone()
            textViewNoVoteTitle.text = context.getText(R.string.no_dislikes_title)
            textViewNoVoteDescription.text = context.getText(R.string.no_dislikes_description)
            imageViewThumb.setImageResource(R.drawable.music_ic_thumb_down_hollow)
            layoutEmpty.visible()
            recyclerView.invisible()
        }
    }

    override fun showProgress() {
        with(binding) {
            recyclerView.invisible()
            topSeparator.invisible()
            progressBar.visible()
        }
    }

    override fun showDeleteError() {
        context.toast(R.string.error_removing_vote)
    }

    override fun showDeleteInProgressError() {
        context.toast(R.string.error_already_removing_vote)
    }
}
