package com.truedigital.common.share.data.coredata.deeplink.extension

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_ONELINK
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_ONELINK_TTID

fun String.isOneLink(): Boolean {
    return this.contains(HOST_ONELINK) || this.contains(HOST_ONELINK_TTID)
}
