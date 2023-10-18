package com.truedigital.common.share.data.coredata.data.model

import com.google.gson.annotations.SerializedName

data class CommunicatorInitialAppModel(
    @SerializedName("communicator") val communicator: CommunicatorInitialAppEnableModel?
)

data class CommunicatorInitialAppEnableModel(
    @SerializedName("enable") val enable: CommunicatorInitialAppEnablePlatformModel?,
    @SerializedName("image_en") val imageEn: String?,
    @SerializedName("image_th") val imageTh: String?
)

data class CommunicatorInitialAppEnablePlatformModel(
    @SerializedName("android") val android: Boolean?,
    @SerializedName("ios") val ios: Boolean?,
    @SerializedName("image") val image: String = "",
    @SerializedName("error_msg1_en") val errorMsgEn1: String = "",
    @SerializedName("error_msg1_local") val errorMsgLocal1: String = "",
    @SerializedName("error_msg2_en") val errorMsgEn2: String = "",
    @SerializedName("error_msg2_local") val errorMsgLocal2: String = "",
    @SerializedName("error_msg3_en") val errorMsgEn3: String = "",
    @SerializedName("error_msg3_local") val errorMsgLocal3: String = ""
)
