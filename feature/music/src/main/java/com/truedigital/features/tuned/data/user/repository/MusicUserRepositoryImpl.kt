package com.truedigital.features.tuned.data.user.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.truedigital.features.tuned.BuildConfig
import com.truedigital.features.tuned.api.user.AuthenticatedUserApiInterface
import com.truedigital.features.tuned.api.user.BasicUserApiInterface
import com.truedigital.features.tuned.api.user.MetadataGroupApiInterface
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.data.user.model.ContentLanguage
import com.truedigital.features.tuned.data.user.model.LoginType
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.UserModel
import com.truedigital.features.tuned.data.user.model.request.AuthDevice
import com.truedigital.features.tuned.data.user.model.request.RegisterByType
import com.truedigital.features.tuned.data.user.model.request.UserDetails
import com.truedigital.features.tuned.data.user.model.request.UsernamePasswordType
import com.truedigital.features.tuned.injection.module.NetworkModule
import io.reactivex.Single
import retrofit2.HttpException
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MusicUserRepositoryImpl(
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val basicUserApi: BasicUserApiInterface,
    private val authenticatedUserApi: AuthenticatedUserApiInterface,
    private val metadataGroupApi: MetadataGroupApiInterface,
    private val config: Configuration
) : MusicUserRepository {

    companion object {
        const val CURRENT_USER_KEY = "current_user"
        const val CURRENT_USER_SETTINGS_KEY = "current_user_settings"
        const val CONTENT_LANGUAGES_KEY = "content_languages"
        const val CONTENT_LANGUAGES_LAST_UPDATE_KEY = "content_languages_last_update"
        const val MSISDN_KEY = "msisdn"

        private const val USER_HAS_NOT_BEEN_CREATED = "User has not been created"
        private val oneDayMillisecond = TimeUnit.DAYS.toMillis(1) // 1 Day
    }

    private var user: User? = sharedPreferences.get(CURRENT_USER_KEY)
        set(value) {
            // auto updates relevant user model/storage after set user model
            val actionLessUser = value?.copy(action = null)
            if (actionLessUser == null) sharedPreferences.remove(CURRENT_USER_KEY)
            else sharedPreferences.put(CURRENT_USER_KEY, actionLessUser)
            field = actionLessUser
        }

    // call this when modified a user attribute
    private fun syncCurrentUser() {
        sharedPreferences.put(CURRENT_USER_KEY, user)
    }

    override fun getTrueUserId(): Int? {
        return user?.let { _user ->
            _user.logins.firstOrNull { it.type == LoginType.USERNAME.value }?.value?.toIntOrNull()
        } ?: run {
            null
        }
    }

    override fun getUserId(): Int? {
        return user?.userId
    }

    override fun get(refresh: Boolean): Single<User> {
        return user?.let { _user ->
            if (refresh) {
                authenticatedUserApi.getUser(_user.userId).doOnSuccess { user = it }
            } else {
                Single.just(_user)
            }
        } ?: run {
            Single.error(IllegalStateException(USER_HAS_NOT_BEEN_CREATED))
        }
    }

    override fun get(userId: Int): Single<User> =
        authenticatedUserApi.getUser(userId).doOnSuccess { user = it }

    override fun exist() = user != null

    override fun registerByType(device: Device, type: LoginType, msisdn: String?): Single<User> {
        var description = device.brand
        device.referrer?.let {
            description += ", "
            description += it.replace("\\&", ", ")
        }

        return basicUserApi.registerByType(
            RegisterByType(
                device.type, device.displayName,
                if (type == LoginType.MSISDN) msisdn.orEmpty() else device.uniqueId,
                device.operatingSystem, device.operatingSystemVersion, device.appVersion,
                device.token, device.country, device.language, device.manufacturer,
                config.applicationId, device.timezoneOffset, device.carrier, description, type.value
            )
        ).doOnSuccess { user = it }
    }

    override fun addLogin(username: String, password: String, type: LoginType): Single<Boolean> =
        authenticatedUserApi.addLogin(UsernamePasswordType(username, password, type.value))

    override fun addDevice(device: Device): Single<Boolean> =
        authenticatedUserApi.authDevice(
            AuthDevice(
                device.type, device.displayName, device.uniqueId, device.operatingSystem,
                device.operatingSystemVersion, BuildConfig.VERSION_NAME, device.manufacturer,
                config.applicationId, device.carrier
            )
        ).map { it.value }

    override fun getSettings(): Settings? = sharedPreferences.get(CURRENT_USER_SETTINGS_KEY)

    override fun syncLanguage(language: String): Single<User> {
        return user?.let { _user ->
            if (_user.language != language) {
                update(language = language)
            } else {
                Single.just(_user)
            }
        } ?: run {
            Single.error(IllegalStateException(USER_HAS_NOT_BEEN_CREATED))
        }
    }

    override fun refreshSettings(): Single<Settings> {
        return user?.let { _user ->
            authenticatedUserApi.getSettings(_user.userId, config.applicationId)
                .map { settings ->
                    settings.allowAlbumNavigation = true
                    // check configure and define maximum offline duration for each product flavor
                    settings.offlineMaximumDuration = oneDayMillisecond
                    sharedPreferences.put(CURRENT_USER_SETTINGS_KEY, settings)
                    settings
                }
        } ?: run {
            Single.error(IllegalStateException(USER_HAS_NOT_BEEN_CREATED))
        }
    }

    override fun update(
        userModel: UserModel?,
        tags: List<String>?,
        audioQuality: String?,
        language: String?
    ): Single<User> {
        user?.let { _user ->
            val birthYear = if (userModel?.age != null && userModel.age.isNotBlank()) {
                Calendar.getInstance().get(Calendar.YEAR) - userModel.age.toInt()
            } else {
                0
            }

            val userDetails = UserDetails(
                firstName = userModel?.firstName,
                lastName = userModel?.lastName,
                displayName = userModel?.displayName,
                email = userModel?.email,
                gender = userModel?.gender?.type,
                birthYear = birthYear,
                language = language,
                audioQuality = audioQuality,
                tags = tags?.joinToString(",")
            )
            return authenticatedUserApi.updateMyUserDetails(userDetails)
                .map {
                    if (it.value) {
                        userDetails.updateUserModel(_user)
                        syncCurrentUser()
                    }
                    user
                }
        } ?: run {
            return Single.error(IllegalStateException(USER_HAS_NOT_BEEN_CREATED))
        }
    }

    override fun logout() {
        user = null
        sharedPreferences.remove(CURRENT_USER_SETTINGS_KEY)
        sharedPreferences.remove(MSISDN_KEY)
    }

    override fun getAction(code: String): Single<String> =
        authenticatedUserApi.redeemPromoCode(code).map { it.action }

    override fun updateContentLanguages(languages: List<String>): Single<Any> =
        authenticatedUserApi.updateContentLanguages(languages)

    override fun getContentLanguages(): Single<List<ContentLanguage>> {
        val lastUpdatedTime = sharedPreferences.get(CONTENT_LANGUAGES_LAST_UPDATE_KEY, 0L)
        val timeSinceLastUpdate = System.currentTimeMillis() - lastUpdatedTime

        return if (timeSinceLastUpdate < oneDayMillisecond &&
            sharedPreferences.contains(CONTENT_LANGUAGES_KEY)
        ) {
            val contentLanguagesString = sharedPreferences.get<String>(CONTENT_LANGUAGES_KEY)
            val typeToken = object : TypeToken<List<ContentLanguage>>() {}.type
            val contentLanguages =
                Gson().fromJson<List<ContentLanguage>>(contentLanguagesString, typeToken)
            Single.just(contentLanguages)
        } else {
            metadataGroupApi.getContentLanguages()
                .doOnSuccess { sharedPreferences.put(CONTENT_LANGUAGES_KEY, it) }
                .onErrorResumeNext {
                    if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                        sharedPreferences.put(CONTENT_LANGUAGES_KEY, listOf<ContentLanguage>())
                        Single.just(listOf())
                    } else {
                        throw it
                    }
                }
        }
    }

    override fun getSuggestedProfiles() = authenticatedUserApi.getSuggestedUsers()

    override fun getFollowedProfiles() = authenticatedUserApi.getFollowedUsers()
}
