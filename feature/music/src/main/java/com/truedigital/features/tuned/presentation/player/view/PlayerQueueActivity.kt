package com.truedigital.features.tuned.presentation.player.view

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.Constants
import com.truedigital.features.tuned.common.extensions.alert
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.startServiceDefault
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ActivityPlayerQueueBinding
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.components.LifecycleComponentActivity
import com.truedigital.features.tuned.presentation.components.PresenterComponent
import com.truedigital.features.tuned.presentation.player.presenter.PlayerQueuePresenter
import com.truedigital.features.tuned.presentation.popups.view.UpgradePremiumDialog
import com.truedigital.features.tuned.presentation.track.DragDropTrackAdapter
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import javax.inject.Inject

class PlayerQueueActivity :
    LifecycleComponentActivity(),
    PlayerQueuePresenter.ViewSurface,
    PlayerQueuePresenter.RouterSurface {

    private val binding by viewBinding(ActivityPlayerQueueBinding::inflate)

    @Inject
    lateinit var presenter: PlayerQueuePresenter

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    @Inject
    lateinit var imageManager: ImageManager

    private var musicService: MusicPlayerService? = null
    private var musicServiceConnection: ServiceConnection? = null

    private var mediaControllerCallback: MediaControllerCompat.Callback? = null
    private var lastPlaybackState: Int? = null
    private var lastRepeatMode = PlaybackStateCompat.REPEAT_MODE_INVALID
    private var lastShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_INVALID

    private var btnRepeat: MenuItem? = null
    private var btnShuffle: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        presenter.onInject(this, this)
        window?.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        with(binding) {
            setSupportActionBar(toolbarLayout.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.music_ic_cross_black)
            toolbarLayout.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    this@PlayerQueueActivity,
                    R.color.theme_color_black
                )
            )
            toolbarLayout.toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(
                    this@PlayerQueueActivity,
                    R.color.theme_color_black
                ),
                PorterDuff.Mode.SRC_ATOP
            )

            val dividerItemDecoration =
                DividerItemDecoration(this@PlayerQueueActivity, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(this@PlayerQueueActivity, R.drawable.separator)
                ?.let { dividerItemDecoration.setDrawable(it) }

            tracksRecyclerView.layoutManager =
                LinearLayoutManager(this@PlayerQueueActivity, LinearLayoutManager.VERTICAL, false)
            tracksRecyclerView.adapter = DragDropTrackAdapter(
                onClickListener = {
                    startServiceDefault {
                        action = MusicPlayerController.ACTION_PLAY
                        putExtra(MusicPlayerController.ACTION_PLAY, it)
                    }
                },
                onDragAndDropListener = { oldIndex, newIndex ->
                    var adjustedIndex = newIndex
                    if (newIndex >= (tracksRecyclerView.adapter as DragDropTrackAdapter).items.size) {
                        adjustedIndex =
                            (tracksRecyclerView.adapter as DragDropTrackAdapter).items.size - 1
                    }
                    musicService?.moveTrack(oldIndex, adjustedIndex)
                    presenter.onItemMoved(oldIndex, adjustedIndex)
                },
                onRemoveListener = {
                    musicService?.removeTrack(it)
                    presenter.onItemRemoved(it)
                },
                onLongClickListener = { track, position -> showTrackBottomSheet(track, position) },
                onMoreSelectedListener = { track, position ->
                    showTrackBottomSheet(
                        track,
                        position
                    )
                }
            )
            tracksRecyclerView.addItemDecoration(dividerItemDecoration)
            (tracksRecyclerView.adapter as DragDropTrackAdapter).touchHelper.attachToRecyclerView(
                tracksRecyclerView
            )

            clearButton.onClick { presenter.onClearSelected() }

            if (!config.enablePlaylist || !config.enablePlaylistEditing || !presenter.getHasPlaylistWriteRight()) {
                saveButton.gone()
            }

            // needed to show correct state when player is paused or when the end of queue is reached
            lastRepeatMode = intent.getIntExtra(
                PlayerQueuePresenter.REPEAT_MODE_KEY,
                PlaybackStateCompat.REPEAT_MODE_NONE
            )
            lastShuffleMode = intent.getIntExtra(
                PlayerQueuePresenter.SHUFFLE_MODE_KEY,
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            )

            lifecycleComponents.add(PresenterComponent(presenter))
        }
    }

    override fun onStart() {
        super.onStart()
        musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                musicService = (binder as MusicPlayerService.PlayerBinder).service
                (binding.tracksRecyclerView.adapter as DragDropTrackAdapter).items =
                    musicService?.getCurrentQueueInfo()?.queueInDisplayOrder ?: listOf()

                mediaSession.controller?.let {
                    presenter.onUpdatePlaybackState(
                        it.metadata?.trackId,
                        musicService?.getCurrentQueueInfo()?.indexInDisplayOrder
                    )
                    lastPlaybackState = it.playbackState?.state

                    mediaControllerCallback = getMediaControllerCallback()
                    mediaControllerCallback?.let { callback -> it.registerCallback(callback) }
                }
            }
        }

        musicServiceConnection?.let { musicServiceConnection ->
            this.bindServiceMusic(musicServiceConnection)
        }
    }

    override fun onStop() {
        mediaControllerCallback?.let {
            mediaSession.controller?.unregisterCallback(it)
        }
        musicServiceConnection?.let { unbindService(it) }
        musicService = null
        musicServiceConnection = null
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_queue, menu)

        btnRepeat = menu.findItem(R.id.action_repeat)
        btnShuffle = menu.findItem(R.id.action_shuffle)

        when (lastRepeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                btnRepeat?.setIcon(R.drawable.music_ic_loop)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
            }

            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                btnRepeat?.setIcon(R.drawable.music_ic_loop_single_active)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
            }

            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                btnRepeat?.setIcon(R.drawable.music_ic_loop_active)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
            }

            else -> {
                btnRepeat?.setIcon(R.drawable.music_ic_loop)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX / 2
            }
        }

        when (lastShuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                btnShuffle?.setIcon(R.drawable.music_ic_shuffle_inactive)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
            }

            PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                btnShuffle?.setIcon(R.drawable.music_ic_shuffle_active)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
            }

            else -> {
                btnShuffle?.setIcon(R.drawable.music_ic_shuffle_inactive)
                btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX / 2
            }
        }
        btnRepeat?.icon?.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.theme_color_black),
            PorterDuff.Mode.SRC_ATOP
        )
        btnShuffle?.icon?.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.theme_color_black),
            PorterDuff.Mode.SRC_ATOP
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
            R.id.action_repeat -> presenter.onRepeatSelected()
            R.id.action_shuffle -> presenter.onShuffleSelected()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    // region ViewSurface

    override fun showCurrentPlayingTrack(trackId: Int?, index: Int?) {
        val queueAdapter = binding.tracksRecyclerView.adapter as DragDropTrackAdapter
        queueAdapter.currentPlayingTrackIdAndIndex = (trackId ?: -1) to (index ?: -1)
    }

    override fun showItemMoved(oldIndex: Int, newIndex: Int) {
        val queueAdapter = binding.tracksRecyclerView.adapter as DragDropTrackAdapter
        val track = queueAdapter.items[oldIndex]
        (queueAdapter.items as MutableList).removeAt(oldIndex)
        (queueAdapter.items as MutableList).add(newIndex, track)
        // no need to notify item move here, as it notifies the adapter as it moves
    }

    override fun showItemRemoved(index: Int) {
        val queueAdapter = binding.tracksRecyclerView.adapter as DragDropTrackAdapter
        (queueAdapter.items as MutableList).removeAt(index)
        queueAdapter.notifyItemRemoved(index)

        if (queueAdapter.itemCount == 0) {
            clearQueue()
        }
    }

    override fun toggleRepeat() {
        startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_REPEAT
        }
    }

    override fun toggleShuffle() {
        startServiceDefault {
            action = MusicPlayerController.ACTION_TOGGLE_SHUFFLE
        }
    }

    override fun showClearQueueDialog() {
        alert {
            setMessage(R.string.clear_queue_confirmation_message)
            setPositiveButton(R.string.dialog_yes) { _, _ ->
                clearQueue()
            }
            setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }
    }

    override fun clearQueue() {
        musicService?.clearQueue()
        ActivityCompat.finishAfterTransition(this)
    }

    override fun showUpgradeDialog() {
        UpgradePremiumDialog(this).show()
    }

    private fun showTrackBottomSheet(track: Track, position: Int) {
        BottomSheetProductPicker(this) {
            itemType = ProductPickerType.SONG_QUEUE
            product = track
            onRemoveFromQueue = {
                musicService?.removeTrack(position)
                this@PlayerQueueActivity.presenter.onItemRemoved(position)
            }
            onDismiss = { willNavigate ->
                if (willNavigate) ActivityCompat.finishAfterTransition(this@PlayerQueueActivity)
            }
        }.show()
    }

    private fun getMediaControllerCallback() = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            mediaSession.controller?.let {
                val shuffleMode = it.shuffleMode
                val repeatMode = it.repeatMode

                if (state?.state != lastPlaybackState) {
                    presenter.onUpdatePlaybackState(
                        it.metadata?.trackId,
                        musicService?.getCurrentQueueInfo()?.indexInDisplayOrder
                    )
                    lastPlaybackState = state?.state
                }

                if (shuffleMode != lastShuffleMode) {
                    when (shuffleMode) {
                        PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                            btnShuffle?.setIcon(R.drawable.music_ic_shuffle_inactive)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
                        }

                        PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                            btnShuffle?.setIcon(R.drawable.music_ic_shuffle_active)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
                        }

                        else -> {
                            btnShuffle?.setIcon(R.drawable.music_ic_shuffle_inactive)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX / 2
                        }
                    }
                    lastShuffleMode = shuffleMode
                }

                if (repeatMode != lastRepeatMode) {
                    when (repeatMode) {
                        PlaybackStateCompat.REPEAT_MODE_NONE -> {
                            btnRepeat?.setIcon(R.drawable.music_ic_loop)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
                        }

                        PlaybackStateCompat.REPEAT_MODE_ONE -> {
                            btnRepeat?.setIcon(R.drawable.music_ic_loop_single_active)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
                        }

                        PlaybackStateCompat.REPEAT_MODE_ALL -> {
                            btnRepeat?.setIcon(R.drawable.music_ic_loop_active)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX
                        }

                        else -> {
                            btnRepeat?.setIcon(R.drawable.music_ic_loop)
                            btnRepeat?.icon?.alpha = Constants.DRAWABLE_ALPHA_MAX / 2
                        }
                    }
                    lastRepeatMode = repeatMode
                }
                btnRepeat?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        this@PlayerQueueActivity,
                        R.color.theme_color_black
                    ),
                    PorterDuff.Mode.SRC_ATOP
                )
                btnShuffle?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        this@PlayerQueueActivity,
                        R.color.theme_color_black
                    ),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaSession.controller?.let {
                presenter.onUpdatePlaybackState(
                    it.metadata?.trackId,
                    musicService?.getCurrentQueueInfo()?.indexInDisplayOrder
                )
            }
        }
    }
}
