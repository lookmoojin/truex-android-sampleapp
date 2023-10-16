package com.truedigital.component.widget.livecommerce.domain.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_COMMERCE
import javax.inject.Inject

interface CreateCommerceLiveDeeplinkUseCase {
    fun execute(postId: String?, streamId: String?): String
}

class CreateCommerceLiveDeeplinkUseCaseImpl @Inject constructor() : CreateCommerceLiveDeeplinkUseCase {

    companion object {
        private const val SCHEME_HTTPS = "https"
        private const val PATH_COMMERCE_LIVE = "commercelive"
    }

    override fun execute(postId: String?, streamId: String?): String {
        return StringBuilder()
            .append("$SCHEME_HTTPS://")
            .append(HOST_COMMERCE)
            .append("/$PATH_COMMERCE_LIVE")
            .apply {
                if (!postId.isNullOrEmpty() && !streamId.isNullOrEmpty()) {
                    append("/$postId")
                    append("/$streamId")
                }
            }.toString()
    }
}
