package com.truedigital.common.share.datalegacy.data.repository.profile.model.accounts

import com.google.gson.annotations.SerializedName

data class AccountGetAllProfilesSettingsDataResponse(
    @SerializedName("kids_mode")
    val kidsMode: Boolean,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("notify_call")
    val notifyCall: Boolean,
    @SerializedName("notify_chat")
    val notifyChat: Boolean,
    @SerializedName("notify_community")
    val notifyCommunity: Boolean,
    @SerializedName("notify_order")
    val notifyOrder: String,
    @SerializedName("pincode")
    val pincode: String,
    @SerializedName("profile_lock")
    val profileLock: Boolean,
    @SerializedName("purchase_oneclick")
    val purchaseOneclick: Boolean,
    @SerializedName("purchase_pincode")
    val purchasePincode: Boolean,
    @SerializedName("purchase_user_level")
    val purchaseUserLevel: Boolean,
    @SerializedName("voice_commentary")
    val voiceCommentary: String
)
