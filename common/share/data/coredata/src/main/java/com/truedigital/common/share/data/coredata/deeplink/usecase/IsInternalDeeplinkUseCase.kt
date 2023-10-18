package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import javax.inject.Inject

interface IsInternalDeeplinkUseCase {
    fun execute(url: String): Boolean
}

class IsInternalDeeplinkUseCaseImpl @Inject constructor() : IsInternalDeeplinkUseCase {

    override fun execute(url: String): Boolean {
        return url.contains(DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.HOST_ONELINK) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.HOST_ONELINK_TTID) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID_HTTP) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID_PREPROD) ||
            url.contains(DeeplinkConstants.DeeplinkConstants.KEY_ONELINK)
    }
}
