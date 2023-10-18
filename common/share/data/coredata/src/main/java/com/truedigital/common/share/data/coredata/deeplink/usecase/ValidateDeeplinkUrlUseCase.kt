package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import javax.inject.Inject

interface ValidateDeeplinkUrlUseCase {
    fun execute(url: String): String
}

class ValidateDeeplinkUrlUseCaseImpl @Inject constructor(
    private val generateDeeplinkFormatUseCase: GenerateDeeplinkFormatUseCase,
    private val isInternalDeeplinkUseCase: IsInternalDeeplinkUseCase
) : ValidateDeeplinkUrlUseCase {
    override fun execute(url: String): String {
        return if (url.isEmpty() || isInternalDeeplinkUseCase.execute(url)) {
            url
        } else {
            generateDeeplinkFormatUseCase.execute(
                type = DeeplinkConstants.DeeplinkContentType.INAPP_BROWSER,
                parameter = mapOf(DeeplinkConstants.DeeplinkKey.WEBSITE to url)
            )
        }
    }
}
