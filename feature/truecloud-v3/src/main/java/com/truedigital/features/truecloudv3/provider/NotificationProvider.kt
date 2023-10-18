package com.truedigital.features.truecloudv3.provider

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.tdg.truecloud.R
import javax.inject.Inject

interface NotificationProvider {
    fun getNotification(id: String, pendingIntent: PendingIntent): Notification
}

class NotificationProviderImpl @Inject constructor(
    private val context: Context
) : NotificationProvider {
    override fun getNotification(id: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(context, id)
            .setContentTitle(context.getString(R.string.true_cloudv3_notification_title))
            .setContentText(context.getString(R.string.true_cloudv3_notification_text))
            .setContentIntent(pendingIntent)
            .build()
    }
}
