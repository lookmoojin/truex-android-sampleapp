package com.truedigital.common.share.datalegacy.wrapper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.webkit.WebViewCompat
import com.appsflyer.AppsFlyerLib
import com.newrelic.agent.android.NewRelic
import com.truedigital.authentication.common.AuthEnvironment
import com.truedigital.authentication.common.KeyAnalytic.AnalyticEvent.LOGIN
import com.truedigital.authentication.domain.model.AnalyticFirebaseModel
import com.truedigital.authentication.domain.model.Events
import com.truedigital.authentication.domain.model.ProfileModel
import com.truedigital.authentication.domain.model.Screens
import com.truedigital.authentication.domain.model.TrackRefreshTokenModel
import com.truedigital.authentication.logging.AuthLogTree
import com.truedigital.authentication.presentation.AccessTokenListener
import com.truedigital.authentication.presentation.AuthActionListener
import com.truedigital.authentication.presentation.AuthAnalyticsListener
import com.truedigital.authentication.presentation.AuthManager
import com.truedigital.authentication.presentation.BindingTrueMoneyListener
import com.truedigital.authentication.presentation.IdentificationListener
import com.truedigital.authentication.presentation.LogoutListener
import com.truedigital.authentication.presentation.OpenFunction
import com.truedigital.authentication.presentation.ProfileListener
import com.truedigital.authentication.presentation.QrCodeLoginListener
import com.truedigital.authentication.presentation.RefreshTokenListener
import com.truedigital.authentication.presentation.SingleSignOnListener
import com.truedigital.authentication.social.SocialLoginService
import com.truedigital.common.share.datalegacy.R
import com.truedigital.common.share.datalegacy.bus.SendAuthStatusChange
import com.truedigital.common.share.datalegacy.domain.login.usecase.GetLoginUrlUseCase
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCase
import com.truedigital.common.share.datalegacy.login.SocialLoginServiceImpl
import com.truedigital.common.share.datalegacy.progress.AuthProgressDialog
import com.truedigital.core.BuildConfig
import com.truedigital.core.bus.MIBusProvider
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.CompareVersion
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.compareVersionTo
import com.truedigital.core.extensions.dismissSafe
import com.truedigital.core.extensions.environmentCase
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.manager.ApplicationPackageManager
import com.truedigital.core.manager.ApplicationPackageName
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.trueidsdk.TrueIdSdk
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

const val KEY_REMOTE_CONFIG_AAA_SIGNIN_URL = "aaa_signin_flow"
const val DELAYED_LOGIN_CANCEL = 300L

@SuppressLint("StaticFieldLeak")
class AuthManagerWrapper @Inject constructor(
    private val contextDataProvider: ContextDataProvider,
    private val applicationPackageManager: ApplicationPackageManager,
    private val localizationRepository: LocalizationRepository,
    private val getSystemWebViewMinimumVersionUseCase: GetSystemWebViewMinimumVersionUseCase,
    private val getLoginUrlUseCase: GetLoginUrlUseCase,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main + handler

    private val context = contextDataProvider.getDataContext()

    private var authLoginListener: AuthLoginListener? = null

    private val getProfileSuccess: PublishSubject<ProfileModel> = PublishSubject.create()
    private val finishFlowLogin: PublishSubject<Boolean> = PublishSubject.create()
    private val logoutSuccess: PublishSubject<Boolean> = PublishSubject.create()
    private val allowRegister: PublishSubject<Unit> = PublishSubject.create()
    private val sendScreens: PublishSubject<Screens> = PublishSubject.create()
    private val sendEvents: PublishSubject<Events> = PublishSubject.create()
    private val sendTrackFirebase: PublishSubject<AnalyticFirebaseModel> = PublishSubject.create()
    private val sendTrackEventFirebase: PublishSubject<String> = PublishSubject.create()
    private val sendTrackStateRefreshToken: PublishSubject<String> = PublishSubject.create()
    private val handler = CoroutineExceptionHandler { context, error ->
        val handlingExceptionMap = mapOf(
            "Key" to "AuthManagerWrapper.handler()",
            "Value" to "Problem with Coroutine caused by $error in context $context",
        )
        NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
    }

    private val socialLoginService: SocialLoginService by lazy {
        SocialLoginServiceImpl(contextDataProvider)
    }

    private var authProgressDialog: AuthProgressDialog? = null
    private var requireDialogLoading: Boolean = true
    private var isOpenByPass: Boolean = false

    fun initial(context: Context) {
        authProgressDialog = AuthProgressDialog(context).apply { setCancelable(false) }
        AuthManager.initial(context = context).run {
            // authManager.setLanguage(localizationRepository.getAppLanguageCodeForEnTh())
            val authEnvironment = BuildConfig.ENVIRONMENT.environmentCase(
                staging = {
                    AuthEnvironment.ALPHA
                },
                preProd = {
                    AuthEnvironment.STAGING
                },
                prod = {
                    AuthEnvironment.PRODUCTION
                },
            )
            setEnvironment(authEnvironment)
            setClientId(localizationRepository.getAppClientID())
            setLanguage(localizationRepository.getAppLanguageCode())
            setCountry(localizationRepository.getAppCountryCode())

            launchSafe {
                getLoginUrlUseCase.execute().collectSafe { url ->
                    setLoginUrl(url)
                }
            }

            setDebugging(BuildConfig.DEBUG)
            setOnSingleSignOnListener(object : SingleSignOnListener {
                override fun onCompleted() {
                    loadUserData()
                }

                override fun onRevoke() {
                    logoutSuccess.onNext(true)
                }
            })
            setOnAuthAnalyticsListener(object : AuthAnalyticsListener {
                override fun onReceiveScreen(screens: Screens) {
                    sendScreens.onNext(screens)
                }

                override fun onReceiveEvent(events: Events) {
                    sendEvents.onNext(events)
                }

                override fun onReceiveTrackFirebase(analyticsFirebaseModel: AnalyticFirebaseModel) {
                    analyticsFirebaseModel.apply {
                        this.webViewVersion = applicationPackageManager.getPackageVersion(
                            context,
                            ApplicationPackageName.SystemWebView.appName,
                        )
                    }
                    sendTrackFirebase.onNext(analyticsFirebaseModel)
                }

                override fun onReceiveStateRefreshToken(refreshTokenModel: TrackRefreshTokenModel) {
                    sendTrackStateRefreshToken.onNext(refreshTokenModel.event)
                }
            })
            setAuthLogTree(object : AuthLogTree {
                override fun recordHandledException(
                    exceptionToHandle: Exception,
                    exceptionAttributes: Map<String, Any>,
                ) {
                    NewRelic.recordHandledException(exceptionToHandle, exceptionAttributes)
                }
            })
        }
    }

    fun login(
        authLoginListener: AuthLoginListener? = null,
        requireDialogLoading: Boolean = true,
        isOpenByPass: Boolean = false,
        isForceLogin: Boolean = false,
        configCloseButton: Boolean = true
    ) {
        this.requireDialogLoading = requireDialogLoading
        TrueIdSdk.customSDKSettings.isHiddenCloseLoginButton = configCloseButton.not()
        launchSafe {
            getSystemWebViewMinimumVersionUseCase.execute()
                .onStart {
                    authProgressDialog?.show()
                }.catch {
                    authProgressDialog?.dismissSafe()
                    openAuthWebView(
                        OpenFunction.LOGIN,
                        authLoginListener,
                        isOpenByPass,
                        isForceLogin
                    )
                }
                .collectSafe { minVersion ->
                    authProgressDialog?.dismissSafe()
                    if (isForceLogin.not()) {
                        sendTrackEventFirebase.onNext("login_begin")
                    }
                    context.let {
                        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(it)
                        if (webViewPackageInfo?.versionName?.compareVersionTo(minVersion)
                            == CompareVersion.LESS_THAN
                        ) {
                            AlertDialog.Builder(it).apply {
                                setTitle(it.getString(R.string.webview_warning_dialog_title))
                                setMessage(
                                    it.getString(R.string.webview_warning_dialog_message) +
                                        "(${webViewPackageInfo.versionName})",
                                )
                                setPositiveButton(
                                    it.getString(R.string.webview_warning_dialog_btn_agree),
                                ) { dialog, _ ->
                                    dialog.dismissSafe()
                                    intentPlayStore()
                                }
                            }.create().show()
                        } else {
                            openAuthWebView(
                                OpenFunction.LOGIN,
                                authLoginListener,
                                isOpenByPass,
                                isForceLogin
                            )
                        }
                    }
                }
        }
    }

    fun register(
        authLoginListener: AuthLoginListener? = null,
        requireDialogLoading: Boolean = true,
    ) {
        this.requireDialogLoading = requireDialogLoading
        openAuthWebView(OpenFunction.REGISTER, authLoginListener, false)
    }

    private fun openAuthWebView(
        openFunction: OpenFunction,
        authLoginListener: AuthLoginListener?,
        isOpenByPass: Boolean,
        isForceLogin: Boolean = false
    ) {
        this.authLoginListener = authLoginListener
        setOpenByPass(isOpenByPass)

        val authActionListener = object : AuthActionListener {
            override fun onLoginSuccess() {
                if (requireDialogLoading.not()) {
                    authLoginListener?.onLoginSuccess()
                    this@AuthManagerWrapper.authLoginListener = null
                    setOpenByPass(false)
                }
                if (isForceLogin) {
                    sendTrackEventFirebase.onNext("force_login_success")
                } else {
                    sendTrackEventFirebase.onNext(LOGIN)
                }
                loadUserData()
            }

            override fun onLoginError(message: String?) {
                showLoginError(message)
                onLoginFlowFailure()
            }

            override fun onLoginCancel() {
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        authLoginListener?.onLoginCancel()
                        this@AuthManagerWrapper.authLoginListener = null
                        setOpenByPass(false)
                    },
                    DELAYED_LOGIN_CANCEL,
                )
            }
        }
        AuthManager.openAuthWebView(openFunction, authActionListener)
        AuthManager.login(authActionListener, socialLoginService)
    }

    fun openMappingWebView(identificationListener: IdentificationListener) {
        AuthManager.openMappingWebView(identificationListener = identificationListener)
    }

    fun isLoggedIn(): Boolean {
        return AuthManager.isLoggedIn()
    }

    fun getAccessToken(): String? {
        return AuthManager.getAccessToken()
    }

    fun getAccessToken(accessTokenListener: AccessTokenListener) {
        AuthManager.getAccessToken(accessTokenListener = accessTokenListener)
    }

    fun getProfileCache(): ProfileModel? {
        return AuthManager.getProfileCache()
    }

    fun forceRefreshToken(key: String, refreshTokenListener: RefreshTokenListener? = null) {
        AuthManager.forceRefreshToken(
            key = key,
            object : RefreshTokenListener {
                override fun onSuccess() {
                    loadUserData()
                    refreshTokenListener?.onSuccess()
                }

                override fun onError(message: String?) {
                    refreshTokenListener?.onError(message)
                }
            },
        )
    }

    fun loginByQrCode(qrCodeLoginListener: QrCodeLoginListener) {
        AuthManager.loginByQrCode(qrCodeLoginListener = qrCodeLoginListener)
    }

    fun sendQrData(jsonData: String, qrCodeLoginListener: QrCodeLoginListener? = null) {
        AuthManager.sendQrData(jsonData = jsonData, qrCodeLoginListener = qrCodeLoginListener)
    }

    fun bindingTrueMoney(bindingTrueMoneyListener: BindingTrueMoneyListener) {
        AuthManager.bindingTrueMoney(bindingTrueMoneyListener = bindingTrueMoneyListener)
    }

    fun logout(forceRestart: Boolean = true, isClearEasyLogin: Boolean = false) {
        AuthManager.logout(object : LogoutListener {
            override fun onCompleted() {
                if (isClearEasyLogin) {
                    clearEasyLogin()
                }
                AppsFlyerLib.getInstance().setCustomerUserId("")

                (socialLoginService as? SocialLoginServiceImpl)?.provideGoogleSignInClient()
                    ?.signOut()

                TrueIdSdk.customSDKSettings.isHiddenCloseLoginButton = false
                logoutSuccess.onNext(forceRestart)
            }
        })
    }

    fun clearEasyLogin() {
        AuthManager.clearEasyLogin()
    }

    fun setOpenByPass(isByPass: Boolean) {
        this.isOpenByPass = isByPass
    }

    fun isOpenByPass(): Boolean {
        return isOpenByPass
    }

    fun closeAuth() {
        AuthManager.closeAuth()
    }

    fun onGetProfile(): Observable<ProfileModel> {
        return getProfileSuccess
    }

    fun onFinishFlowLogin(): Observable<Boolean> {
        return finishFlowLogin
    }

    fun onLogoutSuccess(): Observable<Boolean> {
        return logoutSuccess
    }

    fun onAllowRegister(): Observable<Unit> {
        return allowRegister
    }

    fun onLoginFlowSuccess() {
        authProgressDialog?.dismissSafe()
        this.authLoginListener?.onLoginSuccess()
        this.authLoginListener = null
        this.isOpenByPass = false
        finishFlowLogin.onNext(true)
    }

    fun onLoginFlowFailure() {
        authProgressDialog?.dismissSafe()
        this.authLoginListener?.onLoginError()
        this.authLoginListener = null
        this.isOpenByPass = false
        finishFlowLogin.onNext(false)
    }

    fun onReceiveScreens(): Observable<Screens> {
        return sendScreens
    }

    fun onReceiveEvents(): Observable<Events> {
        return sendEvents
    }

    fun onReceiveTrackEventFirebase(): Observable<String> {
        return sendTrackEventFirebase
    }

    fun onReceiveTrackFirebase(): Observable<AnalyticFirebaseModel> {
        return sendTrackFirebase
    }

    fun onReceiveStateRefreshToken(): Observable<String> {
        return sendTrackStateRefreshToken
    }

    private fun loadUserData() {
        val profileListener = object : ProfileListener {
            override fun onSuccess(model: ProfileModel) {
                AppsFlyerLib.getInstance().setCustomerUserId(model.payload?.sub.orEmpty())
                getProfileSuccess.onNext(model)
            }

            override fun onError(message: String?) {
                showLoginError(message)
                logout()
                authLoginListener?.onLoginError()
                val handlingExceptionMap = mapOf(
                    "Key" to "AuthManagerWrapper.loadUserData()",
                    "Value" to "Problem with get profile user",
                )
                NewRelic.recordHandledException(
                    Exception("AuthModule(getProfile)"),
                    handlingExceptionMap,
                )
            }
        }

        MIBusProvider.bus.post(SendAuthStatusChange())
        AuthManager.getProfile(profileListener)
        if (requireDialogLoading) authProgressDialog?.show()
    }

    private fun showLoginError(message: String?) {
        Toast.makeText(
            contextDataProvider.getDataContext(),
            "Login Error -> $message",
            Toast.LENGTH_LONG,
        )
            .show()
    }

    private fun intentPlayStore() {
        contextDataProvider.getDataContext().startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://play.google.com/store/apps/details?id=" +
                        ApplicationPackageName.SystemWebView.appName,
                ),
            ),
        )
    }
}
