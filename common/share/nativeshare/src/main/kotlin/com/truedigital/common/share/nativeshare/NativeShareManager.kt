package com.truedigital.common.share.nativeshare

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.truedigital.common.share.nativeshare.constant.NativeShareConstant
import com.truedigital.common.share.nativeshare.utils.getImageFileFromUrl

interface NativeShareManager {
    fun onSharePress(
        shortUrl: String,
        title: String,
        imageUrl: String,
        shareType: String,
        onSharedSelected: (
            (packageName: String?) -> Unit
        )? = null
    )
}

class NativeShareManagerImpl(val context: Context) : NativeShareManager {
    companion object {
        const val ACTION_SHARE = "com.truedigital.common.share.nativeshare.action.SHARE"
    }

    override fun onSharePress(
        shortUrl: String,
        title: String,
        imageUrl: String,
        shareType: String,
        onSharedSelected: (
            (packageName: String?) -> Unit
        )?
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, shortUrl)
            type = NativeShareConstant.DYNAMIC_LINK

            if (shareType == NativeShareConstant.IMAGE) {
                val imageUri = getImageFileFromUrl(context, imageUrl)?.toURI()
                imageUri?.let {
                    type = NativeShareConstant.IMAGE
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                }
            }
        }

        val chooserIntent = if (onSharedSelected != null) {
            val shareReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    context.unregisterReceiver(this)
                    val component = intent?.getParcelableExtra<ComponentName>(Intent.EXTRA_CHOSEN_COMPONENT)
                    onSharedSelected.invoke(component?.packageName)
                }
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FLAG_UPDATE_CURRENT or FLAG_MUTABLE
            } else {
                FLAG_UPDATE_CURRENT
            }
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, Intent(ACTION_SHARE), flags)

            context.registerReceiver(shareReceiver, IntentFilter(ACTION_SHARE))

            Intent.createChooser(intent, title, pendingIntent.intentSender)
        } else {
            Intent.createChooser(intent, title)
        }

        context.startActivity(chooserIntent)
    }
}
