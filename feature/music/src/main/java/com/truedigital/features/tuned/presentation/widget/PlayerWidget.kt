package com.truedigital.features.tuned.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.RemoteViews
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.artist
import com.truedigital.features.tuned.common.extensions.isVideo
import com.truedigital.features.tuned.common.extensions.launchPendingIntent
import com.truedigital.features.tuned.common.extensions.mediaType
import com.truedigital.features.tuned.common.extensions.pendingIntentForAction
import com.truedigital.features.tuned.common.extensions.skipLimitReached
import com.truedigital.features.tuned.common.extensions.title
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController

class PlayerWidget : AppWidgetProvider() {
    companion object {
        fun triggerUpdate(context: Context) {
            val intent = Intent(context, PlayerWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, PlayerWidget::class.java))
            )
            context.sendBroadcast(intent)
        }
    }

    private var knownWidgetIds = emptyList<Int>()
    private var prevTitles = ""
    private var prevRating: RatingCompat? = null
    private var lastPlaybackActions: Long = -1L

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        MusicComponent.getInstance().getApplicationComponent().getMediaSession().controller?.let {
            updateWidgets(context, it)
        }
    }

    override fun onEnabled(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.widget_player)
        if (MusicComponent.getInstance().getApplicationComponent().getConfiguration().enableFavourites) {
            views.setViewVisibility(R.id.likeButton, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.likeButton, View.GONE)
        }
    }

    private fun updateWidgets(context: Context, controller: MediaControllerCompat) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, PlayerWidget::class.java)
        val widgetIds = widgetManager.getAppWidgetIds(componentName).toList()
        val shouldDisable =
            controller.metadata?.isVideo ?: false || controller.metadata?.mediaType == MediaType.AD
        val isPlayingStation = controller.metadata?.mediaType == MediaType.ARTIST ||
            controller.metadata?.mediaType == MediaType.SINGLE_ARTIST ||
            controller.metadata?.mediaType == MediaType.PRESET

        // New
        widgetIds.minus(knownWidgetIds).forEach {
            val views = RemoteViews(context.packageName, R.layout.widget_player)

            views.setOnClickPendingIntent(R.id.widgetPlayerContainer, context.launchPendingIntent())

            if (controller.metadata == null ||
                (
                    controller.playbackState != null &&
                        (
                            controller.playbackState.state == PlaybackStateCompat.STATE_STOPPED ||
                                controller.playbackState.state == PlaybackStateCompat.STATE_ERROR
                            )
                    )
            ) {
                views.setTextViewText(R.id.widgetTitle, context.getString(R.string.app_name))
                views.setTextViewText(
                    R.id.widgetSubtitle,
                    context.getString(R.string.widget_description)
                )

                views.setImageViewResource(
                    R.id.likeButton,
                    R.drawable.music_ic_thumb_up_hollow_disabled
                )
                views.setOnClickPendingIntent(R.id.likeButton, null)
                views.setImageViewResource(
                    R.id.previousButton,
                    R.drawable.music_ic_skip_previous_disabled
                )
                views.setOnClickPendingIntent(R.id.previousButton, null)
            } else {
                setTitles(views, controller)

                val userRating =
                    controller.metadata.getRating(MediaMetadataCompat.METADATA_KEY_USER_RATING)
                setVoteControls(userRating, views, context, shouldDisable, isPlayingStation)
            }

            if (controller.playbackState == null || shouldDisable) {
                views.setImageViewResource(R.id.skipButton, R.drawable.music_ic_skip_next_disabled)
                views.setOnClickPendingIntent(R.id.skipButton, null)

                views.setImageViewResource(R.id.playButton, R.drawable.music_ic_play_disabled)
                views.setOnClickPendingIntent(R.id.playButton, null)
            } else {
                setPlaybackControls(context, views, controller)
            }

            widgetManager.updateAppWidget(it, views)
        }

        // updated
        widgetIds.union(knownWidgetIds).forEach {
            val views = RemoteViews(context.packageName, R.layout.widget_player)

            views.setOnClickPendingIntent(R.id.widgetPlayerContainer, context.launchPendingIntent())

            if (controller.metadata == null ||
                (
                    controller.playbackState != null &&
                        (
                            controller.playbackState.state == PlaybackStateCompat.STATE_STOPPED ||
                                controller.playbackState.state == PlaybackStateCompat.STATE_ERROR
                            )
                    )
            ) {
                views.setTextViewText(R.id.widgetTitle, context.getString(R.string.app_name))
                views.setTextViewText(
                    R.id.widgetSubtitle,
                    context.getString(R.string.widget_description)
                )
            } else {
                val titles =
                    "${controller.metadata.title}|${controller.metadata.artist}|${controller.queueTitle}"
                if (prevTitles != titles) {
                    setTitles(views, controller)
                    prevTitles = titles

                    setVoteControls(null, views, context, shouldDisable, isPlayingStation)
                    prevRating = null
                }

                val userRating =
                    controller.metadata.getRating(MediaMetadataCompat.METADATA_KEY_USER_RATING)
                if (prevRating != userRating) {
                    setVoteControls(userRating, views, context, shouldDisable, isPlayingStation)
                }
            }

            if (controller.playbackState == null || shouldDisable) {
                views.setImageViewResource(R.id.skipButton, R.drawable.music_ic_skip_next_disabled)
                views.setOnClickPendingIntent(R.id.skipButton, null)

                views.setImageViewResource(R.id.playButton, R.drawable.music_ic_play_disabled)
                views.setOnClickPendingIntent(R.id.playButton, null)
            } else {
                if (controller.playbackState.actions != lastPlaybackActions) {
                    setPlaybackControls(context, views, controller)
                }
            }

            widgetManager.updateAppWidget(it, views)
        }

        knownWidgetIds = widgetIds
    }

    private fun setVoteControls(
        userRating: RatingCompat?,
        views: RemoteViews,
        context: Context,
        shouldDisable: Boolean,
        isPlayingStation: Boolean
    ) {
        when {
            shouldDisable -> {
                // disable last visible icon on the left side of the widget
                disableThumbAndPrevious(views)
            }
            !isPlayingStation -> showPreviousActive(context, views)
            MusicComponent.getInstance().getApplicationComponent().getConfiguration().enableFavourites -> {
                if (userRating != null && userRating.isRated && userRating.isThumbUp) {
                    showThumbSelected(context, views)
                } else {
                    showThumbUnselected(context, views)
                }
            }
            else -> hideThumbAndPrevious(views)
        }
    }

    private fun setPlaybackControls(
        context: Context,
        views: RemoteViews,
        controller: MediaControllerCompat
    ) {
        if (controller.playbackState.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT ==
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT &&
            controller.playbackState.state != PlaybackStateCompat.STATE_STOPPED &&
            controller.playbackState.state != PlaybackStateCompat.STATE_ERROR
        ) {
            val skipIconResource =
                if (controller.metadata?.skipLimitReached == true) {
                    R.drawable.music_ic_skip_next_disabled
                } else {
                    R.drawable.music_ic_skip_next
                }
            views.setImageViewResource(R.id.skipButton, skipIconResource)
            views.setOnClickPendingIntent(
                R.id.skipButton,
                context.pendingIntentForAction(MusicPlayerController.ACTION_SKIP_NEXT)
            )
        } else {
            views.setImageViewResource(R.id.skipButton, R.drawable.music_ic_skip_next_disabled)
            views.setOnClickPendingIntent(R.id.skipButton, null)
        }

        when (controller.playbackState.state) {
            PlaybackStateCompat.STATE_PAUSED -> {
                views.setImageViewResource(R.id.playButton, R.drawable.music_ic_play)
                views.setOnClickPendingIntent(
                    R.id.playButton,
                    context.pendingIntentForAction(MusicPlayerController.ACTION_PLAY)
                )
            }
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                views.setImageViewResource(R.id.playButton, R.drawable.music_ic_pause)
                views.setOnClickPendingIntent(
                    R.id.playButton,
                    context.pendingIntentForAction(MusicPlayerController.ACTION_PAUSE)
                )
            }
            PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.STATE_ERROR -> {
                views.setImageViewResource(R.id.playButton, R.drawable.music_ic_play_disabled)
                views.setOnClickPendingIntent(R.id.playButton, null)
            }
        }
    }

    private fun setTitles(views: RemoteViews, controller: MediaControllerCompat) {
        views.setTextViewText(R.id.widgetTitle, controller.metadata.title)

        val artist = controller.metadata.artist
        views.setTextViewText(
            R.id.widgetSubtitle,
            when {
                artist == null -> ""
                controller.queueTitle == null -> artist
                else -> "$artist - ${controller.queueTitle}"
            }
        )
    }

    private fun showThumbSelected(context: Context, views: RemoteViews) {
        views.setViewVisibility(R.id.likeButton, View.VISIBLE)
        views.setViewVisibility(R.id.previousButton, View.GONE)
        views.setImageViewResource(R.id.likeButton, R.drawable.music_ic_thumb_up_solid_highlighted)
        views.setOnClickPendingIntent(
            R.id.likeButton,
            context.pendingIntentForAction(MusicPlayerController.ACTION_REMOVE_RATING)
        )
    }

    private fun showThumbUnselected(context: Context, views: RemoteViews) {
        views.setViewVisibility(R.id.likeButton, View.VISIBLE)
        views.setViewVisibility(R.id.previousButton, View.GONE)
        views.setImageViewResource(R.id.likeButton, R.drawable.music_ic_thumb_up_hollow)
        views.setOnClickPendingIntent(
            R.id.likeButton,
            context.pendingIntentForAction(MusicPlayerController.ACTION_LIKE)
        )
    }

    private fun showPreviousActive(context: Context, views: RemoteViews) {
        views.setViewVisibility(R.id.likeButton, View.GONE)
        views.setOnClickPendingIntent(R.id.likeButton, null)
        views.setViewVisibility(R.id.previousButton, View.VISIBLE)
        views.setImageViewResource(R.id.previousButton, R.drawable.music_ic_skip_previous)
        views.setOnClickPendingIntent(
            R.id.previousButton,
            context.pendingIntentForAction(MusicPlayerController.ACTION_SKIP_PREVIOUS)
        )
    }

    private fun disableThumbAndPrevious(views: RemoteViews) {
        views.setImageViewResource(R.id.likeButton, R.drawable.music_ic_thumb_up_hollow_disabled)
        views.setOnClickPendingIntent(R.id.likeButton, null)
        views.setImageViewResource(R.id.previousButton, R.drawable.music_ic_skip_previous_disabled)
        views.setOnClickPendingIntent(R.id.previousButton, null)
    }

    private fun hideThumbAndPrevious(views: RemoteViews) {
        views.setViewVisibility(R.id.likeButton, View.GONE)
        views.setViewVisibility(R.id.previousButton, View.GONE)
    }
}
