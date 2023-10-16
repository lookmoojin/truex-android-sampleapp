package com.tdg.login.data.model

import com.google.gson.annotations.SerializedName

data class OauthRequest(
    @SerializedName("client_id")
    var clientId: String = "212",

    @SerializedName("client_secret")
    var clientSecret: String = "98391840d556e06878d775276891da3c",

    @SerializedName("username")
    var username: String = "",

    @SerializedName("password")
    var password: String = "",

    @SerializedName("grant_type")
    var grantType: String = "password",

    @SerializedName("scope")
    var scope: String = "public_profile,mobile,email,references",

    @SerializedName("device_id")
    var deviceId: String = "M34567994",

    @SerializedName("device_model")
    var deviceModel: String = "ME909483022O",

    @SerializedName("latlong")
    var latlong: String = "16.56523,100.13137",

    @SerializedName("ip_address")
    var ipAddress: String = "155.5.5.5"
)
