package com.truedigital.features.tuned.service.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.media.app.NotificationCompat.MediaStyle
import com.truedigital.features.music.MusicInitializer
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.albumArtUri
import com.truedigital.features.tuned.common.extensions.artUri
import com.truedigital.features.tuned.common.extensions.artist
import com.truedigital.features.tuned.common.extensions.launchPendingIntent
import com.truedigital.features.tuned.common.extensions.mediaType
import com.truedigital.features.tuned.common.extensions.pendingIntentForAction
import com.truedigital.features.tuned.common.extensions.skipLimitReached
import com.truedigital.features.tuned.common.extensions.title
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.foundation.NotificationChannelInfo.Companion.MUSIC_CHANNEL_ID
import com.truedigital.foundation.NotificationChannelInfo.Companion.MUSIC_CHANNEL_NAME

class MediaSessionServiceNotification(
    val service: Service,
    val mediaSession: MediaSessionCompat,
    val imageManager: ImageManager,
    val config: Configuration
) {
    companion object {
        private const val NOTIFICATION_ID = 10022
    }

    private lateinit var currentNotification: Notification
    private var loadingArtUri: String? = null
    private var loadedArtUri: String? = null
    private var stationImage: Bitmap? = null

    private var serviceIsForeground = false
    private var previousPlaybackState = -1
    private var previousActions = -1L

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            // as when the app is not in foreground,
            // we need to use startForegroundService to get away the new background process limitation
            // which requires us to call startForeground within 10 seconds
            if (!MusicInitializer.appIsInForeground && !serviceIsForeground) {
                updateNotification()
            }

            if (state != null &&
                (previousPlaybackState != state.state || previousActions != state.actions)
            ) {
                updateNotification()
                previousPlaybackState = state.state
                previousActions = state.actions
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            if (metadata == null) {
                service.stopForeground(true)
            } else {
                updateNotification()
            }
        }
    }

    init {
        mediaSession.controller.registerCallback(mediaControllerCallback)
        initNotification()
    }

    private fun initNotification() {
        if (::currentNotification.isInitialized.not()) {
            val builder = NotificationCompat.Builder(service, createNotificationChannel())
                .setContentTitle(service.getString(R.string.app_name))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        service.resources,
                        R.drawable.placeholder_trueidwhite_square
                    )
                )
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setShowWhen(false)
                .setContentIntent(service.launchPendingIntent())
                .addAction(Action(R.drawable.music_ic_skip_previous_disabled, "previous", null))
                .addAction(Action(R.drawable.music_ic_play_disabled, "play", null))
                .addAction(Action(R.drawable.music_ic_skip_next_disabled, "next", null))
                .setStyle(getStyle(mediaSession).setShowActionsInCompactView(0, 1, 2))
            currentNotification = builder.build()
        }
        service.startForeground(NOTIFICATION_ID, currentNotification)
        service.stopForeground(false)
        serviceIsForeground = false
    }

    private fun updateNotification() {
        val controller = mediaSession.controller
        val playbackState = controller.playbackState ?: return
        val metadata = controller.metadata ?: return

        // if require remove notification when playing video,
        // need to use startService instead of startForegroundService, delay the init notification method

        val artUri = when (metadata.mediaType) {
            MediaType.PRESET -> metadata.artUri
            else -> metadata.albumArtUri
        }

        if (artUri != loadedArtUri)
            stationImage =
                BitmapFactory.decodeResource(
                    service.resources,
                    R.drawable.placeholder_trueidwhite_square
                )

        val builder = NotificationCompat.Builder(service, createNotificationChannel())
            .setContentTitle(metadata.title)
            .setSubText(controller.queueTitle)
            .setLargeIcon(stationImage)
            .setSmallIcon(R.mipmap.ic_notification_small)
            .setShowWhen(false)
            .setContentIntent(service.launchPendingIntent())

        var artist = metadata.artist
        artist?.let {
            if (it.isEmpty()) artist = service.getString(R.string.various_artists_title)
            builder.setContentText(it)
        }

        when (metadata.mediaType) {
            MediaType.VIDEO,
            MediaType.RADIO -> {
                builder.addAction(getPlayPauseAction(playbackState))
                builder.setStyle(getStyle(mediaSession).setShowActionsInCompactView(0))
            }

            MediaType.AD -> {
                builder.addAction(Action(R.drawable.music_ic_pause_disabled, "pause", null))
                builder.addAction(Action(R.drawable.music_ic_skip_next_disabled, "next", null))
                builder.setStyle(getStyle(mediaSession).setShowActionsInCompactView(0, 1))
            }

            else -> {
                builder.addAction(getSkipPreviousAction(playbackState))
                builder.addAction(getPlayPauseAction(playbackState))
                builder.addAction(getSkipNextAction(playbackState, metadata))
                builder.setStyle(getStyle(mediaSession).setShowActionsInCompactView(0, 1, 2))
            }
        }

        if (!artUri.isNullOrEmpty() && artUri != loadedArtUri) {
            val size =
                service.resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
            loadingArtUri = artUri
            imageManager.init(service)
                .load(artUri)
                .options(size, isCustomUrl = isRadioType().not())
                .intoBitmap {
                    if (artUri == loadingArtUri) {
                        stationImage = it
                        builder.setLargeIcon(stationImage)
                        render(playbackState, builder.build())
                        loadedArtUri = artUri
                    }
                }
        } else if (artUri.isNullOrEmpty()) {
            loadedArtUri = ""
        }
        render(playbackState, builder.build())
    }

    private fun isRadioType(): Boolean {
        return mediaSession.controller.metadata.mediaType == MediaType.RADIO
    }

    private fun createNotificationChannel(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    MUSIC_CHANNEL_ID,
                    MUSIC_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager =
                service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            MUSIC_CHANNEL_ID
        } else {
            ""
        }
    }

    private fun render(playbackState: PlaybackStateCompat, notification: Notification) {
        currentNotification = notification
        // Play as foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            service.startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            service.startForeground(NOTIFICATION_ID, notification)
        }
        serviceIsForeground = true

        // allow swipe to dismiss when its not playing
        if (playbackState.state == PlaybackStateCompat.STATE_PAUSED ||
            playbackState.state == PlaybackStateCompat.STATE_ERROR ||
            playbackState.state == PlaybackStateCompat.STATE_STOPPED ||
            playbackState.state == PlaybackStateCompat.STATE_NONE
        ) {
            service.stopForeground(false)
            serviceIsForeground = false
        }
    }

    private fun getStyle(mediaSession: MediaSessionCompat): MediaStyle =
        MediaStyle().setMediaSession(mediaSession.sessionToken)

    private fun getSkipPreviousAction(playbackState: PlaybackStateCompat) =
        if (playbackState.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS ==
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        ) {
            Action(
                R.drawable.music_ic_skip_previous,
                "previous",
                service.pendingIntentForAction(MusicPlayerController.ACTION_SKIP_PREVIOUS)
            )
        } else {
            Action(R.drawable.music_ic_skip_previous_disabled, "previous", null)
        }

    private fun getPlayPauseAction(playbackState: PlaybackStateCompat) =
        if (playbackState.state == PlaybackStateCompat.STATE_PLAYING ||
            playbackState.state == PlaybackStateCompat.STATE_BUFFERING
        ) {
            Action(
                R.drawable.music_ic_pause,
                "pause",
                service.pendingIntentForAction(MusicPlayerController.ACTION_PAUSE)
            )
        } else if (playbackState.state == PlaybackStateCompat.STATE_STOPPED) {
            Action(R.drawable.music_ic_play_disabled, "play", null)
        } else {
            Action(
                R.drawable.music_ic_play,
                "play",
                service.pendingIntentForAction(MusicPlayerController.ACTION_PLAY)
            )
        }

    private fun getSkipNextAction(
        playbackState: PlaybackStateCompat,
        metadata: MediaMetadataCompat
    ) =
        if (playbackState.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT ==
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        ) {
            val skipIconResource =
                if (metadata.skipLimitReached) {
                    R.drawable.music_ic_skip_next_disabled
                } else {
                    R.drawable.music_ic_skip_next
                }
            Action(
                skipIconResource,
                "next",
                service.pendingIntentForAction(MusicPlayerController.ACTION_SKIP_NEXT)
            )
        } else {
            Action(R.drawable.music_ic_skip_next_disabled, "next", null)
        }

    fun startNotification() {
        initNotification()
    }

    fun clearPlayerNotification() {
        val notificationManager =
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
