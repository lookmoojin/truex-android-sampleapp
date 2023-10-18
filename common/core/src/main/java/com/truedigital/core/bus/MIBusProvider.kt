package com.truedigital.core.bus

import com.squareup.otto.ThreadEnforcer

object MIBusProvider {

    val bus: MainThreadBus by lazy {
        MainThreadBus(ThreadEnforcer.ANY)
    }
}
