package com.truedigital.common.share.nativeshare.domain.model

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.GenerateDynamicLinkModel

data class DynamicLinkForTestModel(
    @SerializedName("DynamicLink") val dynamicLinkModel: List<GenerateDynamicLinkModel>
)
