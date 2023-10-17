package com.truedigital.features.tuned.service.appstatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.truedigital.features.tuned.presentation.widget.PlayerWidget

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        PlayerWidget.triggerUpdate(context)
    }
}
