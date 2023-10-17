package com.truedigital.features.tuned.presentation.artist.view

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.extensions.withAlpha
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.player.MusicPlayerStateViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.ALPHA_255F
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.Constants.FLOAT_12F
import com.truedigital.features.tuned.common.Constants.FLOAT_8F
import com.truedigital.features.tuned.common.Constants.INT_3
import com.truedigital.features.tuned.common.Constants.INT_4
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.fadeIn
import com.truedigital.features.tuned.common.extensions.fadeOut
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.onLayoutFinish
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.sourceId
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ActivityArtistBinding
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.album.view.AlbumAdapter
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter.Companion.ARTIST_ID_KEY
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter.Companion.ARTIST_KEY
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.HorizontalSpacingItemDecoration
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.popups.view.FullScreenImageDialog
import com.truedigital.features.tuned.presentation.popups.view.LoadingDialog
import com.truedigital.features.tuned.presentation.popups.view.UpgradePremiumDialog
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.productlist.view.ProductListHorizontalAdapter
import com.truedigital.features.tuned.presentation.station.presenter.StationPresenter
import com.truedigital.features.tuned.presentation.station.view.StationActivity
import com.truedigital.features.tuned.presentation.station.view.StationAdapter
import com.truedigital.features.tuned.presentation.track.SongVideoAdapter
import com.truedigital.features.tuned.presentation.track.TrackAdapter
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.getAppDrawable
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.handlerScrolling
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject
import kotlin.math.abs

class ArtistActivity :
    LifecycleComponentActivity(),
    ArtistPresenter.ViewSurface,
    ArtistPresenter.RouterSurface {

    companion object {
        private const val NUM_OF_TRENDING_TRACKS = 5
        private const val NUM_OF_LATEST_TRACKS = 3
    }

    private val binding by viewBinding(ActivityArtistBinding::inflate)

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicPlayerStateViewModel: MusicPlayerStateViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var presenter: ArtistPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    private var fadeDistance: Int = 0
    private var scrimColor = Color.TRANSPARENT

    private var warningDialog: AlertDialog? = null

    private var mediaControllerCallback: MediaControllerCompat.Callback? = null

    private var lastPlaybackState: Int? = null
    private var isFollowed: Boolean? = null
    private var favMenuItem: MenuItem? = null
    private var artistBottomSheet: BottomSheetProductPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this@ArtistActivity)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        presenter.onInject(this@ArtistActivity, this@ArtistActivity)

        window?.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray)
        binding.toolbar.setNavigationContentDescription(R.string.artist_button_back)
        binding.toolbar.navigationIcon?.colorFilter =
            PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)

        scrimColor = ContextCompat.getColor(this@ArtistActivity, R.color.primary)
        fadeDistance = resources.getDimensionPixelSize(R.dimen.header_scrim_fade_distance)

        val artContainerLp = binding.artistArtContainer.layoutParams as FrameLayout.LayoutParams
        artContainerLp.topMargin = actionBarHeight + statusBarHeight
        artContainerLp.bottomMargin = resources.dp(-FLOAT_8F)

        initAppBar()
        initPopularTracks()
        initLatestTracks()
        initVideoAppears()
        initStation()
        initAlbum()
        initAlbumAppear()
        initSimilarArtist()
        initOnClick()
        observeViewModel()

        lifecycleComponents.add(PresenterComponent(presenter))

        if (config.enableShareAndFavIcon && config.enableShare) binding.ivShare.visible()
        if (config.enableShareAndFavIcon && config.enableFavourites) binding.ivStar.visible()

        hideRadioButton()
    }

    override fun onStart() {
        super.onStart()
        mediaControllerCallback ?: registerMediaControllerCallback()
    }

    override fun onResume() {
        super.onResume()
        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(
                it.metadata?.sourceId,
                it.metadata?.trackId,
                it.playbackState?.state
            )
        }
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = ArtistActivity::class.java.simpleName
                screenName = MeasurementConstant.Music.ScreenName.SCREEN_NAME_LISTEN_ARTIST
            }
        )
    }

    override fun onPause() {
        super.onPause()

        warningDialog?.dismiss()
        artistBottomSheet?.dismiss()
    }

    override fun onStop() {
        super.onStop()
        mediaControllerCallback?.let {
            mediaSession.controller?.unregisterCallback(it)
        }
        mediaControllerCallback = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav_home, menu)
        favMenuItem = menu.findItem(R.id.action_fav)
        favMenuItem?.let {
            if (!config.enableShareAndFavIcon && config.enableFavourites) it.isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            R.id.action_fav -> presenter.onToggleFollow()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun finish() {
        if (isFollowed == false) {
            val artist = intent.getParcelableExtra<Artist>(ARTIST_KEY)
            val artistId = artist?.id ?: intent.getIntExtra(ARTIST_ID_KEY, -1)
            val intent = Intent()
            intent.putExtra(ProductListActivity.UPDATED_PRODUCT_ID_KEY, artistId)
            setResult(Activity.RESULT_OK, intent)
        }
        super.finish()
    }

    // prevent the CollapsingToolbarLayout from scrolling/collapsing while hint is visible
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (binding.hintOverlay.visibility == View.VISIBLE) {
            binding.hintOverlay.onTouchEvent(ev)
            false
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    // endregion

    // region ViewSurface

    override fun showArtist(artist: Artist) {
        with(binding) {
            supportActionBar?.title = artist.name
            textViewArtistName.text = artist.name
            toolbar.setTitleTextColor(Color.TRANSPARENT)

            appearsInTitle.text = getString(R.string.mixes_with_title, artist.name)

            val backgroundSize =
                resources.getDimensionPixelSize(R.dimen.header_background_image_size)

            artist.image?.let {
                imageManager.init(this@ArtistActivity)
                    .load(it)
                    .options(backgroundSize, filters = arrayOf("blur(50, 10)", "saturation(1.2)"))
                    .intoBitmap { bitmap ->
                        scrimColor =
                            Palette.Builder(bitmap).generate().getDarkMutedColor(scrimColor)
                        // Don't remove because may be used later. (Use for display background of artist image)
                        // artistAppBar.background = BitmapDrawable(resources, bitmap)
                    }
                artistArt.load(
                    context = this@ArtistActivity,
                    url = it,
                    placeholder = R.drawable.placeholder_new_trueid_white_square
                )
            }

            progressBarRadio.visible()
            buttonRadio.gone()
            buttonRadio.onClick {
                if (progressBarRadio.visibility != View.VISIBLE && buttonRadio.alpha == 1f) {
                    presenter.onPlayArtistMix()
                }
            }

            mediaSession.controller?.let {
                presenter.onUpdatePlaybackState(
                    it.metadata?.sourceId,
                    it.metadata?.trackId,
                    it.playbackState?.state
                )
                lastPlaybackState = it.playbackState?.state
            }
        }
    }

    override fun showLoadArtistError() {
        warningDialog = alert {
            setMessage(R.string.load_artist_error)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@ArtistActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showPopularSongs(tracks: List<Track>) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllTrendingImageView.visible()
        } else {
            ivTrendingNavigationArrow.visible()
        }
        artistsTrendingViewMore.isClickable = true

        popularTracksList.visible()

        progressBarPlay.visibilityGone()
        buttonPlayArtist.alpha = 1f
        if (progressBarPlay.visibility == View.VISIBLE) buttonPlayArtist.text =
            getString(R.string.artist_play_button)
        buttonPlayArtist.onClick { if (progressBarPlay.visibility != View.VISIBLE) presenter.onPlayArtistShuffle() }

        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(
                it.metadata?.sourceId,
                it.metadata?.trackId,
                it.playbackState?.state
            )
            lastPlaybackState = it.playbackState?.state
        }

        val tracksAdapter = popularTracksList.adapter as TrackAdapter
        tracksAdapter.items = tracks
        popularTracksError.gone()
        separatorPopularTracks.visible()
    }

    override fun showPopularSongsLoading() = with(binding) {
        seeAllTrendingImageView.gone()
        ivTrendingNavigationArrow.gone()
        artistsTrendingViewMore.isClickable = false

        popularTracksError.gone()
        popularTracksList.gone()
        separatorPopularTracks.gone()
    }

    override fun showPopularSongsError() = with(binding) {
        seeAllTrendingImageView.gone()
        ivTrendingNavigationArrow.gone()
        artistsTrendingViewMore.isClickable = false

        popularTracksError.visible()
        popularTracksList.gone()
        separatorPopularTracks.gone()

        progressBarPlay.visibilityGone()
        buttonPlayArtist.text = getString(R.string.artist_play_button)
        buttonPlayArtist.alpha = FLOAT_0_5F
    }

    override fun hidePopularSongs() = with(binding) {
        artistsTrendingBgContainer.gone()
        artistsTrendingViewMore.gone()
        popularTracksContainer.gone()

        progressBarPlay.visibilityGone()
        buttonPlayArtist.text = getString(R.string.artist_play_button)
        buttonPlayArtist.alpha = FLOAT_0_5F
    }

    override fun showLatestSongs(tracks: List<Track>) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllLatestImageView.visible()
        } else {
            ivLatestNavigationArrow.visible()
        }
        artistsLatestViewMore.isClickable = true

        progressBarLatestTracks.visibilityGone()
        latestTracksList.visible()

        val tracksAdapter = latestTracksList.adapter as ProductListHorizontalAdapter
        tracksAdapter.items = tracks
        latestTracksError.gone()
        separatorLatestTracks.visible()
    }

    override fun showLatestSongsLoading() = with(binding) {
        seeAllLatestImageView.gone()
        ivLatestNavigationArrow.gone()
        artistsLatestViewMore.isClickable = false

        progressBarLatestTracks.visible()
        latestTracksError.gone()
        latestTracksList.gone()
        separatorLatestTracks.gone()
    }

    override fun showLatestSongsError() = with(binding) {
        seeAllLatestImageView.gone()
        ivLatestNavigationArrow.gone()
        artistsLatestViewMore.isClickable = false

        progressBarLatestTracks.visibilityGone()
        latestTracksError.visible()
        latestTracksList.gone()
        separatorLatestTracks.gone()
    }

    override fun hideLatestSongs() = with(binding) {
        artistsLatestBgContainer.gone()
        artistsLatestViewMore.gone()
        latestTracksContainer.gone()
    }

    override fun showVideoAppearsIn(videos: List<Track>, morePages: Boolean) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllVideoAppearsInImageView.visible()
        } else {
            ivVideoAppearsInNavigationArrow.visible()
        }
        videoAppearsInViewMore.isClickable = true

        videoAppearsInProgressBar.visibilityGone()
        videoAppearsInError.gone()
        videoAppearsInList.visible()

        val adapter = videoAppearsInList.adapter as SongVideoAdapter
        adapter.morePages = morePages
        adapter.items = videos
    }

    override fun showVideoAppearsInLoading() = with(binding) {
        seeAllVideoAppearsInImageView.gone()
        ivVideoAppearsInNavigationArrow.gone()
        videoAppearsInViewMore.isClickable = false

        videoAppearsInProgressBar.visible()
        videoAppearsInError.gone()
        videoAppearsInList.gone()
    }

    override fun showVideoAppearsInError() = with(binding) {
        seeAllVideoAppearsInImageView.gone()
        ivVideoAppearsInNavigationArrow.gone()
        videoAppearsInViewMore.isClickable = false

        videoAppearsInProgressBar.visibilityGone()
        videoAppearsInList.gone()
        videoAppearsInError.visible()
    }

    override fun hideVideoAppearsIn() = with(binding) {
        videoAppearsInBgContainer.gone()
        videoAppearsInViewMore.gone()
        videoAppearsInContainer.gone()
    }

    override fun showStationsAppearsIn(stations: List<Station>) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllAppearsInImageView.visible()
        } else {
            ivAppearsInNavigationArrow.visible()
        }
        appearsInViewMore.isClickable = true

        appearsInProgressBar.visibilityGone()
        appearsInError.gone()
        appearsInList.visible()
        (appearsInList.adapter as StationAdapter).items = stations
    }

    override fun showStationsAppearsInLoading() = with(binding) {
        seeAllAppearsInImageView.gone()
        ivAppearsInNavigationArrow.gone()
        appearsInViewMore.isClickable = false

        appearsInProgressBar.visible()
        appearsInError.gone()
        appearsInList.gone()
    }

    override fun showStationsAppearsInError() = with(binding) {
        seeAllAppearsInImageView.gone()
        ivAppearsInNavigationArrow.gone()
        appearsInViewMore.isClickable = false

        appearsInProgressBar.visibilityGone()
        appearsInList.gone()
        appearsInError.visible()
    }

    override fun hideStationsAppearsIn() = with(binding) {
        appearsInBgContainer.gone()
        appearsInViewMore.gone()
        appearsInContainer.gone()
    }

    override fun showAlbums(albums: List<Album>, morePages: Boolean) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllAlbumsImageView.visible()
        } else {
            ivAlbumsNavigationArrow.visible()
        }
        albumsViewMore.isClickable = true

        albumsProgressBar.visibilityGone()
        albumsError.gone()
        albumsList.visible()

        val adapter = albumsList.adapter as AlbumAdapter
        adapter.morePages = morePages
        adapter.items = albums
    }

    override fun showAlbumsLoading() = with(binding) {
        seeAllAlbumsImageView.gone()
        ivAlbumsNavigationArrow.gone()
        albumsViewMore.isClickable = false

        albumsProgressBar.visible()
        albumsError.gone()
        albumsList.gone()
    }

    override fun showAlbumsError() = with(binding) {
        seeAllAlbumsImageView.gone()
        ivAlbumsNavigationArrow.gone()
        albumsViewMore.isClickable = false

        albumsProgressBar.visibilityGone()
        albumsList.gone()
        albumsError.visible()
    }

    override fun hideAlbums() = with(binding) {
        albumsBgContainer.gone()
        albumsViewMore.gone()
        albumsContainer.gone()
    }

    override fun showAlbumsAppearsOn(albums: List<Album>) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllAppearsOnAlbumsImageView.visible()
        } else {
            ivAppearsOnAlbumsNavigationArrow.visible()
        }
        appearsOnAlbumsViewMore.isClickable = true

        appearsOnAlbumsProgressBar.visibilityGone()
        appearsOnAlbumsError.gone()
        appearsOnAlbumsList.visible()
        (appearsOnAlbumsList.adapter as AlbumAdapter).items = albums
    }

    override fun showAlbumsAppearsOnLoading() = with(binding) {
        seeAllAppearsOnAlbumsImageView.gone()
        ivAppearsOnAlbumsNavigationArrow.gone()
        appearsOnAlbumsViewMore.isClickable = false

        appearsOnAlbumsProgressBar.visible()
        appearsOnAlbumsError.gone()
        appearsOnAlbumsList.gone()
    }

    override fun showAlbumsAppearsOnError() = with(binding) {
        seeAllAppearsOnAlbumsImageView.gone()
        ivAppearsOnAlbumsNavigationArrow.gone()
        appearsOnAlbumsViewMore.isClickable = false

        appearsOnAlbumsProgressBar.visibilityGone()
        appearsOnAlbumsList.gone()
        appearsOnAlbumsError.visible()
    }

    override fun hideAlbumsAppearsOn() = with(binding) {
        appearsOnAlbumsBgContainer.gone()
        appearsOnAlbumsViewMore.gone()
        appearsOnAlbumsContainer.gone()
    }

    override fun showSimilarArtists(artists: List<Artist>) = with(binding) {
        if (config.enableTextSeeAll) {
            seeAllSimilarArtistsImageView.visible()
        } else {
            ivSimilarArtistsNavigationArrow.visible()
        }
        similarArtistsViewMore.isClickable = true

        similarArtistsProgressBar.visibilityGone()
        similarArtistsError.gone()
        similarArtistsList.visible()
        (similarArtistsList.adapter as ArtistAdapter).items = artists
    }

    override fun showSimilarArtistsLoading() = with(binding) {
        seeAllSimilarArtistsImageView.gone()
        ivSimilarArtistsNavigationArrow.gone()
        similarArtistsViewMore.isClickable = false

        similarArtistsProgressBar.visible()
        similarArtistsError.gone()
        similarArtistsList.gone()
    }

    override fun showSimilarArtistsError() = with(binding) {
        seeAllSimilarArtistsImageView.gone()
        ivSimilarArtistsNavigationArrow.gone()
        similarArtistsViewMore.isClickable = false

        similarArtistsProgressBar.visibilityGone()
        similarArtistsError.visible()
        similarArtistsList.gone()
    }

    override fun hideSimilarArtists() = with(binding) {
        similarArtistsBgContainer.gone()
        similarArtistsViewMore.gone()
        similarArtistsContainer.gone()
    }

    override fun playArtistSongs(
        artist: Artist,
        tracks: List<Track>,
        startIndex: Int?,
        forceShuffle: Boolean,
        forceSequential: Boolean
    ) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@ArtistActivity.getMusicServiceIntent())
                        playTracks(
                            tracks,
                            artist,
                            startIndex,
                            forceShuffle,
                            forceSequential
                        )
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    override fun showNoArtistShuffleRight() {
        binding.buttonPlayArtist.alpha = FLOAT_0_5F
    }

    override fun showCurrentPlayingTrack(trackId: Int?) {
        val tracksAdapter = binding.popularTracksList.adapter as TrackAdapter
        tracksAdapter.currentPlayingTrackId = trackId ?: -1
    }

    override fun showFollowed(followed: Boolean) {
        isFollowed = followed
        artistBottomSheet?.updateCollectionStatus(getCollectionStatus())
        val starImageRes = if (followed) {
            R.drawable.music_ic_star_ticked_black
        } else {
            R.drawable.music_ic_star_empty_black
        }
        binding.ivStar.setImageResource(starImageRes)
        favMenuItem?.setIcon(
            if (followed) R.drawable.music_ic_star_ticked_black
            else R.drawable.music_ic_star_empty_black
        )
    }

    override fun showFollowError() {
        binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
    }

    override fun showUpgradeDialog() {
        UpgradePremiumDialog(this).show()
    }

    override fun showArtistMixDisabled() = with(binding) {
        progressBarRadio.visibilityGone()
        buttonRadio.visible()
        buttonRadio.alpha = FLOAT_0_5F
    }

    override fun showArtistSimilarStationError() {
        toast(R.string.play_artist_station_error)
    }

    override fun showFollowSuccess() {
        binding.root.showSnackBar(R.string.added_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showUnFollowSuccess() {
        binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showUnFollowError() {
        binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
    }

    override fun playArtistMix(station: Station) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@ArtistActivity.getMusicServiceIntent())
                        playStation(station)
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    override fun showPlayArtistMix() = with(binding) {
        progressBarRadio.visibilityGone()
        buttonRadio.visible()
        buttonRadio.setImageResource(R.drawable.music_ic_radio)
    }

    override fun showLoadingArtistMix() = with(binding) {
        progressBarRadio.visible()
        buttonRadio.gone()
    }

    override fun showArtistMixEnabled() {
        with(binding) {
            progressBarRadio.visibilityGone()
            buttonRadio.visible()
            buttonRadio.alpha = 1f
        }

        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(
                it.metadata?.sourceId,
                it.metadata?.trackId,
                it.playbackState?.state
            )
            lastPlaybackState = it.playbackState?.state
        }
    }

    override fun showMoreOptions(artist: Artist) {
        artistBottomSheet = BottomSheetProductPicker(this) {
            itemType = ProductPickerType.ARTIST
            product = artist
            isInCollectionStatus = getCollectionStatus()
            onOptionSelected = { this@ArtistActivity.presenter.onMoreOptionSelected(it) }
        }
        artistBottomSheet?.show()
    }

    override fun showArtistPlayCount(count: Int) = with(binding) {
        tvActivityType.invisible()
        llPlayCount.visible()

        val suffixAry = arrayOf("", "k", "m", "b", "t")
        var display = ""
        val length = count.toString().length
        val firstLetter = count.toString()[0].toString()
        var suffixIndex = (length - 1) / INT_3
        if (suffixIndex > INT_4) suffixIndex = INT_4
        if (suffixIndex == 0) {
            display = "<1k"
        } else {
            val suffix = suffixAry[suffixIndex]
            val zeros = length - suffixIndex * INT_3 - 1
            display = firstLetter
            repeat(zeros) {
                display = "${display}0"
            }
            display = ">$display$suffix"
        }
        tvPlayCount.text = display
    }

    override fun hideArtistPlayCount() = with(binding) {
        tvActivityType.visible()
        llPlayCount.invisible()
    }

    override fun showArtistVotesCleared() {
        toast(R.string.clear_votes_success)
    }

    override fun showClearArtistVotesError() {
        toast(R.string.clear_votes_error)
    }

    override fun playVideo(video: Track) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@ArtistActivity.getMusicServiceIntent())
                        playVideo(video)
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    override fun showArtistHint() = with(binding) {
        hintOverlay.fadeIn()

        // manually calculate hint position because using fixed dp
        // can make the view go off the button depending on screen size and pixel density
        // since setting id on Coordinator Layout will crash the app, we use parent to get to the coordinator layout
        (artistAppBar.parent as CoordinatorLayout).onLayoutFinish {
            val lpShuffleHint = playShuffleHint.layoutParams as FrameLayout.LayoutParams
            val lpMixHint = playMixHint.layoutParams as FrameLayout.LayoutParams
            val playShuffleLocation = IntArray(2)
            val playMixLocation = IntArray(2)
            val halfTooltipArrowSize = resources.dp(FLOAT_12F) // tooltip arrows are 24dp x 24dp

            // getScreenLocation gets coordinates at the center of the view, so need to adjust it afterwards
            buttonPlayArtist.measure(0, View.MeasureSpec.UNSPECIFIED)
            buttonPlayArtist.getLocationOnScreen(playShuffleLocation)
            val mt = playShuffleLocation[1] -
                playShuffleHint.height - (buttonPlayArtist.measuredHeight / 2) + halfTooltipArrowSize
            lpShuffleHint.topMargin = mt

            buttonRadio.measure(0, View.MeasureSpec.UNSPECIFIED)
            buttonRadio.getLocationOnScreen(playMixLocation)
            lpMixHint.topMargin =
                playMixLocation[1] + (buttonRadio.measuredHeight / 2) - halfTooltipArrowSize

            lpShuffleHint.topMargin += statusBarHeight
            lpMixHint.topMargin += statusBarHeight

            playShuffleHint.layoutParams = lpShuffleHint
            playMixHint.layoutParams = lpMixHint
        }

        hintOverlay.onClick {
            hintOverlay.fadeOut()
            presenter.onCloseHint()
        }
        hideHintOverlay()
    }

    override fun showShareLoading() {
        LoadingDialog.show(this)
    }

    override fun showEnlargedImage(image: String) {
        FullScreenImageDialog(this, image).show()
    }

    // region RouterSurface

    override fun navigateToStation(station: Station) {
        startActivity(StationActivity::class) {
            putExtras(StationPresenter.STATION_KEY to station)
        }
    }

    override fun navigateToArtist(artist: Artist) {
        startActivity(ArtistActivity::class) {
            putExtras(ARTIST_KEY to artist)
        }
    }

    override fun navigateToProductList(type: ProductListType, artist: Artist) {
        startActivity(ProductListActivity::class) {
            val activityTitle = when (type) {
                ProductListType.ARTIST_TRACKS -> getString(R.string.trending_title)
                ProductListType.ARTIST_VIDEO -> getString(R.string.videos_title)
                ProductListType.ARTIST_STATION -> getString(R.string.mixes_with_title, artist.name)
                ProductListType.ARTIST_ALBUM -> getString(R.string.albums_title)
                ProductListType.ARTIST_APPEAR_ON -> getString(R.string.appears_on_albums_title)
                ProductListType.ARTIST_SIMILAR -> getString(R.string.similar_title)
                ProductListType.ARTIST_LATEST -> getString(R.string.latest_songs_title)
                else -> throw IllegalArgumentException("artist product type not supported")
            }
            putExtras(
                ProductListActivity.PRODUCT_LIST_TYPE_KEY to type.name,
                ProductListActivity.ACTIVITY_NAME_KEY to activityTitle,
                ProductListActivity.PRODUCT_LIST_ID_KEY to artist.id,
                ProductListActivity.PRODUCT_LIST_TAG_KEY to artist.name,
                ProductListActivity.IS_GRID_KEY to
                    (type != ProductListType.ARTIST_TRACKS && type != ProductListType.ARTIST_LATEST)
            )
        }
    }

    override fun navigateToAlbum(album: Album) {
        startActivity(AlbumActivity::class) {
            putExtras(AlbumPresenter.ALBUM_ID_KEY to album.id)
        }
    }

    override fun showAddToQueueToast() {
        binding.root.showSnackBar(R.string.add_to_queue_success, SnackBarType.SUCCESS)
    }

    private fun observeViewModel() = with(musicPlayerStateViewModel) {
        onExpandedPlayerState().observe(this@ArtistActivity) {
            binding.artistAppBar.handlerScrolling(isEnabled = false)
        }
        onCollapsedPlayerState().observe(this@ArtistActivity) {
            binding.artistAppBar.handlerScrolling(isEnabled = true)
        }
    }

    private fun showTrackBottomSheet(track: Track) {
        BottomSheetProductPicker(this) {
            itemType = ProductPickerType.ARTIST_SONG
            product = track
            fragmentManager = supportFragmentManager
            onFavoriteItemClick = { isFavourited, isSuccess ->
                this@ArtistActivity.presenter.onFavouriteSelect(isFavourited, isSuccess)
            }
            onOptionSelected = {
                this@ArtistActivity.presenter.onMoreTrackOptionSelected(it)
                false
            }
        }.show()
    }

    private fun getMediaControllerCallback() = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state?.state != lastPlaybackState) {
                mediaSession.controller?.let {
                    presenter.onUpdatePlaybackState(
                        it.metadata?.sourceId,
                        it.metadata?.trackId,
                        it.playbackState?.state
                    )
                }
            }
            lastPlaybackState = state?.state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaSession.controller?.let {
                presenter.onUpdatePlaybackState(
                    it.metadata?.sourceId,
                    it.metadata?.trackId,
                    it.playbackState?.state
                )
            }
        }
    }

    private fun registerMediaControllerCallback() {
        mediaSession.controller?.let { controller ->
            presenter.onUpdatePlaybackState(
                controller.metadata?.sourceId,
                controller.metadata?.trackId,
                controller.playbackState?.state
            )
            lastPlaybackState = controller.playbackState?.state

            mediaControllerCallback = getMediaControllerCallback()
            mediaControllerCallback?.let { callback -> controller.registerCallback(callback) }
        }
    }

    private fun getCollectionStatus(): ProductPickerCollectionStatus =
        when (isFollowed) {
            true -> ProductPickerCollectionStatus.IN_COLLECTION
            false -> ProductPickerCollectionStatus.NOT_IN_COLLECTION
            else -> ProductPickerCollectionStatus.PENDING_UPDATE
        }

    private fun hideRadioButton() = with(binding) {
        if (config.enableRadioButton) {
            flRadio.visible()
            buttonRadio.visible()
        } else {
            flRadio.invisible()
            buttonRadio.gone()
        }
    }

    private fun hideHintOverlay() = with(binding) {
        if (config.enableHintOverlay) {
            hintOverlay.visible()
        } else {
            hintOverlay.gone()
        }
    }

    private fun initAppBar() = with(binding) {
        var previousOffset = 0
        artistAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (verticalOffset != previousOffset) {
                    var ratio =
                        (abs(verticalOffset) - fadeDistance) /
                            (appBarLayout.totalScrollRange - fadeDistance).toFloat()
                    ratio = minOf(ratio, 1f)
                    ratio = maxOf(ratio, 0f)

                    val color = when (abs(verticalOffset) - artistAppBar.totalScrollRange) {
                        0 -> {
                            Color.WHITE
                        }

                        else -> {
                            Color.TRANSPARENT
                        }
                    }

                    val drawable = ColorDrawable(color)
                    artistCollapsingToolbar.background = drawable
                    artistArt.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

                    viewOverlay.alpha = 1 - ratio
                    artistArt.alpha = 1 - ratio
                    if (config.enableShareAndFavIcon) {
                        ivShare.alpha = 1 - ratio
                        ivStar.alpha = 1 - ratio
                    }
                    layoutInfo.alpha = 1 - ratio
                    toolbar.setTitleTextColor(Color.BLACK.withAlpha((ratio * ALPHA_255F).toInt()))
                }
                previousOffset = verticalOffset
            }
        )
    }

    private fun initPopularTracks() = with(binding) {
        val dividerItemDecoration =
            DividerItemDecoration(this@ArtistActivity, DividerItemDecoration.VERTICAL)
        getAppDrawable(R.drawable.separator)?.let { dividerItemDecoration.setDrawable(it) }

        popularTracksList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.VERTICAL, false)
        popularTracksList.adapter = TrackAdapter(
            this@ArtistActivity,
            onClickListener = { _, position -> presenter.onPlayArtistTrendingTrack(position) },
            onLongClickListener = { showTrackBottomSheet(it) },
            onMoreSelectedListener = { showTrackBottomSheet(it) },
            numItemsToDisplay = NUM_OF_TRENDING_TRACKS
        )
        popularTracksList.addItemDecoration(dividerItemDecoration)
        popularTracksList.isNestedScrollingEnabled = false
        buttonErrorPopularTracksRetry.onClick { presenter.onRetryPopularSongs() }
    }

    private fun initLatestTracks() = with(binding) {
        latestTracksList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.VERTICAL, false)
        latestTracksList.adapter = ProductListHorizontalAdapter(
            imageManager,
            numItemsToDisplay = NUM_OF_LATEST_TRACKS,
            onClickListener = { presenter.onArtistLatestTrackSelected(it as Track) },
            onLongClickListener = { showTrackBottomSheet(it as Track) },
            onMoreSelectedListener = { showTrackBottomSheet(it as Track) }
        )
        latestTracksList.isNestedScrollingEnabled = false
        buttonErrorLatestTracksRetry.onClick { presenter.onRetryLatestSongs() }
    }

    private fun initVideoAppears() = with(binding) {
        videoAppearsInList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.HORIZONTAL, false)
        videoAppearsInList.adapter = SongVideoAdapter(
            imageManager,
            onPageLoadListener = { presenter.onLoadMoreVideos() },
            onClickListener = { presenter.onVideoSelected(it) },
            onLongClickListener = {
                BottomSheetProductPicker(this@ArtistActivity) {
                    itemType = ProductPickerType.VIDEO
                    product = it
                }.show()
            }
        )
        videoAppearsInList.addItemDecoration(HorizontalSpacingItemDecoration())
        videoAppearsInList.isNestedScrollingEnabled = false
        videoAppearsInErrorRetryButton.onClick { presenter.onRetryVideosAppearsIn() }
    }

    private fun initStation() = with(binding) {
        appearsInList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.HORIZONTAL, false)
        appearsInList.adapter = StationAdapter(
            imageManager,
            onClickListener = { presenter.onStationSelected(it) },
            onLongClickListener = {
                BottomSheetProductPicker(this@ArtistActivity) {
                    itemType = ProductPickerType.MIX
                    product = it
                }.show()
            }
        )
        appearsInList.addItemDecoration(HorizontalSpacingItemDecoration())
        appearsInList.isNestedScrollingEnabled = false
        appearsInErrorRetryButton.onClick { presenter.onRetryStationsAppearsIn() }
    }

    private fun initAlbum() = with(binding) {
        albumsList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.HORIZONTAL, false)
        albumsList.adapter = AlbumAdapter(
            imageManager,
            config.enableAlbumDetailedDescription,
            onPageLoadListener = { presenter.onLoadMoreAlbums() },
            onClickListener = { presenter.onAlbumSelected(it) },
            onLongClickListener = {
                BottomSheetProductPicker(this@ArtistActivity) {
                    itemType = ProductPickerType.ALBUM
                    product = it
                }.show()
            }
        )
        albumsList.addItemDecoration(HorizontalSpacingItemDecoration())
        albumsList.isNestedScrollingEnabled = false
        albumsRetryButton.onClick { presenter.onRetryAlbums() }
    }

    private fun initAlbumAppear() = with(binding) {
        appearsOnAlbumsList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.HORIZONTAL, false)
        appearsOnAlbumsList.adapter = AlbumAdapter(
            imageManager,
            config.enableAlbumDetailedDescription,
            onClickListener = { presenter.onAlbumSelected(it) },
            onLongClickListener = {
                BottomSheetProductPicker(this@ArtistActivity) {
                    itemType = ProductPickerType.ALBUM
                    product = it
                }.show()
            }
        )
        appearsOnAlbumsList.addItemDecoration(HorizontalSpacingItemDecoration())
        appearsOnAlbumsList.isNestedScrollingEnabled = false
        appearsOnAlbumsRetryButton.onClick { presenter.onRetryAlbumsAppearsOn() }
    }

    private fun initSimilarArtist() = with(binding) {
        similarArtistsList.layoutManager =
            LinearLayoutManager(this@ArtistActivity, LinearLayoutManager.HORIZONTAL, false)
        similarArtistsList.adapter = ArtistAdapter(
            imageManager,
            onClickListener = { presenter.onArtistSelected(it) },
            onLongClickListener = {
                BottomSheetProductPicker(this@ArtistActivity) {
                    itemType = ProductPickerType.ARTIST
                    product = it
                }.show()
            }
        )
        similarArtistsList.addItemDecoration(HorizontalSpacingItemDecoration())
        similarArtistsList.isNestedScrollingEnabled = false
        similarArtistsErrorRetryButton.onClick { presenter.onRetrySimilarArtists() }
    }

    private fun initOnClick() = with(binding) {
        buttonMore.onClick { presenter.onShowMoreOptions() }
        ivStar.onClick { presenter.onToggleFollow() }
        artistArt.onClick { presenter.onImageSelected() }

        artistsTrendingViewMore.setOnClickListener { presenter.onTrendingSeeAllSelected() }
        artistsLatestViewMore.setOnClickListener { presenter.onLatestSeeAllSelected() }
        videoAppearsInViewMore.setOnClickListener { presenter.onVideoAppearsInSeeAllSelected() }
        appearsInViewMore.setOnClickListener { presenter.onAppearsInSeeAllSelected() }
        albumsViewMore.setOnClickListener { presenter.onAlbumsSeeAllSelected() }
        appearsOnAlbumsViewMore.setOnClickListener { presenter.onAppearsOnAlbumsSeeAllSelected() }
        similarArtistsViewMore.setOnClickListener { presenter.onSimilarSeeAllSelected() }
    }
}
