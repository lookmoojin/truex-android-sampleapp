package com.truedigital.core.bus

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

class MainThreadBus : Bus {

    constructor(enforcer: ThreadEnforcer) : super(enforcer)
    constructor()

    private val handler = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event)
        } else {
            handler.post { super@MainThreadBus.post(event) }
        }
    }
}
