package com.truedigital.features.tuned.presentation.station.view

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.browse
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.sourceId
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.databinding.ViewOverviewBinding
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.artist.view.ArtistAdapter
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.HorizontalSpacingItemDecoration
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.components.LifecycleComponentView
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.popups.view.UpgradePremiumDialog
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.station.presenter.StationOverviewPresenter
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import javax.inject.Inject

class StationOverviewView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LifecycleComponentView(context, attrs),
    StationOverviewPresenter.ViewSurface,
    StationOverviewPresenter.RouterSurface {

    @Inject
    lateinit var presenter: StationOverviewPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    val binding: ViewOverviewBinding by lazy {
        ViewOverviewBinding.inflate(LayoutInflater.from(this.context), this, true)
    }

    private var mediaControllerCallback: MediaControllerCompat.Callback? = null
    private var lastPlaybackState: Int? = null

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        presenter.onInject(this, this)
        with(binding) {
            featuringList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            featuringList.adapter = ArtistAdapter(
                imageManager,
                onClickListener = { presenter.onArtistSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(context) {
                        itemType = ProductPickerType.ARTIST
                        product = it
                    }.show()
                }
            )
            featuringList.addItemDecoration(HorizontalSpacingItemDecoration())
            featuringList.isNestedScrollingEnabled = false
            featuringErrorRetryButton.onClick { presenter.onRetryFeaturedArtists() }

            similarList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            similarList.adapter = StationAdapter(
                imageManager,
                onClickListener = { presenter.onStationSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(context) {
                        itemType = ProductPickerType.MIX
                        product = it
                    }.show()
                }
            )
            similarList.addItemDecoration(HorizontalSpacingItemDecoration())
            similarList.isNestedScrollingEnabled = false
            similarErrorRetryButton.onClick { presenter.onRetrySimilarStations() }

            featuringViewMore.onClick { presenter.onFeaturingSeeAllSelected() }
            similarViewMore.onClick { presenter.onSimilarSeeAllSelected() }
        }

        lifecycleComponents.add(PresenterComponent(presenter))
    }

    override fun onStart(arguments: Bundle?) {
        super.onStart(arguments)
        mediaControllerCallback ?: registerMediaControllerCallback()
    }

    override fun onResume() {
        super.onResume()
        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(it.metadata?.sourceId, it.playbackState?.state)
            lastPlaybackState = it.playbackState?.state
        }
    }

    override fun onStop() {
        super.onStop()
        mediaControllerCallback?.let {
            mediaSession.controller?.unregisterCallback(it)
        }
        mediaControllerCallback = null
    }

    // region ViewSurface

    override fun initStation(station: Station) {
        with(binding) {
            progressBarStation.visibilityGone()
            playStation.text = context.getString(R.string.station_play_button)
            playStation.onClick { if (progressBarStation.visibility != View.VISIBLE) presenter.onPlayStation() }

            val description = station.description.valueForSystemLanguage(context)
            if (description.isNullOrBlank()) stationDescription.gone()
            else {
                stationDescription.visible()
                stationDescription.text = description
            }

            mediaSession.controller?.let {
                presenter.onUpdatePlaybackState(it.metadata?.sourceId, it.playbackState?.state)
                lastPlaybackState = it.playbackState?.state
            }

            station.bannerImage.valueForSystemLanguage(context)?.let {
                stationBanner.visible()
                val layoutParams = stationBanner.layoutParams as LinearLayout.LayoutParams
                val displayMetrics = DisplayMetrics()
                (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(
                    displayMetrics
                )
                val width =
                    displayMetrics.widthPixels - layoutParams.marginStart - layoutParams.marginEnd
                imageManager.init(this@StationOverviewView)
                    .load(it)
                    .options(width, width / 2)
                    .into(stationBanner)

                station.bannerURL?.let {
                    stationBanner.isClickable = true
                    stationBanner.onClick { presenter.onStationBannerSelected() }
                }
            }
        }
    }

    override fun showOnlineStation(station: Station) {
        // add online station initialization here
    }

    override fun playStation(station: Station, trackHash: String?) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(context.getMusicServiceIntent())
                        playStation(station, trackHash)
                    }
                }
                context.unbindService(this)
            }
        }
        context.bindServiceMusic(musicServiceConnection)
    }

    override fun showFeaturedArtists(artists: List<Artist>) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllFeaturing.visible()
            else ivFeaturingNavigationArrow.visible()
            featuringViewMore.isClickable = true

            featuringError.gone()
            featuringProgressBar.visibilityGone()
            featuringList.visible()
            (featuringList.adapter as ArtistAdapter).items = artists
        }
    }

    override fun showFeaturedArtistsLoading() {
        with(binding) {
            ivFeaturingNavigationArrow.gone()
            tvSeeAllFeaturing.gone()
            featuringViewMore.isClickable = false

            featuringError.gone()
            featuringProgressBar.visible()
            featuringList.gone()
        }
    }

    override fun showFeaturedArtistsError() {
        with(binding) {
            ivFeaturingNavigationArrow.gone()
            tvSeeAllFeaturing.gone()
            featuringViewMore.isClickable = false

            featuringError.visible()
            featuringProgressBar.visibilityGone()
            featuringList.gone()
        }
    }

    override fun hideFeaturedArtists() {
        with(binding) {
            featuringBgContainer.gone()
            featuringViewMore.gone()
            featuringContainer.gone()
        }
    }

    override fun showSimilarStations(stations: List<Station>) {
        with(binding) {
            if (config.enableTextSeeAll) tvSeeAllSimilar.visible()
            else ivSimilarNavigationArrow.visible()
            similarViewMore.isClickable = true

            similarError.gone()
            similarProgressBar.visibilityGone()
            similarList.visible()
            (similarList.adapter as StationAdapter).items = stations
        }
    }

    override fun showSimilarStationsLoading() {
        with(binding) {
            ivSimilarNavigationArrow.gone()
            tvSeeAllSimilar.gone()
            similarViewMore.isClickable = false

            similarError.gone()
            similarProgressBar.visible()
            similarList.gone()
        }
    }

    override fun showSimilarStationsError() {
        with(binding) {
            ivSimilarNavigationArrow.gone()
            tvSeeAllSimilar.gone()
            similarViewMore.isClickable = false

            similarError.visible()
            similarProgressBar.visibilityGone()
            similarList.gone()
        }
    }

    override fun hideSimilarStations() {
        with(binding) {
            similarBgContainer.gone()
            similarViewMore.gone()
            similarContainer.gone()
        }
    }

    override fun showPlayStation() = with(binding) {
        progressBarStation.visibilityGone()
        playStation.text = context.resources.getString(R.string.station_play_button)
    }

    override fun showPlayButtonLoading() = with(binding) {
        progressBarStation.visible()
        playStation.text = ""
    }

    override fun showUpgradeDialog() {
        UpgradePremiumDialog(context).show()
    }

    override fun showNoArtistShuffleRights() {
        binding.playStation.alpha = FLOAT_0_5F
    }

    // endregion

    // region RouterSurface

    override fun navigateToArtist(artist: Artist) {
        context.startActivity(ArtistActivity::class) {
            putExtras(ArtistPresenter.ARTIST_KEY to artist)
        }
    }

    override fun navigateToStation(station: Station) {
        context.startActivity(StationActivity::class) {
            putExtras(StationPresenter.STATION_KEY to station)
        }
    }

    override fun navigateToUrl(url: Uri) {
        context.browse(url)
    }

    override fun navigateToProductList(type: ProductListType, stationId: Int) {
        context.startActivity(ProductListActivity::class) {
            val activityTitle = when (type) {
                ProductListType.STATION_FEATURE_ARTIST -> context.getString(R.string.featuring_title)
                ProductListType.STATION_SIMILAR -> context.getString(R.string.similar_title)
                else -> throw IllegalArgumentException("station product type not supported")
            }
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to type.name,
                ProductListActivity.ACTIVITY_NAME_KEY to activityTitle,
                ProductListActivity.PRODUCT_LIST_ID_KEY to stationId
            )
        }
    }

    // endregion

    private fun getMediaControllerCallback() = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state?.state != lastPlaybackState) {
                mediaSession.controller?.let {
                    presenter.onUpdatePlaybackState(it.metadata?.sourceId, it.playbackState?.state)
                }
            }
            lastPlaybackState = state?.state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaSession.controller?.let {
                presenter.onUpdatePlaybackState(it.metadata?.sourceId, it.playbackState?.state)
            }
        }
    }

    private fun registerMediaControllerCallback() {
        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(it.metadata?.sourceId, it.playbackState?.state)
            lastPlaybackState = it.playbackState?.state

            mediaControllerCallback = getMediaControllerCallback()
            mediaControllerCallback?.let { mediaControllerCallback ->
                it.registerCallback(mediaControllerCallback)
            }
        }
    }
}
