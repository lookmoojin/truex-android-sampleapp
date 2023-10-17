package com.tdg.truex_android_sampleapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tdg.login.base.CoroutineDispatcherProvider
import com.tdg.login.base.collectSafe
import com.tdg.login.base.launchSafe
import com.tdg.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _onLoginSuccess = MutableLiveData<String>()
    val onLoginSuccess: LiveData<String>
        get() = _onLoginSuccess

    private val _onLoginFailed = MutableLiveData<String>()
    val onLoginFailed: LiveData<String>
        get() = _onLoginFailed

    fun login(username: String, password: String, isPreProd: Boolean) {
        viewModelScope.launchSafe {
            loginUseCase.execute(username, password, isPreProd)
                .flowOn(coroutineDispatcher.io())
                .catch { error ->
                    _onLoginFailed.value = error.message
                }
                .collectSafe { (accessToken, refreshToken) ->
                    _onLoginSuccess.value =
                        "access token = $accessToken${"\n"}refresh token = $refreshToken"
                }
        }
    }
}