package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import java.net.URL
import javax.inject.Inject

interface IsPrivilegeUrlUseCase {
    fun execute(url: String): Boolean
}

class IsPrivilegeUrlUseCaseImpl @Inject constructor() : IsPrivilegeUrlUseCase {
    override fun execute(stringUrl: String): Boolean {
        if (stringUrl.isEmpty()) {
            return false
        }
        return try {
            val url = URL(stringUrl)
            val host = url.host
            host.equals(DeeplinkConstants.DeeplinkConstants.HOST_PRIVILEGE)
        } catch (error: Exception) {
            false
        }
    }
}
