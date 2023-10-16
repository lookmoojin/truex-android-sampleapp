package com.truedigital.common.share.data.coredata.deeplink

import com.truedigital.foundation.extension.SingleLiveEvent

object OneLinkLiveData {
    val deepLinkFromOneLink = SingleLiveEvent<String>()

    fun openDeepLinkFromOneLink(url: String?) {
        if (!url.isNullOrEmpty()) {
            this.deepLinkFromOneLink.value = url
        }
    }
}
