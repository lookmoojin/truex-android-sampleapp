package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model

import com.google.gson.annotations.SerializedName

class CmsShelfResponse {

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: Data? = null

    @SerializedName("lang")
    var lang: String? = null

    @SerializedName("pages")
    var pages: Pages? = null
}

data class CmsShelfListResponse(

    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("data")
    val data: List<Data>? = null,

    @SerializedName("lang")
    val lang: String? = null,
)

data class CmsShelfGenericResponse<T>(
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("data")
    var data: T? = null,

    @SerializedName("lang")
    var lang: String? = null,

    @SerializedName("pages")
    var pages: Pages? = null
)
