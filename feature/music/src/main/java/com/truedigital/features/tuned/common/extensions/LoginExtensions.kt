package com.truedigital.features.tuned.common.extensions

import com.truedigital.features.tuned.data.user.model.Login
import com.truedigital.features.tuned.data.user.model.UserAccountType

val List<Login>.isDeviceUser
    get() = any { it.type == UserAccountType.DEVICE.type }
