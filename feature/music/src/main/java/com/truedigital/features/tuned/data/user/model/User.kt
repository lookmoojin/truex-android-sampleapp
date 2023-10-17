package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    @SerializedName("UserId") var userId: Int,
    @SerializedName("DisplayName") var displayName: String?,
    @SerializedName("FirstName") var firstName: String?,
    @SerializedName("LastName") var lastName: String?,
    @SerializedName("PrimaryEmail") var primaryEmail: String?,
    @SerializedName("IsPrimaryEmailValidated") var isPrimaryEmailValidated: Boolean,
    @SerializedName("Image") var image: String?,
    @SerializedName("BackgroundImage") var backgroundImage: String?,
    @SerializedName("Followers") var followers: List<Int>,
    @SerializedName("Following") var following: List<Int>,
    @SerializedName("IsPublic") var isPublic: Boolean,
    @SerializedName("OptedIn") var optedIn: Boolean,
    @SerializedName("Blocked") var blocked: List<Int>,
    @SerializedName("Language") var language: String?,
    @SerializedName("Subscriptions") var subscriptions: List<Subscription>,
    @SerializedName("Devices") var devices: List<AssociatedDevice>,
    @SerializedName("BirthYear") var birthYear: Int,
    @SerializedName("Gender") var gender: String?,
    @SerializedName("Logins") var logins: List<Login>,
    @SerializedName("Action") var action: String?,
    @SerializedName("AudioQuality") var audioQuality: String,
    @SerializedName("ContentLanguages") var contentLanguages: List<ContentLanguage>?,
    @SerializedName("Country") var country: String?,
    @SerializedName("IsVerified") var isVerified: Boolean,
    @SerializedName("Circle") var circle: String?,
    var isFacebookUser: Boolean = false,
    var isTwitterUser: Boolean = false
) {
    val hasActiveSub
        get() = subscriptions.firstOrNull { it.endDate.after(Date()) || it.isAutoRenew } != null

    val username: String?
        get() = logins.firstOrNull { it.type == UserAccountType.USERNAME.type }?.value
}
