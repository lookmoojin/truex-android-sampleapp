package com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink

import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.GenerateDynamicLinkModel
import io.reactivex.Single

interface DynamicLinkGeneratorUseCase {

    fun generateDynamicLink(
        dynamicLinkModel: GenerateDynamicLinkModel,
        contentId: String,
        callback: DynamicLinkGeneratorCallback
    )

    fun generateDynamicLink(
        dynamicLinkModel: GenerateDynamicLinkModel,
        callback: DynamicLinkGeneratorCallback
    )

    fun generateDynamicLinkToSingle(
        dynamicLinkModel: GenerateDynamicLinkModel
    ): Single<String>
}
