package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkFeatureOff
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkResult
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkSuccess

interface DecodeDeeplinkUseCase {
    fun execute(stringUrl: String): String {
        return ""
    }

    fun isCanDecode(stringUrl: String): Boolean

    suspend fun switchBottomTab(stringUrl: String): String {
        return ""
    }

    /* Optionally implement when need uses dialog of the module example */
    suspend fun executeV2(stringUrl: String): DeepLinkResult {
        return if (isFeatureOpen()) {
            val url = execute(stringUrl)
            DeepLinkSuccess(url, switchBottomTab(url))
        } else {
            DeepLinkFeatureOff
        }
    }

    suspend fun isFeatureOpen(): Boolean {
        return true
    }
}
