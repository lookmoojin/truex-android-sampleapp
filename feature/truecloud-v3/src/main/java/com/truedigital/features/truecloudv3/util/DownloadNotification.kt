package com.truedigital.features.truecloudv3.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tdg.truecloud.R
import com.truedigital.features.truecloudv3.receiver.DownloadNotificationReceiver
import com.truedigital.foundation.NotificationChannelInfo.Companion.TRUE_CLOUD_DOWNLOAD_CHANNEL_ID
import com.truedigital.foundation.NotificationChannelInfo.Companion.TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME
import org.jetbrains.annotations.VisibleForTesting

class DownloadNotification(val context: Context) {
    private var notificationManagerCompat: NotificationManagerCompat? = null
    private var builder: NotificationCompat.Builder

    companion object {
        private const val MAX_PROGRESS = 100
        private const val MIN_PROGRESS = 0
        private const val REQUEST_CODE = 0
        private const val KEY = "key"
        private const val PATH = "file_path"
        private const val NOTIFICATION_ID = "download_notification_id"
        private const val DOWNLOAD_PAUSE = "download_pause"
        private const val DOWNLOAD_RESUME = "download_resume"
        private const val PAUSE = "pause"
        private const val RESUME = "resume"
    }

    init {
        builder = NotificationCompat.Builder(context, createNotificationChannel(context))
    }

    fun getPauseAction(notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, DownloadNotificationReceiver::class.java)
        intent.putExtra(NOTIFICATION_ID, notificationId)
        intent.action = DOWNLOAD_PAUSE
        intent.flags = notificationId
        val broadcastPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_baseline_pause_24,
            PAUSE,
            broadcastPendingIntent
        )
    }

    fun getResumeAction(notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, DownloadNotificationReceiver::class.java)
        intent.putExtra(NOTIFICATION_ID, notificationId)
        intent.action = DOWNLOAD_RESUME
        intent.flags = notificationId
        val broadcastPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_baseline_play_arrow_24,
            RESUME,
            broadcastPendingIntent
        )
    }

    @SuppressLint("MissingPermission")
    fun show(notificationId: Int, key: String?, path: String?) {
        builder.setContentTitle(context.getString(R.string.true_cloudv3_noti_title_downloading))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_baseline_cloud_download_24
                )
            )
            .addAction(getPauseAction(notificationId))
            .setSmallIcon(R.mipmap.ic_notification_small)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addExtras(
                Bundle().apply {
                    putString(KEY, key)
                    putString(PATH, path)
                }
            )
            .setProgress(MAX_PROGRESS, MIN_PROGRESS, true)

        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun updateProgress(notificationId: Int, progress: Int) {
        builder.setContentText(
            context.getString(
                R.string.true_cloudv3_noti_download_progress,
                progress.toString()
            )
        )
            .setProgress(MAX_PROGRESS, progress, false)
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun downloadComplete(notificationId: Int) {
        builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_download_complete))
            .setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
            .setOngoing(false)
            .clearActions()
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun downloadFailed(notificationId: Int) {
        builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_download_failed))
            .setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
            .clearActions()
            .setOngoing(false)
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun downloadPause(notificationId: Int) {
        builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_download_pause))
            .clearActions()
            .addAction(getResumeAction(notificationId))
            .setOngoing(false)
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun downloadResume(notificationId: Int) {
        builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_downloading))
            .clearActions()
            .addAction(getPauseAction(notificationId))
            .setOngoing(false)
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @VisibleForTesting
    fun createNotificationChannel(context: Context): String {
        val notificationChannel =
            NotificationChannel(
                TRUE_CLOUD_DOWNLOAD_CHANNEL_ID,
                TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat?.createNotificationChannel(notificationChannel)

        return TRUE_CLOUD_DOWNLOAD_CHANNEL_ID
    }

    private fun checkPostNotificationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
}
