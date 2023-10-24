package com.truedigital.common.share.datalegacy.data.repository.profile

import com.newrelic.agent.android.NewRelic
import com.truedigital.authentication.domain.model.ProfileModel
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.constant.SharedPrefsKeyConstant
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

/***************************************************************************************************
 *
 * The `UserRepository` class represents the user's information
 * such as
 * - SsoId
 * - Profile
 *
 * Next Development plan:
 * - Remove dataManager: [com.tdcm.trueidapp.features.helpers.content.DataManagerProvider] as dependency
 *
 **************************************************************************************************/
interface UserRepository {
    fun getConfigState(key: String): Boolean
    fun setConfigState(key: String, value: Boolean)
    fun getDebugMode(): String
    fun setDebugMode(debugMode: String)
    fun getProfile(): ProfileModel?
    fun getAccessToken(): String
    fun getEmail(): String
    fun getLoginAccount(): String
    fun getMobile(): String
    fun getSsoAvatar(): String
    fun getSsoDisplayName(): String
    fun getSsoId(): String
    fun getSsoThaiId(): String
    fun getSubId(): String
    fun getTrustedOwner(): String
    fun getRegisterDate(): String
    fun getBio(): String
    fun isTokenExp(): Boolean
    fun isLoginWithMobile(): Boolean

    // true you
    fun setThaiId(thaiId: String?)
    fun getThaiId(): String

    // truecloud example app
    fun saveSsoId(ssoId:String)
}

class UserRepositoryImpl @Inject constructor(
    private val authManagerWrapper: AuthManagerWrapper,
    private val sharedPrefsUtils: SharedPrefsUtils
) : UserRepository {

    private companion object {
        const val PHONE_NUMBER_PATTERN = "^((\\+(66))|(0))\\d{8,9}$"
        private var ssoIdForTrueCloudExample: String = ""
    }

    override fun getConfigState(key: String): Boolean {
        return sharedPrefsUtils.get(key, false)
    }

    override fun setConfigState(key: String, value: Boolean) {
        sharedPrefsUtils.put(key, value)
    }

    override fun getDebugMode(): String {
        return sharedPrefsUtils.get(FireBaseConstant.DEBUG_MODE, "")
    }

    override fun setDebugMode(debugMode: String) {
        sharedPrefsUtils.put(FireBaseConstant.DEBUG_MODE, debugMode)
    }

    override fun getProfile(): ProfileModel? {
        return try {
            authManagerWrapper.getProfileCache()
        } catch (exception: Exception) {
            val handlingExceptionMap = mapOf(
                "Key" to "getProfile() in UserRepository",
                "Value" to exception.localizedMessage
            )
            NewRelic.recordHandledException(Exception("getProfile()"), handlingExceptionMap)

            ProfileModel()
        }
    }

    override fun getAccessToken(): String {
        return authManagerWrapper.getAccessToken() ?: ""
    }

    override fun getEmail(): String {
        return getProfile()?.more?.email ?: ""
    }

    override fun getLoginAccount(): String {
        return getProfile()?.more?.loginByAccount ?: ""
    }

    override fun getMobile(): String {
        return getProfile()?.more?.mobile ?: ""
    }

    override fun getSsoAvatar(): String {
        return getProfile()?.more?.avatar ?: ""
    }

    override fun getSsoDisplayName(): String {
        return getProfile()?.more?.displayName ?: ""
    }

    override fun getSsoId(): String {
//        return getProfile()?.payload?.sub ?: ""
        return ssoIdForTrueCloudExample
    }

    override fun getSsoThaiId(): String {
        return getProfile()?.more?.thaiId ?: ""
    }

    override fun getSubId(): String {
        return getProfile()?.payload?.sub ?: ""
    }

    override fun getTrustedOwner(): String {
        return getProfile()?.payload?.trustedOwner ?: ""
    }

    override fun getRegisterDate(): String {
        return getProfile()?.more?.registerDate ?: ""
    }

    override fun getBio(): String {
        return getProfile()?.more?.bio ?: ""
    }

    override fun isTokenExp(): Boolean {
        val tokenExp = getProfile()?.payload?.tokenExp ?: 0
        return (System.currentTimeMillis() / 1000) >= tokenExp.toLong()
    }

    override fun isLoginWithMobile(): Boolean {
        return PHONE_NUMBER_PATTERN.toRegex().containsMatchIn(getLoginAccount())
    }

    override fun setThaiId(thaiId: String?) {
        sharedPrefsUtils.put(SharedPrefsKeyConstant.TRUE_POINT_THAI_ID, thaiId ?: "")
    }

    override fun getThaiId(): String {
        return sharedPrefsUtils.get(SharedPrefsKeyConstant.TRUE_POINT_THAI_ID, "")
    }

    override fun saveSsoId(ssoId: String) {
        ssoIdForTrueCloudExample = ssoId
    }
}
