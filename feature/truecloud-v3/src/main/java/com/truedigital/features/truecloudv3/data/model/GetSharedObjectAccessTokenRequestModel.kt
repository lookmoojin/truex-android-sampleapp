package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class GetSharedObjectAccessTokenRequestModel(
    @SerializedName("encrypted_shared_object_id")
    val encryptedSharedObjectId: String,
    @SerializedName("password")
    val password: String
)
