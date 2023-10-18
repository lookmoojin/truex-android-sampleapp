package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK_DEV
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK_PREPROD
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK_PROD
import java.net.URL
import javax.inject.Inject

interface IsDynamicUrlUseCase {
    fun execute(url: String): Boolean
}

class IsDynamicUrlUseCaseImpl @Inject constructor() : IsDynamicUrlUseCase {
    override fun execute(stringUrl: String): Boolean {
        if (stringUrl.isEmpty()) {
            return false
        }
        return try {
            val url = URL(stringUrl)
            val host = url.host
            stringUrl.contains(DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK) ||
                host.equals(HOST_DYNAMIC_LINK_DEV) ||
                host.equals(HOST_DYNAMIC_LINK_PREPROD) ||
                host.equals(HOST_DYNAMIC_LINK_PROD)
        } catch (error: Exception) {
            false
        }
    }
}
