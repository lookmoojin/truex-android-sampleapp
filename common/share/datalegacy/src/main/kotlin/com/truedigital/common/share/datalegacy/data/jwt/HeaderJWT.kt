package com.truedigital.common.share.datalegacy.data.jwt

import com.google.gson.annotations.SerializedName

/*
* Created by yothinindy on 7/11/2018 AD.
*/

internal data class HeaderJWT(
    @SerializedName("kid") val kid: String,
    @SerializedName("alg") val alg: String
)
