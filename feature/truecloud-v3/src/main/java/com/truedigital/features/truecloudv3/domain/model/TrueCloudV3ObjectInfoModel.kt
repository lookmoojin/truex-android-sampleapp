package com.truedigital.features.truecloudv3.domain.model

data class TrueCloudV3ObjectInfoModel(
    var id: String? = null,
    var parentObjectId: String? = null,
    var objectType: String? = null,
    var name: String? = null,
    var mimeType: String = "",
    var category: String? = null,
    var fileUrl: String? = null,
)
