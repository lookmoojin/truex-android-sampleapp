package com.truedigital.features.tuned.domain.facade.lostnetwork

interface LossOfNetworkFacade {
    fun hasOfflineRight(): Boolean
    fun isUserAllowedOffline(): Boolean
}
