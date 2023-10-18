package com.truedigital.features.tuned.presentation.common

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

interface SimpleServiceConnection : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, binder: IBinder)
    override fun onServiceDisconnected(name: ComponentName) {
        Unit
    }
}
