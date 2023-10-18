package com.truedigital.features.tuned.data.user.repository

import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.profile.model.Profile
import com.truedigital.features.tuned.data.user.model.ContentLanguage
import com.truedigital.features.tuned.data.user.model.LoginType
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.UserModel
import io.reactivex.Single

interface MusicUserRepository {
    fun getTrueUserId(): Int?
    fun getUserId(): Int?

    fun get(refresh: Boolean = false): Single<User>
    fun get(userId: Int): Single<User>
    fun exist(): Boolean
    fun registerByType(device: Device, type: LoginType, msisdn: String? = null): Single<User>
    fun addLogin(
        username: String,
        password: String,
        type: LoginType = LoginType.DEVICE
    ): Single<Boolean>

    fun addDevice(device: Device): Single<Boolean>
    fun getSettings(): Settings?
    fun refreshSettings(): Single<Settings>
    fun syncLanguage(language: String): Single<User>
    fun update(
        userModel: UserModel? = null,
        tags: List<String>? = null,
        audioQuality: String? = null,
        language: String? = null
    ): Single<User>

    fun logout()

    fun getAction(code: String): Single<String>

    fun updateContentLanguages(languages: List<String>): Single<Any>
    fun getContentLanguages(): Single<List<ContentLanguage>>

    fun getSuggestedProfiles(): Single<List<Profile>>
    fun getFollowedProfiles(): Single<List<Profile>>
}
