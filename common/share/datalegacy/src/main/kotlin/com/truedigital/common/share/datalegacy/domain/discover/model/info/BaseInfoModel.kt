package com.truedigital.common.share.datalegacy.domain.discover.model.info

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class BaseInfoModel(
    var category: List<String>? = null,
    var cmsId: String = "",
    var contentType: String = "",
    var genre: List<String>? = null,
    var apiUrl: String = "",
    var params: String = "",
    var right: String = "",
    var recommendationSchemaId: String = ""
) : Parcelable
