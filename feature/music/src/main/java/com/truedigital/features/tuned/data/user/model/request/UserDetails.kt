package com.truedigital.features.tuned.data.user.model.request

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.user.model.User

data class UserDetails(
    @SerializedName("FirstName") var firstName: String? = null,
    @SerializedName("LastName") var lastName: String? = null,
    @SerializedName("DisplayName") var displayName: String? = null,
    @SerializedName("Email") var email: String? = null,
    @SerializedName("Language") var language: String? = null,
    @SerializedName("BirthYear") var birthYear: Int? = null,
    @SerializedName("Gender") var gender: String? = null,
    @SerializedName("AudioQuality") var audioQuality: String? = null,
    @SerializedName("Tags") var tags: String? = null
) {

    fun updateUserModel(user: User): User {
        firstName?.let { user.firstName = it }
        lastName?.let { user.lastName = it }
        displayName?.let { user.displayName = it }
        email?.let { user.primaryEmail = it }
        language?.let { user.language = it }
        birthYear?.let { user.birthYear = it }
        gender?.let { user.gender = it }
        audioQuality?.let { user.audioQuality = it }
        return user
    }
}
