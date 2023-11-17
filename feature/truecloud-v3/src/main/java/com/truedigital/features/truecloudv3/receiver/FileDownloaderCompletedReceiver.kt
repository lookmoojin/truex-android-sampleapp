package com.truedigital.features.truecloudv3.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FileDownloaderCompletedReceiver(private val onReceiveAction: () -> Unit) :
    BroadcastReceiver() {
    companion object {
        private const val defaultValue: Long = -1
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val downloadManager = context?.getSystemService(DownloadManager::class.java)
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, defaultValue)
            if (id != defaultValue && downloadManager != null) {
                onReceiveAction.invoke()
            }
        }
    }
}
