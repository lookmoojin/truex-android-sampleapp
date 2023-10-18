package com.truedigital.features.tuned.presentation.album.view

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
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.common.share.nativeshare.NativeShareManagerImpl
import com.truedigital.common.share.nativeshare.constant.NativeShareConstant
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.extensions.withAlpha
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.musicshare.MusicShareViewModel
import com.truedigital.features.music.presentation.player.MusicPlayerStateViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants.ALPHA_255F
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.Constants.FLOAT_8F
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.putExtras
import com.truedigital.features.tuned.common.extensions.sourceId
import com.truedigital.features.tuned.common.extensions.startActivity
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ActivityAlbumBinding
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter.Companion.ALBUM_ID_KEY
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
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
import com.truedigital.features.tuned.presentation.track.TrackAdapter
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.getAppDrawable
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.handlerScrolling
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject
import kotlin.math.abs

class AlbumActivity :
    LifecycleComponentActivity(),
    AlbumPresenter.ViewSurface,
    AlbumPresenter.RouterSurface {

    companion object {
        private const val CONTENT_TYPE_ALBUM = "music_album"
        private const val ALBUM_NAME_COUNT = 100
    }

    private val binding by viewBinding(ActivityAlbumBinding::inflate)

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var presenter: AlbumPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicShareViewModel: MusicShareViewModel by viewModels { viewModelFactory }
    private val musicPlayerStateViewModel: MusicPlayerStateViewModel by viewModels { viewModelFactory }

    private var fadeDistance: Int = 0
    private var scrimColor = Color.TRANSPARENT
    private var isFavourited: Boolean? = null
    private var albumBottomSheet: BottomSheetProductPicker? = null

    private var mediaControllerCallback: MediaControllerCompat.Callback? = null
    private var lastPlaybackState: Int? = null

    private var favMenuItem: MenuItem? = null
    private var shareMenuItem: MenuItem? = null
    private var homeMenuItem: MenuItem? = null
    private var shouldShowFavMenu: Boolean = false
    private var shouldShowHomeMenu: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this@AlbumActivity)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        with(binding) {
            val artContainerLp = albumArtContainer.layoutParams as FrameLayout.LayoutParams
            artContainerLp.topMargin = actionBarHeight + statusBarHeight
            artContainerLp.bottomMargin = resources.dp(-FLOAT_8F)

            presenter.onInject(this@AlbumActivity, this@AlbumActivity)

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray)
            toolbar.setNavigationContentDescription(R.string.album_button_back)
            toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
                Color.BLACK,
                PorterDuff.Mode.SRC_ATOP
            )

            val dividerItemDecoration =
                DividerItemDecoration(this@AlbumActivity, DividerItemDecoration.VERTICAL)
            getAppDrawable(R.drawable.separator)?.let { dividerItemDecoration.setDrawable(it) }

            recyclerViewTracks.layoutManager =
                LinearLayoutManager(this@AlbumActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewTracks.adapter = TrackAdapter(
                this@AlbumActivity,
                onClickListener = { track, _ -> presenter.onSongSelected(track.id) },
                onLongClickListener = { showTrackBottomSheet(it) },
                onMoreSelectedListener = { showTrackBottomSheet(it) },
                useVolumeIndex = true
            )
            recyclerViewTracks.addItemDecoration(dividerItemDecoration)
            recyclerViewTracks.isNestedScrollingEnabled = false

            recyclerViewMore.layoutManager =
                LinearLayoutManager(this@AlbumActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewMore.adapter = AlbumAdapter(
                imageManager,
                config.enableAlbumDetailedDescription,
                onClickListener = { presenter.onAlbumSelected(it) },
                onLongClickListener = {
                    BottomSheetProductPicker(this@AlbumActivity) {
                        itemType = ProductPickerType.ALBUM
                        product = it
                    }.show()
                }
            )
            recyclerViewMore.addItemDecoration(HorizontalSpacingItemDecoration())
            recyclerViewMore.isNestedScrollingEnabled = false

            var previousOffset = 0
            albumAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    if (verticalOffset != previousOffset) {
                        var ratio =
                            (abs(verticalOffset) - fadeDistance) /
                                (appBarLayout.totalScrollRange - fadeDistance).toFloat()
                        ratio = minOf(ratio, 1f)
                        ratio = maxOf(ratio, 0f)
                        val color = when {
                            Math.abs(verticalOffset) - albumAppBar.totalScrollRange == 0 -> {
                                Color.WHITE
                            }

                            else -> {
                                Color.TRANSPARENT
                            }
                        }

                        val drawable = ColorDrawable(color)
                        albumCollapsingToolbar.background = drawable
                        albumArtImageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

                        viewOverlay.alpha = 1 - ratio
                        albumArtOverlay.alpha = 1 - ratio
                        if (config.enableShareAndFavIcon) {
                            shareImageView.alpha = 1 - ratio
                            starImageView.alpha = 1 - ratio
                        }
                        layoutInfo.alpha = 1 - ratio
                        toolbar.setTitleTextColor(Color.BLACK.withAlpha((ratio * ALPHA_255F).toInt()))
                    }
                    previousOffset = verticalOffset
                }
            )

            errorTracksRetryButton.onClick { presenter.onRetryTracks() }
            errorMoreRetryButton.onClick { presenter.onRetryMoreAlbums() }
            moreImageView.onClick { presenter.onShowMoreOptions() }
            artistsTextView.onClick { presenter.onArtistSelected() }
            layoutViewMore.setOnClickListener { presenter.onArtistSelected() }
            shareImageView.onClick { presenter.onShareAlbumMenu() }
            starImageView.onClick { presenter.onToggleFavourite() }
            albumArtImageView.onClick { presenter.onImageSelected() }

            lifecycleComponents.add(PresenterComponent(presenter))

            seeAllTextView.isVisible = config.enableTextSeeAll
            seeAllArrowImageView.isVisible = !config.enableTextSeeAll
        }

        observersViewModel()
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
            lastPlaybackState = it.playbackState?.state
        }
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = AlbumActivity::class.java.simpleName
                screenName = MeasurementConstant.Music.ScreenName.SCREEN_NAME_LISTEN_ALBUM
            }
        )
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
        shareMenuItem = menu.findItem(R.id.action_share)
        shareMenuItem?.isVisible = true
        if (shouldShowFavMenu) favMenuItem?.isVisible = true
        if (shouldShowHomeMenu) homeMenuItem?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            R.id.action_fav -> presenter.onToggleFavourite()
            R.id.action_share -> presenter.onShareAlbumMenu()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun finish() {
        if (isFavourited == false) {
            val intent = Intent()
            val albumId = getIntent().getIntExtra(ALBUM_ID_KEY, -1)
            intent.putExtra(ProductListActivity.UPDATED_PRODUCT_ID_KEY, albumId)
            setResult(Activity.RESULT_OK, intent)
        }
        super.finish()
    }

    private fun observersViewModel() {
        musicShareViewModel.onOpenNativeShare().observe(this) { url ->
            NativeShareManagerImpl(this).onSharePress(
                shortUrl = url,
                title = "",
                imageUrl = "",
                NativeShareConstant.DYNAMIC_LINK
            )
        }

        with(musicPlayerStateViewModel) {
            onExpandedPlayerState().observe(this@AlbumActivity) {
                binding.albumAppBar.handlerScrolling(isEnabled = false)
            }
            onCollapsedPlayerState().observe(this@AlbumActivity) {
                binding.albumAppBar.handlerScrolling(isEnabled = true)
            }
        }
    }

    // region View Surface

    override fun initAlbum(album: Album) {
        with(binding) {
            supportActionBar?.title = album.name
            albumNameTextView.text = album.name
            toolbar.setTitleTextColor(Color.TRANSPARENT)

            if (album.primaryRelease?.artists?.isNotEmpty() == true) {
                layoutViewMore.visible()
                moreHeaderTextView.text = getString(
                    R.string.more_albums_from_title,
                    album.primaryRelease.artists.first().name
                )
                artistsTextView.text = getString(
                    R.string.album_by,
                    album.primaryRelease.artists.joinToString(", ") { it.name }
                )
            } else {
                layoutViewMore.gone()
                artistsTextView.text =
                    getString(R.string.album_by, getString(R.string.various_artists_title))
            }

            if (album.primaryRelease?.originalReleaseDate != null && album.primaryRelease.label?.name != null) {
                labelReleaseTextView.visible()
                val labelReleaseString =
                    "© ${album.primaryRelease.releaseYear()}, ${album.primaryRelease.label.name}"
                labelReleaseTextView.text = labelReleaseString
            }

            val backgroundSize =
                resources.getDimensionPixelSize(R.dimen.header_background_image_size)
            album.primaryRelease?.image?.let {
                imageManager.init(this@AlbumActivity)
                    .load(it)
                    .options(backgroundSize, filters = arrayOf("blur(50, 10)", "saturation(1.2)"))
                    .intoBitmap {
                        scrimColor = Palette.Builder(it).generate().getDarkMutedColor(scrimColor)
                        // Don't remove because may be used later. (Use for display background of album image)
                        // albumAppBar.background = BitmapDrawable(resources, it)
                    }
            }

            album.primaryRelease?.image?.let {
                albumArtImageView.load(
                    context = this@AlbumActivity,
                    url = it,
                    placeholder = R.drawable.placeholder_new_trueid_white_square
                )
            }
        }
    }

    override fun showShareAlbum(album: Album) {
        trackEventShare(album)
        musicShareViewModel.shareAlbum(album.id.toString())
    }

    override fun showOnlineAlbum(album: Album) = with(binding) {
        if (config.enableShareAndFavIcon && config.enableShare) shareImageView.visible()
        if (config.enableShareAndFavIcon && config.enableFavourites) starImageView.visible()

        // showOnline could be called before onCreateOptions Menu, set the flag to make sure it will be visible
        shouldShowFavMenu = !config.enableShareAndFavIcon && config.enableFavourites
        shouldShowHomeMenu = isNestedActivity()
        if (shouldShowFavMenu) favMenuItem?.isVisible = true
        if (shouldShowHomeMenu) homeMenuItem?.isVisible = true
    }

    override fun showNoAlbumShuffleRight() {
        binding.playAlbumButton.alpha = FLOAT_0_5F
    }

    override fun showLoadAlbumError() {
        alert {
            setMessage(R.string.error_load_album)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@AlbumActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showTracks(tracks: List<Track>, selectedTrackId: Int) = with(binding) {
        progressBarAlbum.visibilityGone()
        playAlbumButton.text = getString(R.string.album_play_button)
        playAlbumButton.onClick { if (progressBarAlbum.visibility != View.VISIBLE) presenter.onPlayAlbum() }

        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(
                it.metadata?.sourceId,
                it.metadata?.trackId,
                it.playbackState?.state
            )
            lastPlaybackState = it.playbackState?.state
        }

        val tracksAdapter = recyclerViewTracks.adapter as TrackAdapter
        tracksAdapter.selectedTrackId = selectedTrackId
        tracksAdapter.items = tracks
        if (tracksAdapter.hasMoreThanOneVolume) {
            separatorTracks.gone()
        } else {
            separatorTracks.visible()
        }
        layoutTracksError.gone()
    }

    override fun showLoadingTracks() = with(binding) {
        layoutTracksError.gone()
    }

    override fun showCurrentPlayingTrack(trackId: Int?) {
        val tracksAdapter = binding.recyclerViewTracks.adapter as TrackAdapter
        tracksAdapter.currentPlayingTrackId = trackId ?: -1
    }

    override fun playAlbum(
        album: Album,
        tracks: List<Track>,
        startIndex: Int?,
        forceShuffle: Boolean,
        forceSequential: Boolean,
        isOffline: Boolean
    ) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                if (binder is MusicPlayerService.PlayerBinder) {
                    binder.service.apply {
                        startMusicForegroundService(this@AlbumActivity.getMusicServiceIntent())
                        playTracks(
                            tracks,
                            album,
                            startIndex,
                            forceShuffle,
                            forceSequential,
                            isOffline
                        )
                    }
                }
                unbindService(this)
            }
        }
        bindServiceMusic(musicServiceConnection)
    }

    override fun showTracksError() = with(binding) {
        layoutTracksError.visible()
    }

    override fun showLoadingMoreAlbums() = with(binding) {
        layoutMoreError.gone()
    }

    override fun showMoreAlbums(albums: List<Album>) = with(binding) {
        (recyclerViewMore.adapter as AlbumAdapter).items = albums
        layoutMoreError.gone()
    }

    override fun showMoreAlbumsError() = with(binding) {
        layoutMoreError.visible()
    }

    override fun hideMoreAlbums() = with(binding) {
        moreBgContainer.gone()
        layoutViewMore.gone()
        recyclerViewMore.gone()
    }

    override fun showFavourited(favourited: Boolean) {
        isFavourited = favourited
        albumBottomSheet?.updateCollectionStatus(getCollectionStatus())

        val starImageRes = if (favourited) {
            R.drawable.music_ic_star_ticked_black
        } else {
            R.drawable.music_ic_star_empty_black
        }
        binding.starImageView.setImageResource(starImageRes)

        favMenuItem?.setIcon(
            if (favourited) R.drawable.music_ic_star_ticked_black
            else R.drawable.music_ic_star_empty_black
        )
    }

    override fun showFavouritedError() {
        binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
    }

    override fun showFavouritedToast() {
        if (config.enableFavourites) binding.root.showSnackBar(
            R.string.added_to_favorite,
            SnackBarType.SUCCESS
        )
    }

    override fun showUnFavouritedToast() {
        binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showUnFavouritedError() {
        binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
    }

    override fun showNavigationNotAllowed() {
        alert {
            setMessage(R.string.error_album_navigation_not_allowed)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@AlbumActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showUpgradeDialog() {
        UpgradePremiumDialog(this).show()
    }

    override fun showMoreOptions(album: Album) {
        albumBottomSheet = BottomSheetProductPicker(this) {
            itemType = ProductPickerType.ALBUM
            product = album
            isInCollectionStatus = getCollectionStatus()
            onOptionSelected = { this@AlbumActivity.presenter.onMoreOptionSelected(it) }
        }
        albumBottomSheet?.show()
    }

    override fun showShareLoading() {
        LoadingDialog.show(this)
    }

    override fun showEnlargedImage(image: String) {
        FullScreenImageDialog(this, image).show()
    }

    //endregion

    //region Router Surface

    override fun navigateToAlbum(album: Album) {
        startActivity(AlbumActivity::class) {
            putExtras(ALBUM_ID_KEY to album.id)
        }
    }

    override fun navigateToArtist(id: Int) {
        startActivity(ArtistActivity::class) {
            putExtras(ArtistPresenter.ARTIST_ID_KEY to id)
        }
    }

    override fun showAddToQueueToast() {
        binding.root.showSnackBar(R.string.add_to_queue_success, SnackBarType.SUCCESS)
    }

    private fun trackEventShare(album: Album) {
        analyticManager.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SOCIAL_SHARE,
                MeasurementConstant.Key.KEY_TITLE to album.name.take(ALBUM_NAME_COUNT),
                MeasurementConstant.Key.KEY_CMS_ID to album.id,
                MeasurementConstant.Key.KEY_CONTENT_TYPE to CONTENT_TYPE_ALBUM
            )
        )
    }

    //endregion

    private fun showTrackBottomSheet(track: Track) {
        BottomSheetProductPicker(this) {
            itemType = ProductPickerType.ALBUM_SONG
            product = track
            fragmentManager = supportFragmentManager
            onFavoriteItemClick = { isFavourited, isSuccess ->
                this@AlbumActivity.presenter.onFavouriteSelect(isFavourited, isSuccess)
            }
            onOptionSelected = {
                this@AlbumActivity.presenter.onMoreTrackOptionSelected(it)
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
        when (isFavourited) {
            true -> ProductPickerCollectionStatus.IN_COLLECTION
            false -> ProductPickerCollectionStatus.NOT_IN_COLLECTION
            else -> ProductPickerCollectionStatus.PENDING_UPDATE
        }
}
