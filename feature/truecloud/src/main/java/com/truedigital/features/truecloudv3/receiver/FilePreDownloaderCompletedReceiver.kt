package com.truedigital.features.truecloudv3.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class FilePreDownloaderCompletedReceiver(private val onReceiveAction: (uri: Uri) -> Unit) :
    BroadcastReceiver() {
    companion object {
        private const val defaultValue: Long = -1
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        val downloadManager = context?.getSystemService(DownloadManager::class.java)
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, defaultValue)
            if (id != defaultValue && downloadManager != null) {
                onReceiveAction.invoke(downloadManager.getUriForDownloadedFile(id))
            }
        }
    }
}
