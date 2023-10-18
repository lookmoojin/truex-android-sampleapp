package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class ObjectsRequestModel(
    @SerializedName("parent_object_id")
    val parentObjectId: String?,
    @SerializedName("object_ids")
    val objectIds: ArrayList<String>
)
