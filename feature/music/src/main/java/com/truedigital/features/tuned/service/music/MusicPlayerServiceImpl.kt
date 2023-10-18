package com.truedigital.features.tuned.service.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Surface
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.facebook.crypto.Crypto
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.truedigital.core.extensions.audioManager
import com.truedigital.core.extensions.runOnUiThread
import com.truedigital.features.music.data.player.repository.CacheServicePlayerRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.TUNED_OKHTTP_BUILDER
import com.truedigital.features.tuned.presentation.widget.PlayerWidget
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.features.tuned.service.music.model.TrackQueueInfo
import com.truedigital.features.tuned.service.music.player.AdPlayer
import com.truedigital.features.tuned.service.music.player.RadioPlayer
import com.truedigital.features.tuned.service.music.player.StakkarPlayer
import com.truedigital.features.tuned.service.music.player.TrackPlayer
import com.truedigital.features.tuned.service.music.player.VideoPlayer
import com.truedigital.features.tuned.service.util.PlayQueue
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class MusicPlayerServiceImpl :
    MusicPlayerService(),
    MusicPlayerController.PlayerSurface {
    companion object {
        private const val PROGRESS_UPDATE_DURATION = 5000
        private const val META_DURATION = 1000L
        private const val TIME_DURATION = 500L
        private const val BUFFER_MILLIS = 12000
    }

    @Inject
    lateinit var controller: MusicPlayerController

    @Inject
    lateinit var imageManager: ImageManager

    @field:[Inject Named(TUNED_OKHTTP_BUILDER)]
    lateinit var httpClientBuilder: Provider<OkHttpClient.Builder>

    @Inject
    lateinit var crypto: Crypto

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var config: Configuration

    @Inject
    lateinit var cacheServicePlayerRepository: CacheServicePlayerRepository

    private val binder = PlayerBinder()
    private lateinit var mediaSessionServiceNotification: MediaSessionServiceNotification

    private var positionScheduler: ScheduledFuture<out Any>? = null

    private lateinit var stakkarPlayer: StakkarPlayer
    private lateinit var adPlayer: AdPlayer
    private lateinit var radioPlayer: RadioPlayer
    private lateinit var trackPlayers: List<TrackPlayer>
    private lateinit var videoPlayers: List<VideoPlayer>

    private lateinit var currentPlayer: BasePlayer
    private var previousPlaybackState: Int = Player.STATE_IDLE

    // Accumulate listening time here so we can use it to update shared prefs periodically rather than
    // smash it every time the position is updated
    private var listenedDuration = 0L
    private var lastPlaybackPosition = 0L

    // region Service lifecycle

    override fun onCreate() {
        super.onCreate()

        Timber.d("--------> Service onCreate")
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        controller.onInject(this)
        controller.onStart()

        trackPlayers = listOf(
            TrackPlayer(
                this,
                httpClientBuilder,
                crypto,
                sourceEventListener,
                drmSessionEventListener
            ),
            TrackPlayer(
                this,
                httpClientBuilder,
                crypto,
                sourceEventListener,
                drmSessionEventListener
            )
        )

        videoPlayers = listOf(VideoPlayer(this), VideoPlayer(this))

        currentPlayer = trackPlayers.first()

        adPlayer = AdPlayer(this)

        stakkarPlayer = StakkarPlayer(this)

        radioPlayer = RadioPlayer(this)

        mediaSessionServiceNotification =
            MediaSessionServiceNotification(this, mediaSession, imageManager, config)

        registerReceiver(
            becomingNoisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )

        mediaSession.controller?.registerCallback(object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                PlayerWidget.triggerUpdate(this@MusicPlayerServiceImpl)
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                PlayerWidget.triggerUpdate(this@MusicPlayerServiceImpl)
            }
        })
    }

    // this method won't be called according to docs as we set stopwithtask in manifest for this service
    // however, we leave it here in case
    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    // use trim memory
//    override fun onLowMemory() {
//        cleanUpBeforeTermination()
//        super.onLowMemory()
//    }

    override fun onDestroy() {
        Timber.d("--------> Service onDestroy")
        cleanUpBeforeTermination()
        super.onDestroy()
    }

    private fun cleanUpBeforeTermination() {
        stopForeground(true)
        stopSelf()
        resetMetaData()
        controller.onStop()
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        // new method abandonAudioFocusRequest(AudioFocusRequest) requires API 26
        @Suppress("DEPRECATION")
        audioManager().abandonAudioFocus(audioFocusChangeListener)
        try {
            unregisterReceiver(becomingNoisyReceiver)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("--------> BINDING onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("--------> BINDING onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("--------> Service onStartCommand")
        startNotification()
        if (intent != null && intent.action != null) {
            when (intent.action) {
                MusicPlayerController.ACTION_PAUSE -> controller.onPauseAudio()
                MusicPlayerController.ACTION_PLAY -> {
                    requestAudioFocus()

                    val startIndex = intent.getIntExtra(MusicPlayerController.ACTION_PLAY, -1)
                    if (startIndex >= 0) controller.onPlayTrack(startIndex)
                    else if (currentPlayer is RadioPlayer) playLiveRadio()
                    else controller.onPlayAudio()
                }

                MusicPlayerController.ACTION_SKIP_PREVIOUS -> controller.onSkipToPrevious()
                MusicPlayerController.ACTION_SKIP_NEXT -> {
                    val skipIndex = intent.getIntExtra(MusicPlayerController.ACTION_SKIP_NEXT, -1)
                    if (skipIndex >= 0) controller.onSkipToTrack(skipIndex)
                    else controller.onSkipToNext()
                }

                MusicPlayerController.ACTION_STOP -> controller.onStopAudio()
                MusicPlayerController.ACTION_SEEK -> controller.onSeekTo(
                    intent.getLongExtra(
                        MusicPlayerController.ACTION_SEEK,
                        -1
                    )
                )

                MusicPlayerController.ACTION_LIKE -> controller.onLike()
                MusicPlayerController.ACTION_DISLIKE -> controller.onDislike()
                MusicPlayerController.ACTION_REMOVE_RATING -> controller.onRemoveRating()
                MusicPlayerController.ACTION_TOGGLE_SHUFFLE -> controller.onToggleShuffle()
                MusicPlayerController.ACTION_TOGGLE_REPEAT -> controller.onToggleRepeat()
                MusicPlayerController.ACTION_REPLAY -> controller.onReplay()
                else -> MediaButtonReceiver.handleIntent(mediaSession, intent)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun startMusicForegroundService(intent: Intent) {
        ContextCompat.startForegroundService(this, intent)
    }
    // endregion

    // region Exposed methods

    override fun playStation(station: Station, trackHash: String?) {
        station.resetPlayerSource()
        controller.playRadio(station, trackHash)
    }

    override fun playOfflineStation(station: Station, tracks: List<Track>) {
        station.resetPlayerSource(true)
        tracks.map { it.playerSource = station }
        controller.playRadio(station, tracks = tracks)
    }

    override fun playTracks(
        tracks: List<Track>,
        playerSource: PlayerSource?,
        startIndex: Int?,
        forceShuffle: Boolean,
        forceSequential: Boolean,
        isOffline: Boolean
    ) {
        playerSource?.resetPlayerSource(isOffline)
        tracks.map {
            it.resetPlayerSource(isOffline)
            it.playerSource = playerSource ?: it
        }
        tracks.getOrNull(startIndex ?: 0)?.playerSource?.let {
            controller.playFullStream(
                it,
                tracks,
                startIndex,
                forceShuffle,
                forceSequential
            )
        }
    }

    override fun playVideo(video: Track) {
        video.resetPlayerSource()
        video.playerSource = video
        controller.playVideo(video)
    }

    override fun playVideo(videoId: Int) {
        controller.playVideo(videoId)
    }

    override fun playStakkar(stakkar: Stakkar) {
        controller.onPlayStakkar(stakkar)
    }

    override fun playStakkar(stakkarId: Int) {
        controller.onPlayStakkar(stakkarId)
    }

    override fun playRadio(radio: MusicForYouItemModel.RadioShelfItem) {
        controller.onPlayRadio(radio)
    }

    override fun playAd(adId: Int, url: String?) {
        controller.onPlayAd(adId, url)
    }

    override fun dismissAd(adId: Int) {
        controller.onDismissAd(adId)
    }

    override fun attachVideoSurface(surface: Surface) {
        currentPlayer.surface = surface
    }

    override fun clearVideoSurface() {
        currentPlayer.surface = null
    }

    override fun attachPlayerView(playerView: com.google.android.exoplayer2.ui.StyledPlayerView) {
        playerView.player = currentPlayer
    }

    override fun getAvailableSkips(): Int = controller.onGetAvailableSkip()

    override fun getCurrentQueueInfo(): TrackQueueInfo? = controller.onGetCurrentQueue()

    override fun addTracks(tracks: List<Track>, playerSource: PlayerSource?, isOffline: Boolean) {
        playerSource?.resetPlayerSource(isOffline)
        tracks.map {
            it.resetPlayerSource(isOffline)
            it.playerSource = playerSource ?: it
        }
        controller.onAddTracks(tracks)
    }

    override fun removeTrack(index: Int) = controller.onRemoveTrack(index)

    override fun moveTrack(oldIndex: Int, newIndex: Int) =
        controller.onMoveTrack(oldIndex, newIndex)

    override fun clearQueue() {
        controller.onClearQueue()
        mediaSessionServiceNotification.clearPlayerNotification()
    }

    override fun updateQueue(trackList: List<Track>) = controller.updateQueue(trackList)

    // endregion

    // region PlayerSurface

    override fun play(mediaAsset: MediaAsset) {
        when (mediaAsset) {
            is MusicPlayerController.TrackMediaAsset -> play(
                mediaAsset,
                getPlayerForAsset(mediaAsset)
            )

            is MusicPlayerController.AdMediaAsset -> play(mediaAsset, adPlayer)
            is MusicPlayerController.StakkarMediaAsset -> play(mediaAsset, stakkarPlayer)
            is MusicPlayerController.RadioMediaAsset -> play(mediaAsset, radioPlayer)
        }
    }

    override fun resume(mediaAsset: MediaAsset) {
        if (currentPlayer.assetId == null || currentPlayer.assetId != mediaAsset.id) {
            val player = when (mediaAsset) {
                is MusicPlayerController.StakkarMediaAsset -> stakkarPlayer
                is MusicPlayerController.AdMediaAsset -> adPlayer
                is MusicPlayerController.RadioMediaAsset -> radioPlayer
                else -> getPlayerForAsset(mediaAsset)
            }

            if (player.assetId == null || player.assetId != mediaAsset.id) {
                play(mediaAsset, player)
            } else {
                currentPlayer.removeListener(playerEventListener)
                currentPlayer.playWhenReady = false
                currentPlayer.surface = null

                currentPlayer = player
                currentPlayer.playWhenReady = true
                mediaSession.setPlaybackState(
                    getPlaybackStateBuilder()
                        .setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            currentPlayer.currentPosition,
                            1f
                        )
                        .build()
                )
                startTrackingPosition(currentPlayer)
            }
        } else {
            currentPlayer.playWhenReady = true
            mediaSession.setPlaybackState(
                getPlaybackStateBuilder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, currentPlayer.currentPosition, 1f)
                    .build()
            )
            startTrackingPosition(currentPlayer)
        }
    }

    private fun playLiveRadio() {
        val currentPosition = currentPlayer.currentPosition
        val bufferPosition = currentPlayer.bufferedPosition
        val currentMediaItemIndex = currentPlayer.currentMediaItemIndex
        val window =
            currentPlayer.currentTimeline.getWindow(currentMediaItemIndex, Timeline.Window())
        val defaultPosition = window.defaultPositionMs

        if (defaultPosition > currentPosition) {
            currentPlayer.seekToDefaultPosition()
        } else {
            val remainBufferPosition = (bufferPosition - BUFFER_MILLIS)
            val position = if (remainBufferPosition > currentPosition) {
                remainBufferPosition
            } else {
                currentPosition
            }
            currentPlayer.seekTo(position)
        }

        currentPlayer.playWhenReady = true
        mediaSession.setPlaybackState(
            getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_PLAYING, currentPlayer.currentPosition, 1f)
                .build()
        )
        startTrackingPosition(currentPlayer)
    }

    override fun enqueue(mediaAsset: MediaAsset) {
        val nextPlayer = getPlayerForAsset(mediaAsset)
        if (nextPlayer.assetId != mediaAsset.id) {
            nextPlayer.prepare(mediaAsset, false)
        }
    }

    override fun pause() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        setPlaybackState(
            getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_PAUSED, currentPlayer.currentPosition, 1f)
                .build()
        )
    }

    override fun stop() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        currentPlayer.assetId = null
        setPlaybackState(
            getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1f)
                .build()
        )
    }

    override fun seekTo(position: Long, isVideo: Boolean) {
        if (currentPlayer.supportsSeeking) {
            val bufferPosition = currentPlayer.bufferedPosition
            val seekPosition =
                if (bufferPosition < 0 || position < bufferPosition || isVideo) position else bufferPosition
            currentPlayer.seekTo(seekPosition)
            setPlaybackPosition(seekPosition)
        }
    }

    @Suppress("DEPRECATION")
    override fun requestAudioFocus() {
        // new method requestAudioFocus(AudioFocusRequest) requires API 26
        audioManager().abandonAudioFocus(audioFocusChangeListener)
        audioManager().requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    override fun setVolume(volume: Float) {
        currentPlayer.volume = volume
    }

    override fun setQueueTitle(title: List<LocalisedString>, isStation: Boolean) {
        val queueTitle =
            if (isStation) title.valueForSystemLanguage(this) + " - " + getString(R.string.station_description)
            else title.valueForSystemLanguage(this)
        mediaSession.setQueueTitle(queueTitle)
    }

    override fun setAvailableActions(actions: List<Long>) {
        var availableActions: Long = 0
        actions.forEach {
            availableActions = availableActions or it
        }

        setPlaybackState(
            getPlaybackStateBuilder()
                .setActions(availableActions)
                .build()
        )
    }

    override fun setPlayMode(isShuffle: Boolean, repeatMode: Int) {
        mediaSession.setShuffleMode(
            if (isShuffle) PlaybackStateCompat.SHUFFLE_MODE_ALL else PlaybackStateCompat.SHUFFLE_MODE_NONE
        )
        mediaSession.setRepeatMode(
            when (repeatMode) {
                PlayQueue.REPEAT_MODE_NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
                PlayQueue.REPEAT_MODE_ONE -> PlaybackStateCompat.REPEAT_MODE_ONE
                PlayQueue.REPEAT_MODE_ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
                else -> PlaybackStateCompat.REPEAT_MODE_NONE
            }
        )
        setPlaybackState(getPlaybackStateBuilder().build())
    }

    override fun setMetadata(
        title: String,
        type: MediaType,
        id: String,
        artist: String?,
        duration: Long,
        artUri: List<LocalisedString>?,
        albumArtUri: List<LocalisedString>?,
        rating: Rating?,
        clickUri: String?,
        isAd: Boolean,
        adVastXML: String?,
        hideDialog: Boolean,
        hasLyrics: Boolean,
        isVideo: Boolean,
        isExplicit: Boolean,
        isFirstTrack: Boolean,
        isLastTrack: Boolean
    ) {
        val builder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .putString(MusicPlayerController.METADATA_KEY_TYPE, type.name)
            .putString(MusicPlayerController.METADATA_KEY_CLICK_URI, clickUri)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
            .putLong(MediaMetadataCompat.METADATA_KEY_ADVERTISEMENT, if (isAd) 1 else 0)
            .putLong(MusicPlayerController.METADATA_KEY_HIDE_DIALOG, if (hideDialog) 1 else 0)
            .putLong(MusicPlayerController.METADATA_KEY_HAS_LYRICS, if (hasLyrics) 1 else 0)
            .putLong(MusicPlayerController.METADATA_KEY_IS_VIDEO, if (isVideo) 1 else 0)
            .putLong(MusicPlayerController.METADATA_KEY_IS_EXPLICIT, if (isExplicit) 1 else 0)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MusicPlayerController.METADATA_KEY_AD_VAST_XML, adVastXML)
            .putLong(MusicPlayerController.METADATA_KEY_IS_FIRST_TRACK, if (isFirstTrack) 1 else 0)
            .putLong(MusicPlayerController.METADATA_KEY_IS_LAST_TRACK, if (isLastTrack) 1 else 0)

        artist?.let {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it)
        }

        albumArtUri?.let {
            builder.putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                albumArtUri.valueForSystemLanguage(this)
            )
        }

        artUri?.let {
            builder.putString(
                MediaMetadataCompat.METADATA_KEY_ART_URI,
                artUri.valueForSystemLanguage(this)
            )
        }

        val mediaRating = when (rating) {
            Rating.LIKED -> RatingCompat.newThumbRating(true)
            Rating.DISLIKED -> RatingCompat.newThumbRating(false)
            else -> RatingCompat.newUnratedRating(RatingCompat.RATING_THUMB_UP_DOWN)
        }
        builder.putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, mediaRating)
        mediaSession.setMetadata(builder.build())
    }

    override fun resetMetaData() {
        cacheServicePlayerRepository.clearServiceRunning()
        mediaSession.setMetadata(null)
    }

    override fun setPlaybackPosition(position: Long) {
        runOnUiThread {
            if (lastPlaybackPosition > position) {
                lastPlaybackPosition = 0
            }
            listenedDuration += position - lastPlaybackPosition
            lastPlaybackPosition = position
            if (listenedDuration >= PROGRESS_UPDATE_DURATION) {
                listenedDuration = 0
            }

            mediaSession.controller?.playbackState?.let {
                // prevent seeking will trigger end of queue toast
                val state =
                    if (it.state == PlaybackStateCompat.STATE_ERROR &&
                        it.errorCode == PlaybackStateCompat.ERROR_CODE_END_OF_QUEUE
                    )
                        PlaybackStateCompat.STATE_PAUSED
                    else
                        it.state

                setPlaybackState(
                    getPlaybackStateBuilder()
                        .setState(state, position, 1f)
                        .build()
                )
            }
        }
    }

    override fun setPlaybackBuffering() {
        setPlaybackState(
            getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_BUFFERING, 0, 1f)
                .build()
        )
    }

    override fun setEndOfQueue() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        val playbackState = getPlaybackStateBuilder()
            .setState(PlaybackStateCompat.STATE_ERROR, 0, 1f)
            .setErrorMessage(
                PlaybackStateCompat.ERROR_CODE_END_OF_QUEUE,
                getString(R.string.playback_error_end_of_queue)
            )
        setPlaybackState(playbackState.build())
    }

    override fun setPlayLimitReached() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        val playbackState = getPlaybackStateBuilder()
            .setState(PlaybackStateCompat.STATE_ERROR, 0, 1f)
            .setErrorMessage(
                PlaybackStateCompat.ERROR_CODE_PREMIUM_ACCOUNT_REQUIRED,
                getString(R.string.playback_error_play_limit_reached)
            )
        setPlaybackState(playbackState.build())
    }

    override fun setSkipLimitReached() {
        mediaSession.setMetadata(
            getMetadataBuilder()
                .putLong(MusicPlayerController.METADATA_KEY_SKIP_LIMIT_REACHED, 1).build()
        )
    }

    override fun setSkipAvailable() {
        mediaSession.setMetadata(
            getMetadataBuilder()
                .putLong(MusicPlayerController.METADATA_KEY_SKIP_LIMIT_REACHED, 0).build()
        )
    }

    override fun setUnknownError() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        val playbackState = getPlaybackStateBuilder()
            .setState(PlaybackStateCompat.STATE_ERROR, 0, 1f)
            .setErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR, null)
        setPlaybackState(playbackState.build())
    }

    override fun setStreamingDisabled() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        val playbackState = getPlaybackStateBuilder()
            .setState(PlaybackStateCompat.STATE_ERROR, 0, 1f)
            .setErrorMessage(
                PlaybackStateCompat.ERROR_CODE_APP_ERROR,
                getString(R.string.playback_error_streaming_disabled)
            )
        setPlaybackState(playbackState.build())
    }

    override fun setUpgradeRequired() {
        stopTrackingPosition()
        currentPlayer.playWhenReady = false
        val playbackState = getPlaybackStateBuilder()
            .setState(PlaybackStateCompat.STATE_ERROR, 0, 1f)
            .setErrorMessage(PlaybackStateCompat.ERROR_CODE_PREMIUM_ACCOUNT_REQUIRED, null)
        setPlaybackState(playbackState.build())
    }

    override fun setQueueUpdated() {
        mediaSession.sendSessionEvent(MusicPlayerController.SESSION_EVENT_QUEUE_CHANGE, null)
    }

    // endregion

    private fun play(mediaAsset: MediaAsset, player: BasePlayer) {
        cacheServicePlayerRepository.saveServiceRunning()
        currentPlayer.removeListener(playerEventListener)
        currentPlayer.playWhenReady = false
        currentPlayer.surface = null

        currentPlayer = player
        previousPlaybackState = player.playbackState
        currentPlayer.addListener(playerEventListener)

        // We are playing a pre-buffered asset
        if (
            (
                currentPlayer.playbackState != Player.STATE_ENDED || currentPlayer.supportsSeeking
                ) && currentPlayer.assetId == mediaAsset.id
        ) {
            setPlaybackState(
                getPlaybackStateBuilder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, currentPlayer.currentPosition, 1f)
                    .build()
            )
            if (currentPlayer.supportsSeeking) {
                currentPlayer.seekTo(0, 0)
            }
            currentPlayer.playWhenReady = true
        } else { // new asset
            setPlaybackState(
                getPlaybackStateBuilder()
                    .setState(
                        PlaybackStateCompat.STATE_BUFFERING,
                        currentPlayer.currentPosition,
                        1f
                    )
                    .build()
            )
            currentPlayer.prepare(mediaAsset, true)
        }

        startTrackingPosition(currentPlayer)
    }

    private fun startTrackingPosition(player: BasePlayer) {
        stopTrackingPosition()
        positionScheduler = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            {
                runOnUiThread {
                    controller.onPlaybackTick(player.currentPosition)
                    if (mediaSession.controller?.metadata?.duration == META_DURATION &&
                        player.duration >= 0
                    ) {
                        mediaSession.setMetadata(
                            getMetadataBuilder()
                                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.duration)
                                .build()
                        )
                    }
                }
            },
            0, TIME_DURATION, TimeUnit.MILLISECONDS
        )
    }

    private fun stopTrackingPosition() {
        positionScheduler?.cancel(true)
        positionScheduler = null
    }

    // If we havent started playing a playlist yet, grab the first player
    // Otherwise, try to get the player playing the desired track
    // or failing that, the first one that isnt playing the current track
    private fun getPlayerForAsset(asset: MediaAsset): BasePlayer {
        val players =
            if (asset is MusicPlayerController.TrackMediaAsset && asset.track.isVideo)
                videoPlayers else trackPlayers
        return players.firstOrNull { it.assetId == asset.id }
            ?: players.firstOrNull { it.assetId == null || it != currentPlayer }
            ?: players.first()
    }

    private fun getPlaybackStateBuilder(): PlaybackStateCompat.Builder {
        val playbackState = mediaSession.controller?.playbackState
        return if (playbackState != null) {
            PlaybackStateCompat.Builder(playbackState)
        } else {
            PlaybackStateCompat.Builder()
        }
    }

    private fun getMetadataBuilder(): MediaMetadataCompat.Builder {
        val metadata = mediaSession.controller?.metadata
        return if (metadata != null) {
            MediaMetadataCompat.Builder(metadata)
        } else {
            MediaMetadataCompat.Builder()
        }
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null &&
                intent.action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            ) {
                controller.onBecomingNoisy()
            }
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> controller.onLostAudioFocus()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> controller.onLostAudioFocus(true)
            AudioManager.AUDIOFOCUS_GAIN -> controller.onGainedAudioFocus()
        }
    }

    private val playerEventListener = object : Player.Listener {

        override fun onPlayerError(error: PlaybackException) {
            if (currentPlayer is RadioPlayer) {
                (currentPlayer as RadioPlayer).reInitializePlayer()
            } else {
                controller.onPlaybackError(error)
            }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            // Ignore if state hasn't changed
            if (previousPlaybackState == playbackState) {
                return
            }

            previousPlaybackState = playbackState
            if (playbackState == Player.STATE_ENDED) {
                controller.onPlaybackEnded()
            } else if (playbackState == Player.STATE_READY && playWhenReady) {
                val current = mediaSession.controller?.playbackState ?: return

                if (current.state != PlaybackStateCompat.STATE_PLAYING) {
                    setPlaybackState(
                        PlaybackStateCompat.Builder(current)
                            .setState(
                                PlaybackStateCompat.STATE_PLAYING,
                                currentPlayer.currentPosition,
                                1f
                            )
                            .build()
                    )
                }
            }
        }
    }

    private val sourceEventListener = object : MediaSourceEventListener {
        override fun onLoadCompleted(
            dataType: Int,
            mediaPeriodId: MediaSource.MediaPeriodId?,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData
        ) {
            Timber.d("--------> Source Load Completed")
            controller.onSourceLoaded()
        }
    }

    private val drmSessionEventListener = object : DrmSessionEventListener {}

    private fun setPlaybackState(state: PlaybackStateCompat) {
        runOnUiThread {
            mediaSession.setPlaybackState(state)
        }
    }

    private fun startNotification() {
        if (::mediaSessionServiceNotification.isInitialized) {
            mediaSessionServiceNotification.startNotification()
        } else {
            mediaSessionServiceNotification =
                MediaSessionServiceNotification(this, mediaSession, imageManager, config)
        }
    }
}
