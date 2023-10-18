package com.truedigital.common.share.data.coredata.deeplink

import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkContentNotFound
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkDefaultError
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkFeatureOff
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkResult
import com.truedigital.foundation.extension.SingleLiveEvent

object DeepLinkStateLiveData {
    var isAlready = false
    var isActive = false

    val showDialogDeepLinkLiveEvent = SingleLiveEvent<DeepLinkResult>()

    fun showContentNotFound() {
        this.showDialogDeepLinkLiveEvent.value = DeepLinkContentNotFound
    }

    fun showDefaultError() {
        this.showDialogDeepLinkLiveEvent.value = DeepLinkDefaultError
    }

    fun showFeatureOff() {
        this.showDialogDeepLinkLiveEvent.value = DeepLinkFeatureOff
    }
}
