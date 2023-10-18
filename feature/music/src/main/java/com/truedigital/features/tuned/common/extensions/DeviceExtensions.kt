package com.truedigital.features.tuned.common.extensions

import com.truedigital.features.tuned.data.user.model.AssociatedDevice

fun List<AssociatedDevice>.getDeviceId(uniqueId: String): Int? =
    this.firstOrNull { it.uniqueId == uniqueId }?.deviceId
