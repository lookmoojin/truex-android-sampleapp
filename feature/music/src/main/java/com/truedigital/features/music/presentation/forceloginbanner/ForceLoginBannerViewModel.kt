package com.truedigital.features.music.presentation.forceloginbanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.forceloginbanner.usecase.GetLoginBannerUseCase
import com.truedigital.features.music.domain.geoblock.usecase.GetMusicGeoBlockUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class ForceLoginBannerViewModel @Inject constructor(
    private val getLoginBannerUseCase: GetLoginBannerUseCase,
    private val userRepository: UserRepository,
    private val loginManagerInterface: LoginManagerInterface,
    private val getMusicGeoBlockUseCase: GetMusicGeoBlockUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider
) : ScopedViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val showLoading = SingleLiveEvent<Unit>()
    private val loginTunedMusic = SingleLiveEvent<Pair<Int, String>>()
    private val showLoginErrorToast = SingleLiveEvent<Unit>()
    private val navigateToMusic = SingleLiveEvent<Unit>()
    private val loadForceLoginThumb = SingleLiveEvent<String>()
    private val displayForceLogin = SingleLiveEvent<Unit>()
    private val displayGeoBlock = SingleLiveEvent<Unit>()

    fun onShowLoading(): LiveData<Unit> = showLoading
    fun onLoginTunedMusic(): LiveData<Pair<Int, String>> = loginTunedMusic
    fun onShowLoginErrorToast(): LiveData<Unit> = showLoginErrorToast
    fun onNavigateToMusic(): LiveData<Unit> = navigateToMusic
    fun onLoadForceLoginThumb(): LiveData<String> = loadForceLoginThumb
    fun onDisplayForceLogin(): LiveData<Unit> = displayForceLogin
    fun onDisplayGeoBlock(): LiveData<Unit> = displayGeoBlock

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun authentication() {
        if (loginManagerInterface.isLoggedIn()) {
            verifyGeoBlock()
        } else {
            getLoginBanner()
        }
    }

    fun handlerAuthentication(isAlreadyLoggedIn: Boolean) {
        if (isAlreadyLoggedIn) {
            navigateToMusicLanding()
        } else {
            authentication()
        }
    }

    private fun getLoginBanner() = viewModelScope.launchSafe {
        getLoginBannerUseCase.execute()
            .onCompletion {
                displayForceLogin.value = Unit
            }
            .catch {
                loadForceLoginThumb.value = ""
            }.collectSafe {
                loadForceLoginThumb.value = it
            }
    }

    fun navigateToLogin() {
        loginManagerInterface.login(
            object : AuthLoginListener() {
                override fun onLoginSuccess() {
                    verifyGeoBlock()
                }

                override fun onLoginError() {
                    showLoginErrorToast.value = Unit
                }
            },
            false
        )
    }

    fun navigateToMusicLanding() {
        navigateToMusic.value = Unit
    }

    private fun getAccessToken() {
        showLoading.value = Unit

        val accessToken = userRepository.getAccessToken()
        if (accessToken.isNotEmpty()) {
            val userId: Int = userRepository.getSsoId().toInt()
            loginTunedMusic.value = Pair(userId, accessToken)
        } else {
            showLoginErrorToast.value = Unit
            displayForceLogin.value = Unit
        }
    }

    private fun verifyGeoBlock() {
        viewModelScope.launchSafe {
            getMusicGeoBlockUseCase.execute()
                .flowOn(coroutineDispatcher.io())
                .collectSafe { isMusicGeoBlock ->
                    if (isMusicGeoBlock) {
                        displayGeoBlock.value = Unit
                    } else {
                        getAccessToken()
                    }
                }
        }
    }
}
