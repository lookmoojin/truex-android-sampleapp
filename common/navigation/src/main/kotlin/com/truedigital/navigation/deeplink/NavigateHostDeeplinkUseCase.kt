package com.truedigital.navigation.deeplink

import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkDefaultError
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkResult
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkUnknown
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import javax.inject.Inject

interface NavigateHostDeeplinkUseCase {
    suspend fun execute(stringUrl: String): Pair<DeepLinkResult, DecodeDeeplinkUseCase?>
}

class NavigateHostDeeplinkUseCaseImpl @Inject constructor(
    private val decodeDeeplinkUseCaseList: Set<@JvmSuppressWildcards DecodeDeeplinkUseCase>
) : NavigateHostDeeplinkUseCase {
    override suspend fun execute(stringUrl: String): Pair<DeepLinkResult, DecodeDeeplinkUseCase?> {
        decodeDeeplinkUseCaseList.forEach {
            if (stringUrl.isBlank()) {
                return Pair(DeepLinkUnknown, null)
            }
            if (it.isCanDecode(stringUrl)) {
                return Pair(it.executeV2(stringUrl), it)
            }
        }
        return Pair(DeepLinkDefaultError, null)
    }
}
