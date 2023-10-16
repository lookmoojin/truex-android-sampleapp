package com.truedigital.navigation.data

import com.google.gson.annotations.SerializedName

data class PersonaResponse(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("data")
    var data: PersonaData? = null
)

data class PersonaData(
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("schemaId")
    var schemaId: String? = null
)
