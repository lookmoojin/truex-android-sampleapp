package com.truedigital.features.tuned.presentation.playlist.view

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
import com.truedigital.features.tuned.common.Constants.FLOAT_8F
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.share
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.common.extensions.visibilityGone
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.databinding.ActivityPlaylistBinding
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerCollectionStatus
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.popups.view.FullScreenImageDialog
import com.truedigital.features.tuned.presentation.popups.view.LoadingDialog
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.track.DragDropTrackAdapter
import com.truedigital.features.tuned.presentation.track.TrackAdapter
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.handlerScrolling
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject
import kotlin.math.abs

class PlaylistActivity :
    LifecycleComponentActivity(),
    PlaylistPresenter.ViewSurface,
    PlaylistPresenter.RouterSurface {

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var presenter: PlaylistPresenter

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val binding: ActivityPlaylistBinding by viewBinding(ActivityPlaylistBinding::inflate)
    private val musicShareViewModel: MusicShareViewModel by viewModels { viewModelFactory }
    private val musicPlayerStateViewModel: MusicPlayerStateViewModel by viewModels { viewModelFactory }

    private var fadeDistance: Int = 0
    private var scrimColor = Color.TRANSPARENT
    private var isFavourited: Boolean? = null
    private var playlistBottomSheet: BottomSheetProductPicker? = null
    private var isOwner = false
    private var hasPlaylistWriteRight = false

    private var mediaControllerCallback: MediaControllerCompat.Callback? = null
    private var lastPlaybackState: Int? = null

    private var favMenuItem: MenuItem? = null
    private var homeMenuItem: MenuItem? = null
    private var shareMenuItem: MenuItem? = null
    private var shouldShowFavMenu: Boolean = false
    private var shouldShowHomeMenu: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val artContainerLp = binding.playlistArtContainer.layoutParams as FrameLayout.LayoutParams
        artContainerLp.topMargin = actionBarHeight + statusBarHeight
        artContainerLp.bottomMargin = resources.dp(-FLOAT_8F)

        presenter.onInject(this, this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray)
        binding.toolbar.setNavigationContentDescription(R.string.playlist_button_back)
        binding.toolbar.navigationIcon?.colorFilter =
            PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)

        observeViewModel()

        var previousOffset = 0
        binding.playlistAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (verticalOffset != previousOffset) {
                    var ratio =
                        (abs(verticalOffset) - fadeDistance) /
                            (appBarLayout.totalScrollRange - fadeDistance).toFloat()
                    ratio = minOf(ratio, 1f)
                    ratio = maxOf(ratio, 0f)

                    val color = when {
                        Math.abs(verticalOffset) - binding.playlistAppBar.totalScrollRange == 0 -> {
                            Color.WHITE
                        }

                        else -> {
                            Color.TRANSPARENT
                        }

                        // Don't remove because may be used later. (Use for color gradient of background.)
                        // Math.abs(verticalOffset) > fadeDistance -> {
                        // Color.argb((ratio * 255.0f).toInt(), Color.red(scrimColor),
                        // Color.green(scrimColor), Color.blue(scrimColor) }
                    }

                    val drawable = ColorDrawable(color)
                    binding.playlistCollapsingToolbar.background = drawable
                    binding.playlistArtImageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

                    binding.viewOverlay.alpha = 1 - ratio
                    binding.playlistArtOverlay.alpha = 1 - ratio
                    if (config.enableShareAndFavIcon) {
                        binding.shareImageView.alpha = 1 - ratio
                        binding.starImageView.alpha = 1 - ratio
                    }
                    binding.layoutInfo.alpha = 1 - ratio
                    binding.toolbar.setTitleTextColor(Color.BLACK.withAlpha((ratio * ALPHA_255F).toInt()))
                }
                previousOffset = verticalOffset
            }
        )

        binding.moreButton.onClick { presenter.onShowMoreOptions() }
        binding.starImageView.onClick { presenter.onToggleFavourite() }
        binding.playlistArtImageView.onClick { presenter.onImageSelected() }

        lifecycleComponents.add(PresenterComponent(presenter))
    }

    override fun onStart() {
        super.onStart()
        mediaControllerCallback ?: registerMediaControllerCallback()
    }

    override fun onResume() {
        super.onResume()
        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(it.metadata?.trackId, it.playbackState?.state)
            lastPlaybackState = it.playbackState?.state
        }
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = PlaylistActivity::class.java.simpleName
                screenName =
                    MeasurementConstant.Music.ScreenName.SCREEN_NAME_LISTEN_PLAYLIST_DETAILS
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
            R.id.action_share -> presenter.onSharePlaylistClick()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun finish() {
        if (isFavourited == false && !isOwner) {
            val playlistString = intent.getStringExtra(PlaylistPresenter.PLAYLIST_KEY)
            val playlistId =
                if (!playlistString.isNullOrEmpty()) Playlist.fromString(playlistString).id
                else intent.getIntExtra(PlaylistPresenter.PLAYLIST_ID_KEY, -1)
            val intent = Intent()
            intent.putExtra(ProductListActivity.UPDATED_PRODUCT_ID_KEY, playlistId)
            setResult(Activity.RESULT_OK, intent)
        }
        super.finish()
    }

    // region ViewSurface

    override fun initPlaylist(playlist: Playlist) {
        supportActionBar?.title = playlist.name.valueForSystemLanguage(this)
        binding.playlistNameTextView.text = playlist.name.valueForSystemLanguage(this)
        binding.toolbar.setTitleTextColor(Color.TRANSPARENT)

        val description = playlist.description.valueForSystemLanguage(this)
        if (description.isNullOrEmpty()) {
            binding.playlistDescriptionTextView.gone()
        } else binding.playlistDescriptionTextView.text = description

        val coverImage = playlist.coverImage.valueForSystemLanguage(this)
        coverImage?.let {
            val backgroundSize =
                resources.getDimensionPixelSize(R.dimen.header_background_image_size)
            imageManager.init(this)
                .load(it)
                .options(backgroundSize, filters = arrayOf("blur(50, 10)", "saturation(1.2)"))
                .intoBitmap {
                    scrimColor = Palette.Builder(it).generate().getDarkMutedColor(scrimColor)
                    // Don't remove because may be used later. (Use for display background of album image)
                    // binding.playlistAppBar.background = BitmapDrawable(resources, it)
                }
            binding.playlistArtImageView.load(
                context = this,
                url = it,
                placeholder = R.drawable.placeholder_new_trueid_white_square
            )
        }
    }

    override fun showOnlinePlaylist(playlist: Playlist) {
        // add any initialization for online view of playlist here
    }

    override fun showOwner(isOwner: Boolean, isPublic: Boolean, hasPlaylistWriteRight: Boolean) {
        this.isOwner = isOwner
        this.hasPlaylistWriteRight = hasPlaylistWriteRight

        if (config.enableShareAndFavIcon && config.enableShare && !(isOwner && !isPublic)) {
            binding.shareImageView.visible()
        }
        if (config.enableShareAndFavIcon && config.enableFavourites && !isOwner) {
            binding.starImageView.visible()
        }

        // showOnline could be called before onCreateOptions Menu, set the flag to make sure it will be visible
        shouldShowFavMenu = !config.enableShareAndFavIcon && config.enableFavourites && !isOwner
        shouldShowHomeMenu = isNestedActivity()
        if (shouldShowFavMenu) favMenuItem?.isVisible = true
        if (shouldShowHomeMenu) homeMenuItem?.isVisible = true

        val canBeEdited = isOwner && config.enablePlaylistEditing && hasPlaylistWriteRight

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.separator)
            ?.let { dividerItemDecoration.setDrawable(it) }

        binding.rvSongs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSongs.addItemDecoration(dividerItemDecoration)
        binding.rvSongs.isNestedScrollingEnabled = false

        if (canBeEdited) {
//            rvSongs.adapter = DragDropTrackAdapter(
//                    onClickListener = { presenter.onSongSelected(it) },
//                    onRemoveListener = { presenter.onTrackRemoved(it) },
//                    onDragAndDropListener = { oldIndex, newIndex -> presenter.onTrackMoved(oldIndex, newIndex) },
//                    onLongClickListener = { track, index -> showTrackBottomSheet(track, index) },
//                    onMoreSelectedListener = { track, index -> showTrackBottomSheet(track, index) },
//                    onPageLoadListener = null /*{ presenter.loadMoreTracks() }*/)
//            (rvSongs.adapter as DragDropTrackAdapter).touchHelper.attachToRecyclerView(rvSongs)
        } else {
            binding.rvSongs.adapter = TrackAdapter(
                this,
                onClickListener = { _, position -> presenter.onSongSelected(position) },
                onLongClickListener = { showTrackBottomSheet(it) },
                onMoreSelectedListener = { showTrackBottomSheet(it) },
                onPageLoadListener = { presenter.loadMoreTracks() }
            )
        }
    }

    override fun showLoadPlaylistError() {
        alert {
            setMessage(R.string.error_load_playlist)
            setPositiveButton(R.string.dialog_ok) { _, _ ->
                ActivityCompat.finishAfterTransition(
                    this@PlaylistActivity
                )
            }
            setCancelable(false)
        }
    }

    override fun showPlaylistSongs(tracks: List<Track>, morePages: Boolean) {
        binding.progressBarPlaylist.visibilityGone()
        binding.playPlaylistButton.text = getString(R.string.playlist_play_button)
        binding.playPlaylistButton.onClick {
            if (binding.progressBarPlaylist.visibility != View.VISIBLE) presenter.onPlayPlaylist()
        }

        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(it.metadata?.trackId, it.playbackState?.state)
            lastPlaybackState = it.playbackState?.state
        }

        binding.layoutSongsError.gone()
        binding.playlistEmptySongs.gone()
        binding.rvSongs.visible()

        if (isOwner && config.enablePlaylistEditing && hasPlaylistWriteRight) {
            val adapter = binding.rvSongs.adapter as DragDropTrackAdapter
            adapter.items = tracks
            adapter.morePages = morePages
        } else {
            val adapter = binding.rvSongs.adapter as TrackAdapter
            adapter.items = tracks
            adapter.morePages = morePages
        }
    }

    override fun showPlaylistSongsLoading() {
        binding.layoutSongsError.gone()
        binding.playlistEmptySongs.gone()
        binding.rvSongs.gone()
    }

    override fun showPlaylistNoSongs() {
        binding.progressBarPlaylist.visibilityGone()
        binding.playPlaylistButton.text = getString(R.string.playlist_play_button)
        binding.rvSongs.gone()
        binding.playPlaylistButton.onClick {
            if (binding.progressBarPlaylist.visibility != View.VISIBLE) presenter.onPlayPlaylist()
        }

        binding.layoutSongsError.gone()
        binding.playlistEmptySongs.visible()
    }

    override fun showPlaylistSongsError() {
        binding.layoutSongsError.visible()
        binding.playlistEmptySongs.gone()
        binding.rvSongs.gone()
        binding.errorMoreRetryButton.onClick { presenter.onRetrySongs() }
    }

    override fun showPlayButtonLoading() {
        binding.progressBarPlaylist.visible()
        binding.playPlaylistButton.text = ""
    }

    override fun showCurrentPlayingTrack(trackId: Int?) {
        binding.rvSongs.adapter?.let {
            if (isOwner && config.enablePlaylistEditing && hasPlaylistWriteRight)
                (it as DragDropTrackAdapter).currentPlayingTrackIdAndIndex = (trackId ?: -1) to -1
            else
                (it as TrackAdapter).currentPlayingTrackId = trackId ?: -1
        }
    }

    override fun playPlaylist(
        playlist: Playlist,
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
                        startMusicForegroundService(this@PlaylistActivity.getMusicServiceIntent())
                        playTracks(
                            tracks,
                            playlist,
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

    override fun showLoading() {
        LoadingDialog.show(this)
    }

    override fun showFavourited(favourited: Boolean) {
        isFavourited = favourited
        playlistBottomSheet?.updateCollectionStatus(getCollectionStatus())
        binding.starImageView.setImageResource(
            if (favourited) {
                R.drawable.music_ic_star_ticked_black
            } else {
                R.drawable.music_ic_star_empty_black
            }
        )
        favMenuItem?.setIcon(
            if (favourited) {
                R.drawable.music_ic_star_ticked_black
            } else {
                R.drawable.music_ic_star_empty_black
            }
        )
    }

    override fun showFavouritedError() {
        binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
    }

    override fun showFavouritedToast() {
        if (config.enableFavourites)
            binding.root.showSnackBar(R.string.added_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showUnFavouritedToast() {
        binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
    }

    override fun showUnFavouritedError() {
        binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
    }

    override fun showMoreOptions(playlist: Playlist) {
        playlistBottomSheet = BottomSheetProductPicker(this) {
            itemType = ProductPickerType.PLAYLIST
            product = playlist
            isInCollectionStatus = getCollectionStatus()
            onOptionSelected = { this@PlaylistActivity.presenter.onMoreOptionSelected(it) }
        }
        playlistBottomSheet?.show()
    }

    override fun showShareOptions(playlist: Playlist) {
        musicShareViewModel.sharePlaylist(playlist.id.toString())
        presenter.trackFirebasePlaylistShare()
    }

    override fun showEnlargedImage(images: List<LocalisedString>?) {
        images?.valueForSystemLanguage(this)?.let {
            FullScreenImageDialog(this, it).show()
        }
    }

    // endregion

    // region RouterSurface

    override fun sharePlaylist(playlist: Playlist, link: String) {
        LoadingDialog.dismiss()
        share(
            getString(R.string.share_playlist_title, playlist.name.valueForSystemLanguage(this)),
            link
        )
    }

    override fun showAddToQueueToast() {
        binding.root.showSnackBar(R.string.add_to_queue_success, SnackBarType.SUCCESS)
    }

    private fun showTrackBottomSheet(track: Track) {
        BottomSheetProductPicker(this) {
            itemType = when {
                track.isVideo -> ProductPickerType.VIDEO
                isOwner -> ProductPickerType.PLAYLIST_SONG
                else -> ProductPickerType.SONG
            }
            product = track
            fragmentManager = supportFragmentManager
            onFavoriteItemClick = { isFavourited, isSuccess ->
                this@PlaylistActivity.presenter.onFavouriteSelect(isFavourited, isSuccess)
            }
            onOptionSelected = {
                this@PlaylistActivity.presenter.onMoreTrackOptionSelected(it)
                false
            }
        }.show()
    }

    private fun getMediaControllerCallback() = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state?.state != lastPlaybackState) {
                mediaSession.controller?.let {
                    presenter.onUpdatePlaybackState(it.metadata?.trackId, it.playbackState?.state)
                }
            }
            lastPlaybackState = state?.state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaSession.controller?.let {
                if (metadata != null) {
                    presenter.onUpdatePlaybackState(it.metadata?.trackId, it.playbackState?.state)
                }
            }
        }
    }

    private fun registerMediaControllerCallback() {
        mediaSession.controller?.let {
            presenter.onUpdatePlaybackState(it.metadata?.trackId, it.playbackState?.state)
            lastPlaybackState = it.playbackState?.state

            mediaControllerCallback = getMediaControllerCallback()
            mediaControllerCallback?.let { mediaControllerCallback ->
                it.registerCallback(mediaControllerCallback)
            }
        }
    }

    private fun getCollectionStatus(): ProductPickerCollectionStatus =
        when (isFavourited) {
            true -> ProductPickerCollectionStatus.IN_COLLECTION
            false -> ProductPickerCollectionStatus.NOT_IN_COLLECTION
            else -> ProductPickerCollectionStatus.PENDING_UPDATE
        }

    private fun observeViewModel() {
        musicShareViewModel.onOpenNativeShare().observe(this) { url ->
            NativeShareManagerImpl(this).onSharePress(
                shortUrl = url,
                title = "",
                imageUrl = "",
                NativeShareConstant.DYNAMIC_LINK
            )
        }

        with(musicPlayerStateViewModel) {
            onExpandedPlayerState().observe(this@PlaylistActivity) {
                binding.playlistAppBar.handlerScrolling(isEnabled = false)
            }
            onCollapsedPlayerState().observe(this@PlaylistActivity) {
                binding.playlistAppBar.handlerScrolling(isEnabled = true)
            }
        }
    }
}
