package com.truedigital.common.share.datalegacy.login

import com.truedigital.authentication.domain.model.ProfileModel
import com.truedigital.authentication.presentation.AccessTokenListener
import com.truedigital.authentication.presentation.QrCodeLoginListener
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

interface LoginManagerInterface {
    fun isLoggedIn(): Boolean
    fun getAccessToken(): String
    fun getAccessToken(accessTokenListener: AccessTokenListener)
    fun getProfile(): Observable<ProfileModel>
    fun sendQrData(jsonData: String, qrCodeLoginListener: QrCodeLoginListener? = null)
    fun login(
        authLoginListener: AuthLoginListener? = null,
        requireDialogLoading: Boolean = true,
        isForceLogin: Boolean = false
    )
    fun login(
        authLoginListener: AuthLoginListener? = null,
        requireDialogLoading: Boolean = true,
        isForceLogin: Boolean = false,
        configCloseButton: Boolean
    )
    fun loginWithByPass(authLoginListener: AuthLoginListener? = null)
    fun onFinishFlowLogin(): Single<Boolean>
    fun logout()
    fun logout(forceRestart: Boolean)
    fun logout(forceRestart: Boolean, clearEasyLogin: Boolean = false)
    fun onFinishFlowLogout(): Single<Boolean>
    fun onLogoutSuccess(): Observable<Boolean>
    fun observeOnFinishFlowLogin(): Observable<Boolean>
    fun observeOnFinishFlowLogout(): Observable<Boolean>
    fun clearEasyLogin()
}

class LoginManagerImpl @Inject constructor(
    private val authManagerWrapper: AuthManagerWrapper
) : LoginManagerInterface {

    override fun isLoggedIn(): Boolean {
        return authManagerWrapper.isLoggedIn()
    }

    override fun getAccessToken(): String {
        return authManagerWrapper.getAccessToken() ?: ""
    }

    override fun getAccessToken(accessTokenListener: AccessTokenListener) {
        authManagerWrapper.getAccessToken(accessTokenListener)
    }

    override fun getProfile(): Observable<ProfileModel> = authManagerWrapper.onGetProfile()

    override fun sendQrData(jsonData: String, qrCodeLoginListener: QrCodeLoginListener?) {
        authManagerWrapper.sendQrData(jsonData, qrCodeLoginListener)
    }

    override fun login(
        authLoginListener: AuthLoginListener?,
        requireDialogLoading: Boolean,
        isForceLogin: Boolean
    ) {
        authManagerWrapper.login(
            authLoginListener = authLoginListener,
            requireDialogLoading = requireDialogLoading,
            isForceLogin = isForceLogin,
            configCloseButton = true
        )
    }

    override fun login(
        authLoginListener: AuthLoginListener?,
        requireDialogLoading: Boolean,
        isForceLogin: Boolean,
        configCloseButton: Boolean
    ) {
        authManagerWrapper.login(
            authLoginListener = authLoginListener,
            requireDialogLoading = requireDialogLoading,
            isForceLogin = isForceLogin,
            configCloseButton = configCloseButton
        )
    }

    override fun loginWithByPass(authLoginListener: AuthLoginListener?) {
        authManagerWrapper.login(
            authLoginListener = authLoginListener,
            isOpenByPass = true
        )
    }

    override fun onFinishFlowLogin(): Single<Boolean> {
        return authManagerWrapper.onFinishFlowLogin().firstElement().toSingle()
    }

    override fun onLogoutSuccess(): Observable<Boolean> {
        return authManagerWrapper.onLogoutSuccess()
    }

    override fun logout() {
        authManagerWrapper.logout()
    }

    override fun logout(forceRestart: Boolean) {
        authManagerWrapper.logout(forceRestart)
    }

    override fun logout(forceRestart: Boolean, clearEasyLogin: Boolean) {
        authManagerWrapper.logout(forceRestart, clearEasyLogin)
    }

    override fun onFinishFlowLogout(): Single<Boolean> {
        return authManagerWrapper.onLogoutSuccess().firstElement().toSingle()
    }

    override fun observeOnFinishFlowLogin(): Observable<Boolean> {
        return authManagerWrapper.onFinishFlowLogin()
    }

    override fun observeOnFinishFlowLogout(): Observable<Boolean> {
        return authManagerWrapper.onLogoutSuccess()
    }

    override fun clearEasyLogin() {
        return authManagerWrapper.clearEasyLogin()
    }
}
