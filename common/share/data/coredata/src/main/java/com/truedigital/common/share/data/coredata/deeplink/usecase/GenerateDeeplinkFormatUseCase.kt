package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.ConfigsDeeplinkFormat
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import javax.inject.Inject

interface GenerateDeeplinkFormatUseCase {
    fun execute(type: DeeplinkConstants.DeeplinkContentType, parameter: Map<String, String>): String
}

class GenerateDeeplinkFormatUseCaseImpl @Inject constructor() : GenerateDeeplinkFormatUseCase {
    override fun execute(
        type: DeeplinkConstants.DeeplinkContentType,
        parameter: Map<String, String>
    ): String {
        var deeplinkUrl = ConfigsDeeplinkFormat[type] ?: ""
        parameter.forEach { (key, value) ->
            deeplinkUrl = deeplinkUrl.replace(key, value)
        }
        return deeplinkUrl
    }
}
