package com.truedigital.features.truecloudv3.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.truedigital.features.truecloudv3.service.TrueCloudV3Service

class DownloadNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val DOWNLOAD_PAUSE = "download_pause"
        private const val DOWNLOAD_RESUME = "download_resume"
        private const val NOTIFICATION_ID = "notification_id"
        private const val DEFAULT_ID = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(NOTIFICATION_ID, DEFAULT_ID)
        when (intent.action) {
            DOWNLOAD_PAUSE -> {
                TrueCloudV3Service.callPause(id)
            }
            DOWNLOAD_RESUME -> {
                TrueCloudV3Service.callResume(id)
            }
        }
    }
}
