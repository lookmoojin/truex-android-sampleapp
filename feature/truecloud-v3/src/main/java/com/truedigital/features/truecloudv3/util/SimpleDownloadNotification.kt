package com.truedigital.features.truecloudv3.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.truedigital.features.truecloudv3.R
import com.truedigital.foundation.NotificationChannelInfo.Companion.TRUE_CLOUD_DOWNLOAD_CHANNEL_ID
import com.truedigital.foundation.NotificationChannelInfo.Companion.TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME
import org.jetbrains.annotations.VisibleForTesting

class SimpleDownloadNotification(val context: Context) {
    private var notificationManagerCompat: NotificationManagerCompat? = null
    private var builder: NotificationCompat.Builder

    companion object {
        private const val MAX_PROGRESS = 100
        private const val MIN_PROGRESS = 0
        private const val KEY = "key"
        private const val PATH = "file_path"
        private const val EMPTY_STRING = ""
    }

    init {
        builder = NotificationCompat.Builder(context, createNotificationChannel(context))
    }

    fun show(notificationId: Int, key: String?, path: String?) {
        builder.setContentTitle(context.getString(R.string.true_cloudv3_noti_title_downloading))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_baseline_cloud_download_24
                )
            )
            .setSmallIcon(com.truedigital.core.R.mipmap.ic_notification_small)
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

    fun updateProgress(notificationId: Int, progress: Int) {
        if (progress == MAX_PROGRESS) {
            builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_download_complete))
                .setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
                .setOngoing(false)
                .clearActions()
            if (checkPostNotificationPermission()) return
            notificationManagerCompat?.notify(notificationId, builder.build())
        } else {
            builder.setContentText(
                context.getString(
                    R.string.true_cloudv3_noti_download_progress,
                    progress.toString()
                )
            )
                .setProgress(MAX_PROGRESS, progress, false)
        }
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    fun downloadComplete(notificationId: Int) {
        builder.setContentText(context.getString(R.string.true_cloudv3_noti_title_download_complete))
            .setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
            .setOngoing(false)
            .clearActions()
        if (checkPostNotificationPermission()) return
        notificationManagerCompat?.notify(notificationId, builder.build())
    }

    @VisibleForTesting
    fun createNotificationChannel(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    TRUE_CLOUD_DOWNLOAD_CHANNEL_ID,
                    TRUE_CLOUD_DOWNLOAD_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat?.createNotificationChannel(notificationChannel)
            TRUE_CLOUD_DOWNLOAD_CHANNEL_ID
        } else {
            EMPTY_STRING
        }
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
